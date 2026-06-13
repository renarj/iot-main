# command-svc

`command-svc` is the command ingress service. It accepts JSON commands over REST and republishes them to RabbitMQ for controllers, integrations, robots, or edge services to consume.

## Module

- Source: `backend/command-svc`
- Main class: `com.oberasoftware.iot.command.CommandContainer`
- REST controller: `com.oberasoftware.iot.command.CommandRestSvc`
- Event handler: `com.oberasoftware.iot.command.BasicCommandHandler`
- Shared command model: `com.oberasoftware.iot.core.commands.impl.BasicCommandImpl`

## Runtime Behavior

Startup imports:

- `QueueConfiguration`
- `BaseConfiguration`
- `CoreConfiguation`

On startup, `CommandContainer` connects `RabbitMQTopicSender` and registers a shutdown hook to close it.

Command dispatch path:

1. `POST /api/command/` receives a `BasicCommandImpl`.
2. `CommandRestSvc` publishes the command to the local event bus.
3. `BasicCommandHandler.receive(BasicCommand)` receives the event.
4. The handler serializes the command with `ConverterHelper.mapToJson`.
5. The handler publishes the JSON to `command.producer.topic`.

## Configuration

Default resource: `backend/command-svc/src/main/resources/application.properties`

Important properties:

- `server.port=9004`
- `command.producer.topic=commands` in the `dev` profile.
- `rmq.host` and `rmq.port` configure RabbitMQ in the `dev` profile.

Note: the file also contains `amq.producer.topic=commands`; the active handler reads `command.producer.topic`.

## Command Payload

`BasicCommandImpl` fields:

- `controllerId`: target controller runtime.
- `thingId`: target thing under that controller.
- `commandType`: one of `SWITCH`, `VALUE`, `CONFIG_UPDATE`, `SET_STATE`.
- `attributes`: string map carrying command-specific arguments.

Example:

```json
{
  "controllerId": "robot-01",
  "thingId": "left-elbow-servo",
  "commandType": "VALUE",
  "attributes": {
    "position": "45"
  }
}
```

## API

See [backend services API](../api/backend-services-api.md#command-svc).

