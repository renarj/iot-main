package com.oberasoftware.iot.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.oberasoftware.iot.core.client.AgentClient;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.Controller;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.Plugin;
import com.oberasoftware.iot.core.model.ThingSchema;
import com.oberasoftware.iot.core.model.storage.RuleItem;
import com.oberasoftware.iot.core.model.storage.impl.*;
import com.oberasoftware.iot.core.util.HttpUtils;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class DefaultAgentClient implements AgentClient {
    private static final Logger LOG = getLogger( DefaultAgentClient.class );


    private HttpClient client;

    @Value("${thing-svc.baseUrl:}")
    private String baseUrl;

    @Value("${thing-svc.apiToken:}")
    private String apiToken;

    @PostConstruct
    public void postConstruct() {
        this.client = HttpUtils.createClient(false);
    }

    @Override
    public void configure(String baseUrl, String apiToken) {
        this.baseUrl = baseUrl;
        this.apiToken = apiToken;
    }

    @Override
    public IotThing createOrUpdate(IotThing thing) throws IOTException {
        try {
            var response = post(UriBuilder.create(baseUrl).resource("controllers", thing.getControllerId()).resource("things"), thing);
            return handlePostResponse(response, IotThingImpl.class);
        } catch (IOException | InterruptedException e) {
            throw new IOTException("Unable to create Thing", e);
        }
    }

    @Override
    public Controller createOrUpdate(Controller controller) throws IOTException {
        try {
            var response = post(UriBuilder.create(baseUrl).resource("controllers", controller.getControllerId()), controller);
            return handlePostResponse(response, ControllerImpl.class);
        } catch (IOException | InterruptedException e) {
            throw new IOTException("Unable to create controller", e);
        }
    }

    @Override
    public Plugin createOrUpdatePlugin(Plugin plugin) throws IOTException {
        try {
            var response = post(UriBuilder.create(baseUrl).resource("system").resource("plugins"), plugin);
            return handlePostResponse(response, PluginImpl.class);
        } catch (IOException | InterruptedException e) {
            throw new IOTException("Unable to create Plugin", e);
        }
    }

    @Override
    public ThingSchema createOrUpdateThingSchema(ThingSchema schema) throws IOTException {
        try {
            var response = post(UriBuilder.create(baseUrl).resource("system").resource("schemas"), schema);
            return handlePostResponse(response, ThingSchemaImpl.class);
        } catch (IOException | InterruptedException e) {
            throw new IOTException("Unable to create Schema", e);
        }
    }

    private <T> T handlePostResponse(HttpResponse<String> postResponse, Class<T> type) throws IOException, IOTException {
        if(postResponse.statusCode() == HttpStatus.CREATED.value()) {
            ObjectMapper mapper = new ObjectMapper();
            var c = mapper.readValue(postResponse.body(), type);
            LOG.info("{} was created: {}", type.getName(), c);
            return c;
        } else {
            throw new IOTException("Unable to create " + type.getName() + ", error code: " + postResponse.statusCode());
        }
    }

    private HttpResponse<String> post(UriBuilder uriBuilder, Object postObject) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(postObject);
        LOG.debug("Creating: {}", json);

        var request = HttpRequest.newBuilder()
                .uri(uriBuilder.build())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .headers("Content-Type", "application/json")
                .build();
        LOG.info("Doing HTTP Request: {}", request);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public boolean remove(String controllerId, String thingId) throws IOTException {
        var request = HttpRequest.newBuilder()
                .uri(UriBuilder.create(baseUrl)
                        .resource("controllers", controllerId)
                        .resource("things", thingId)
                        .build())
                .DELETE()
                .headers("Content-Type", "application/json")
                .build();
        LOG.info("Doing HTTP Request: {}", request);

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpStatus.ACCEPTED.value()) {
                LOG.info("Succesfully removed thing: {} on controller: {}", thingId, controllerId);
                return true;
            } else {
                LOG.warn("Could not remove thing: {} on controller: {}", thingId, controllerId);
                return false;
            }
        } catch (IOException | InterruptedException e) {
            throw new IOTException("Unable to remove thing", e);
        }
    }

    @Override
    public List<Controller> getControllers() throws IOTException {
        var request = HttpRequest.newBuilder()
                .uri(UriBuilder.create(baseUrl).resource("controllers").build())
                .build();
        LOG.info("Doing HTTP Request to retrieve controllers: {}", request);

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            var body = response.body();
            ObjectMapper mapper = new ObjectMapper();
            ControllerImpl[] controllers = mapper.readValue(body, ControllerImpl[].class);

            return Lists.newArrayList(controllers);
        } catch (IOException | InterruptedException e) {
            throw new IOTException("Unable to request Controllers from service", e);
        }
    }

    @Override
    public Optional<IotThing> getThing(String controllerId, String thingId) throws IOTException {
        var request = HttpRequest.newBuilder()
                .uri(UriBuilder.create(baseUrl).resource("controllers", controllerId).resource("things", thingId).build())
                .build();
        LOG.info("Doing HTTP Request: {}", request);
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == HttpStatus.OK.value()) {
                var body = response.body();
                ObjectMapper mapper = new ObjectMapper();
                IotThingImpl thing = mapper.readValue(body, IotThingImpl.class);

                LOG.info("Found thing: {}", thing);
                return Optional.of(thing);
            } else {
                return Optional.empty();
            }
        } catch (IOException | InterruptedException e) {
            throw new IOTException("Unable to request things from service", e);
        }
    }

    @Override
    public Optional<Controller> getController(String controllerId) throws IOTException {
        var request = HttpRequest.newBuilder()
                .uri(UriBuilder.create(baseUrl).resource("controllers", controllerId).build())
                .build();
        LOG.info("Doing Controller HTTP Request: {}", request);
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == HttpStatus.OK.value()) {
                var body = response.body();
                ObjectMapper mapper = new ObjectMapper();
                Controller controller = mapper.readValue(body, ControllerImpl.class);

                LOG.info("Found Controller: {}", controller);
                return Optional.of(controller);
            } else {
                return Optional.empty();
            }
        } catch (IOException | InterruptedException e) {
            throw new IOTException("Unable to request Controller from service", e);
        }
    }

    @Override
    public List<IotThing> getThings(String controllerId) throws IOTException {
        var request = HttpRequest.newBuilder()
                .uri(UriBuilder.create(baseUrl).resource("controllers", controllerId).resource("things").build())
                .build();
        LOG.info("Doing HTTP Request to retrieve things for controller: {} request: {}", controllerId, request);
        return doThingListRequest(request);
    }

    @Override
    public List<IotThing> getThings(String controllerId, String pluginId) throws IOTException {
        var request = HttpRequest.newBuilder()
                .uri(UriBuilder.create(baseUrl)
                        .resource("controllers", controllerId)
                        .resource("plugins", pluginId)
                        .resource("things")
                        .build())
                .build();
        LOG.info("Doing HTTP Request to retrieve things for controller: {} and plugin: {} request: {}", controllerId, pluginId, request);

        return doThingListRequest(request);
    }

    @Override
    public List<IotThing> getChildren(String controllerId, String thingId) throws IOTException {
        var request = HttpRequest.newBuilder()
                .uri(UriBuilder.create(baseUrl)
                        .resource("controllers", controllerId)
                        .resource("things", thingId)
                        .resource("children")
                        .build())
                .build();
        LOG.info("Doing HTTP Request to retrieve children for controller: {} and thing: {} request: {}", controllerId, thingId, request);

        return doThingListRequest(request);
    }

    @Override
    public List<IotThing> getChildren(String controllerId, String thingId, String type) throws IOTException {
        var request = HttpRequest.newBuilder()
                .uri(UriBuilder.create(baseUrl)
                        .resource("controllers", controllerId)
                        .resource("things", thingId)
                        .resource("children")
                        .param("type", type)
                        .build())
                .build();
        LOG.info("Doing HTTP Request to retrieve children for controller: {} and thing: {} request: {} of type: {}", controllerId, thingId, request, type);

        return doThingListRequest(request);    }

    @Override
    public List<IotThing> getThings(String controllerId, String pluginId, String type) throws IOTException {
        var request = HttpRequest.newBuilder()
                .uri(UriBuilder.create(baseUrl)
                        .resource("controllers", controllerId)
                        .resource("plugins", pluginId)
                        .resource("things")
                        .param("type", type)
                        .build())
                .build();
        LOG.info("Doing HTTP Request to retrieve things for controller: {} and plugin: {} request: {}", controllerId, pluginId, request);

        return doThingListRequest(request);
    }

    @Override
    public List<RuleItem> getRules(String controllerId) throws IOTException {
        var request = HttpRequest.newBuilder()
                .uri(UriBuilder.create(baseUrl)
                        .resource("rules")
                        .resource("controller", controllerId)
                        .build())
                .build();
        LOG.info("Doing HTTP Request to retrieve Rules for controller: {} request: {}", controllerId, request);

        return internalRequest(request, (Function<String, List<RuleItem>>) s -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                RuleItemImpl[] things = mapper.readValue(s, RuleItemImpl[].class);
                return Lists.newArrayList(things);
            } catch (JsonProcessingException e) {
                return Lists.newArrayList();
            }
        });
    }

    @Override
    public List<Plugin> getPlugins() throws IOTException {
        var request = HttpRequest.newBuilder()
                .uri(UriBuilder.create(baseUrl)
                        .resource("system")
                        .resource("plugins")
                        .build())
                .build();
        LOG.info("Doing HTTP Request to retrieve Plugins request: {}", request);

        return internalRequest(request, (Function<String, List<Plugin>>) s -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                PluginImpl[] things = mapper.readValue(s, PluginImpl[].class);
                return Lists.newArrayList(things);
            } catch (JsonProcessingException e) {
                return Lists.newArrayList();
            }
        });
    }

    @Override
    public List<ThingSchema> getSchemas(String pluginId) throws IOTException {
        var request = HttpRequest.newBuilder()
                .uri(UriBuilder.create(baseUrl)
                        .resource("system")
                        .resource("plugins", pluginId)
                        .resource("schemas")
                        .build())
                .build();
        LOG.info("Doing HTTP Request to retrieve Schemas for Plugin: {} request: {}", pluginId, request);

        return internalRequest(request, (Function<String, List<ThingSchema>>) s -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                ThingSchemaImpl[] things = mapper.readValue(s, ThingSchemaImpl[].class);
                return Lists.newArrayList(things);
            } catch (JsonProcessingException e) {
                return Lists.newArrayList();
            }
        });
    }

    private <R> R internalRequest(HttpRequest request, Function<String, R> f) throws IOTException {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            var body = response.body();
            return f.apply(body);
        } catch (IOException | InterruptedException e) {
            throw new IOTException("Unable to request rules from service", e);
        }
    }

    private List<IotThing> doThingListRequest(HttpRequest request) throws IOTException {
        return internalRequest(request, (Function<String, List<IotThing>>) s -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                IotThingImpl[] things = mapper.readValue(s, IotThingImpl[].class);
                return Lists.newArrayList(things);
            } catch (JsonProcessingException e) {
                return Lists.newArrayList();
            }
        });
    }
}
