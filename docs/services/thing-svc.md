# thing-svc

`thing-svc` is the source of truth for dynamic IoT configuration. It stores plugins, schemas, controllers, things, groups, and rules.

## Module

- Source: `backend/thing-svc`
- Main class: `com.oberasoftware.home.data.DataServiceContainer`
- Thing controller: `com.oberasoftware.home.data.ThingRestSvc`
- System metadata controller: `com.oberasoftware.home.data.SystemDataRestService`
- Group controller: `com.oberasoftware.home.data.GroupRestSvc`
- Rule controller: `com.oberasoftware.home.data.RulesRestController`
- Thing manager: `com.oberasoftware.home.data.ThingManagerImpl`
- System metadata manager: `com.oberasoftware.home.data.SystemDataManagerImpl`

## Configuration

Default resource: `backend/thing-svc/src/main/resources/application.properties`

Important properties:

- `server.port=9010`
- RabbitMQ command publishing is injected through `RabbitMQTopicSender`; `ThingManagerImpl` reads `command.producer.topic`.
- Storage comes from `iot-base/home-storage`.

## Configuration Entities

### Plugin

Plugin metadata declares a capability namespace.

```json
{
  "pluginId": "robot-arm",
  "friendlyName": "Robot Arm"
}
```

### Schema

Schemas define what a plugin can create.

```json
{
  "pluginId": "robot-arm",
  "schemaId": "ServoJoint",
  "type": "ServoJoint",
  "parentType": "Plugin",
  "properties": {
    "busId": { "fieldType": "TEXT", "defaultValue": "" }
  },
  "attributes": {
    "position": "DEGREES"
  }
}
```

Validation rules in `SystemDataManagerImpl`:

- `parentType`, `type`, `schemaId`, and `pluginId` are required.
- `parentType` may be `Controller`, `Plugin`, or another schema in the same plugin.
- `pluginId` must reference an existing plugin.

### Controller

Controllers own things.

```json
{
  "controllerId": "robot-01",
  "properties": {
    "host": "robot-01.local"
  }
}
```

### Thing

Things are concrete devices, robot components, logical capabilities, or installed plugin instances.

```json
{
  "controllerId": "robot-01",
  "thingId": "left-elbow-servo",
  "friendlyName": "Left Elbow Servo",
  "pluginId": "robot-arm",
  "schemaId": "ServoJoint",
  "type": "ServoJoint",
  "parentId": "robot-arm",
  "properties": {
    "busId": "12"
  },
  "attributes": {}
}
```

Validation rules in `ThingManagerImpl`:

- `thingId`, `controllerId`, and `pluginId` are required.
- The referenced controller must exist.
- If `schemaId` is non-null and not `Plugin`, the schema must exist for the thing's plugin.
- The thing's parent must match the schema's `parentType`.
- A thing with children cannot be deleted.
- A controller with things cannot be deleted.

When a thing is created or updated, `thing-svc` publishes a `CONFIG_UPDATE` `BasicCommandImpl` on the command topic.

## Query Patterns

Common lookups:

- all controllers;
- one controller;
- all things on a controller;
- all things for a plugin, optionally filtered by `type`;
- all things with a schema across one or all controllers;
- children of a controller or thing;
- linked things based on schema properties whose `fieldType` is `LINK`.

Linked lookup behavior:

- `ThingManagerImpl.findLinked` reads the thing's schema.
- It selects schema properties with `fieldType = LINK`.
- The property value on the thing is interpreted as a linked `thingId`.
- Optional `type` query filtering compares against the field descriptor's `defaultValue`.

## Groups And Rules

Groups are stored virtual items that contain `deviceIds` and a `controllerId`.

Rules are stored virtual items containing `name`, `controllerId`, `blocklyData`, and `properties`.

The current group endpoint dereferences each `deviceId` through `ThingManager.findThing(controllerId, deviceId)`.

## API

See [backend services API](../api/backend-services-api.md#thing-svc).

