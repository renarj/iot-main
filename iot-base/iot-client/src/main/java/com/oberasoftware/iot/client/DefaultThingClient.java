package com.oberasoftware.iot.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.oberasoftware.iot.core.client.ThingClient;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.Controller;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.impl.ControllerImpl;
import com.oberasoftware.iot.core.model.storage.impl.IotThingImpl;
import com.oberasoftware.iot.core.util.HttpUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class DefaultThingClient implements ThingClient {
    private static final Logger LOG = getLogger( DefaultThingClient.class );


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
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(thing);
            LOG.info("Creating Thing: {}", json);

            var request = HttpRequest.newBuilder()
                    .uri(UriBuilder.create(baseUrl).resource("controllers", thing.getControllerId()).resource("things").build())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .headers("Content-Type", "application/json")
                    .build();
            LOG.info("Doing HTTP Request: {}", request);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == HttpStatus.CREATED.value()) {
                var t = mapper.readValue(response.body(), IotThingImpl.class);
                LOG.info("Thing was created: {}", t);
                return t;
            } else {
                throw new IOTException("Unable to create Thing, error code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new IOTException("Unable to create Thing", e);
        }
    }

    @Override
    public Controller createOrUpdate(Controller controller) throws IOTException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(controller);
            LOG.info("Creating controller: {}", json);

            var request = HttpRequest.newBuilder()
                    .uri(UriBuilder.create(baseUrl).resource("controllers", controller.getControllerId()).build())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .headers("Content-Type", "application/json")
                    .build();
            LOG.info("Doing HTTP Request: {}", request);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == HttpStatus.CREATED.value()) {
                var c = mapper.readValue(response.body(), ControllerImpl.class);
                LOG.info("Controller was created: {}", c);
                return c;
            } else {
                throw new IOTException("Unable to create controller, error code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new IOTException("Unable to create controller", e);
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
    public List<IotThing> getThings(String controllerId) throws IOTException {
        var request = HttpRequest.newBuilder()
                .uri(UriBuilder.create(baseUrl).resource("controllers", controllerId).resource("things").build())
                .build();
        LOG.info("Doing HTTP Request: {}", request);

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            var body = response.body();
            ObjectMapper mapper = new ObjectMapper();
            IotThingImpl[] things = mapper.readValue(body, IotThingImpl[].class);

            return Lists.newArrayList(things);
        } catch (IOException | InterruptedException e) {
            throw new IOTException("Unable to request things from service", e);
        }
    }
}
