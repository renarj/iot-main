# state-svc

`state-svc` maintains the latest known state for things and broadcasts state changes to WebSocket subscribers.

## Module

- Source: `backend/state-svc`
- Main class: `com.oberasoftware.home.core.state.StateContainer`
- REST controller: `com.oberasoftware.home.core.state.StateRestSvc`
- WebSocket controller: `com.oberasoftware.home.core.state.WebsocketController`
- State manager: `com.oberasoftware.home.core.state.StateManagerImpl`
- Optional time-series store: `com.oberasoftware.home.core.state.InfluxDBStateStore`

## Runtime Behavior

Startup imports:

- `StateConfiguration`
- `BaseConfiguration`
- `QueueConfiguration`
- `CoreConfiguation`

State ingestion path:

1. `StateContainer` connects `RabbitMQTopicListener`.
2. It registers a listener for `states.consumer.topic`.
3. Incoming JSON is deserialized as `ValueTransportMessage`.
4. Each entry in `message.values` calls `StateManager.updateItemState(controllerId, thingId, attribute, value)`.
5. `StateManagerImpl` updates the in-memory state map and writes a `lastSeen` value.
6. If the value changed, `StateUpdateEvent` is published locally and any configured `StateStore` is updated.
7. `WebsocketController` forwards state updates to STOMP destination `/topic/state`.

## Configuration

Default resource: `backend/state-svc/src/main/resources/application.properties`

Important properties:

- `server.port=9006`
- `states.consumer.topic=states`
- `rmq.host` and `rmq.port` configure RabbitMQ in the `dev` profile.
- InfluxDB properties are read by `InfluxDBStateStore`: `influxUrl`, `influxToken`, `influxOrg`, and `bucket`.

## State Model

Inbound state messages use `ValueTransportMessage`.

```json
{
  "controllerId": "robot-01",
  "thingId": "left-elbow-servo",
  "values": {
    "position": { "type": "NUMBER", "value": 45 },
    "temperature": { "type": "DECIMAL", "value": 41.2 }
  }
}
```

Supported value types:

- `BOOLEAN`
- `NUMBER`
- `DECIMAL`
- `STRING`

REST state response shape:

```json
{
  "itemId": "left-elbow-servo",
  "controllerId": "robot-01",
  "stateItems": [
    {
      "attribute": "position",
      "value": {
        "type": "NUMBER",
        "value": 45
      }
    },
    {
      "attribute": "lastSeen",
      "value": {
        "type": "NUMBER",
        "value": 1710000000000
      }
    }
  ]
}
```

Implementation detail: attributes are normalized to lowercase when stored in `StateImpl`.

## WebSocket

`StateConfiguration` enables STOMP over SockJS:

- Endpoint: `/ws`
- Application destination prefix: `/app`
- Simple broker destination prefix: `/topic`
- State update topic: `/topic/state`

The message sent to `/topic/state` is the full `State` object for the thing whose attribute changed.

## API

See [backend services API](../api/backend-services-api.md#state-svc).

