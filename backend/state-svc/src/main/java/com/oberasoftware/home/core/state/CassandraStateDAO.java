package com.oberasoftware.home.core.state;

import com.datastax.driver.core.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.model.states.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Renze de Vries
 */
//@Component
public class CassandraStateDAO implements StateDAO {
    private static final Logger LOG = LoggerFactory.getLogger(CassandraStateDAO.class);

    @org.springframework.beans.factory.annotation.Value("${cassandra.host}")
    private String cassandraHost;

    @org.springframework.beans.factory.annotation.Value("${cassandra.keyspace:haas}")
    private String keySpace;

    @org.springframework.beans.factory.annotation.Value("${cassandra.table:item_states}")
    private String stateTable;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String INSERT_QUERY = "insert into %s.%s (controller_id, item_id, label, state) values (?,?,?,?);";

    private Session session;

    private PreparedStatement insertStatement;

    private ConcurrentMap<String, PreparedStatement> statementMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void connect() {
        LOG.info("Connecting to cassandra cluster on: {}", cassandraHost);
        Cluster cluster = Cluster.builder()
                .addContactPoint(cassandraHost)
                .build();
        session = cluster.connect();

        session.execute("CREATE KEYSPACE IF NOT EXISTS " + keySpace + " WITH replication " +
                "= {'class':'SimpleStrategy', 'replication_factor':1};");
        String tableCreate = "CREATE TABLE IF NOT EXISTS " + keySpace + "." + stateTable + " (" +
                "controller_id varchar," +
                "item_id varchar," +
                "label varchar," +
                "state varchar," +
                "PRIMARY KEY (controller_id, item_id)" +
                ");";
        LOG.info("Executing table creation: {}", tableCreate);
        session.execute(tableCreate);

        insertStatement = session.prepare(String.format(INSERT_QUERY, keySpace, stateTable));
    }

    @Override
    public State setState(String controllerId, String itemId, String label, Value value) {
        try {
            BoundStatement boundStatement = new BoundStatement(insertStatement);
            boundStatement.bind(controllerId, itemId, label, OBJECT_MAPPER.writeValueAsString(value));

            session.execute(boundStatement);

            return getState(controllerId, itemId);
        } catch (JsonProcessingException e) {
            LOG.error("Could not serialize to json", e);
            throw new RuntimeIOTException("Could not store state", e);
        }
    }

    @Override
    public State getState(String controllerId, String itemId) {
        List<State> states = new QueryExecutor(keySpace, stateTable)
                .controllerId(controllerId)
                .itemId(itemId)
                .execute();
        return states.isEmpty() ? null : states.get(0);
    }

    @Override
    public List<State> getStates(String controllerId) {
        return new QueryExecutor(keySpace, stateTable)
                .controllerId(controllerId).execute();
    }

    private class QueryExecutor {

        private static final String BASE_SELECT = "select * from ";
        private StringBuilder queryBuilder = new StringBuilder(BASE_SELECT);

        private Map<String, Object> params = new HashMap<>();

        private QueryExecutor(String keyspace, String table) {
            queryBuilder.append(keyspace).append(".").append(table);

        }

        public QueryExecutor controllerId(String controllerId) {
            return field("controller_id", controllerId);
        }

        public QueryExecutor itemId(String itemId) {
            return field("item_id", itemId);
        }

        public QueryExecutor field(String field, Object value) {
            this.params.put(field, value);
            return this;
        }

        public List<State> execute() {
            Statement statement = build();
            ResultSet resultSet = session.execute(statement);

            Map<String, List<StateItemImpl>> stateMap = new HashMap<>();
            resultSet.all().forEach(r -> {
                String controllerId = r.get("controller_id", String.class);
                String itemId = r.get("item_id", String.class);
                String label = r.get("label", String.class);
                String state = r.get("state", String.class);

                try {
                    StateItemImpl item = new StateItemImpl(label, OBJECT_MAPPER.readValue(state, ValueImpl.class));
                    String itemKey = controllerId + "_" + itemId;
                    stateMap.putIfAbsent(itemKey, new ArrayList<>());
                    stateMap.get(itemKey).add(item);
                } catch (IOException e) {
                    LOG.error("", e);
                }
            });

            List<State> states = new ArrayList<>();
            stateMap.forEach((k, v) -> {
                String[] idParts = k.split("_");
                String controllerId = idParts[0];
                String itemId = idParts[1];

                var state = new StateImpl(controllerId, itemId);
                v.forEach(si -> state.updateIfChanged(si.getAttribute(), si));
            });
            return states;
        }

        private Statement build() {
            if(!params.isEmpty()) {
                List<Object> values = new ArrayList<>();
                StringBuilder criteriaBuilder = new StringBuilder();
                params.forEach((k, v) -> {
                    if(criteriaBuilder.length() == 0) {
                        criteriaBuilder.append(" where ");
                    } else {
                        criteriaBuilder.append("and ");
                    }
                    criteriaBuilder.append(k).append("=? ");
                    values.add(v);
                });
                queryBuilder.append(criteriaBuilder);
                String query = queryBuilder.toString();

                statementMap.computeIfAbsent(query, s -> session.prepare(s));
                LOG.debug("Executing query: {}", query);
                return new BoundStatement(statementMap.get(query)).bind(values.toArray());
            } else {
                PreparedStatement preparedStatement = session.prepare(queryBuilder.toString());
                return preparedStatement.bind();
            }
        }
    }
}
