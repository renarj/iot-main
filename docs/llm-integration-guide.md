# LLM Integration Guide

This guide gives LLM agents the stable concepts needed to add future robots, devices, plugins, schemas, commands, or state-producing capabilities.

## Core Mental Model

The backend separates device metadata, command dispatch, and observed state.

1. `thing-svc` stores what exists and how it is configured.
2. `command-svc` accepts commands and publishes them to the command topic.
3. Device controllers, robot adapters, or edge services consume commands, perform hardware work, and publish state messages.
4. `state-svc` consumes state messages, keeps the latest values per thing, optionally writes numeric values to InfluxDB, and publishes live updates over STOMP.

The API uses these identifiers consistently:

- `controllerId`: runtime owner of devices, usually a controller, edge node, robot runtime, or integration instance.
- `pluginId`: capability namespace, for example a robot adapter, Hue integration, train adapter, or device family.
- `schemaId`: type contract inside a plugin namespace.
- `thingId`: runtime identifier of a concrete device, robot component, logical capability, or installed plugin thing.
- `parentId`: hierarchy link. A thing can belong to a controller, plugin, or another thing depending on its schema.

## Dynamic Configuration Model

Dynamic configuration is built from plugins, schemas, controllers, and things.

### Plugin

A plugin declares a capability namespace. It is stored through `thing-svc` system endpoints.

Minimum JSON:

```json
{
  "pluginId": "robot-arm",
  "friendlyName": "Robot Arm"
}
```

### Schema

A schema defines a type of thing a plugin can create.

Important fields:

- `pluginId`: plugin namespace that owns the schema.
- `schemaId`: schema key.
- `type`: runtime type label used for filtering.
- `parentType`: required parent relation. Valid base values are `Controller` and `Plugin`; otherwise it must reference another schema in the same plugin.
- `properties`: configurable fields. `LINK` fields reference another thing by `thingId`.
- `attributes`: supported measurable or controllable attributes.

Supported `TemplateFieldType` values:

- `TEXT`
- `LINK`
- `STATIC_DEFAULT`
- `ENUM`
- `DYNAMIC`

Supported `AttributeType` values:

- `SWITCH`
- `POWER`
- `TEMPERATURE`
- `LABEL`
- `DEGREES`
- `ABS_POSITION`
- `VELOCITY`
- `VOLTAGE`

Example robot schema:

```json
{
  "pluginId": "robot-arm",
  "schemaId": "ServoJoint",
  "type": "ServoJoint",
  "parentType": "Plugin",
  "properties": {
    "busId": { "fieldType": "TEXT", "defaultValue": "" },
    "jointRole": { "fieldType": "ENUM", "defaultValue": "shoulder,elbow,wrist" }
  },
  "attributes": {
    "position": "DEGREES",
    "temperature": "TEMPERATURE",
    "voltage": "VOLTAGE"
  }
}
```

### Controller

A controller represents a runtime endpoint that owns things. It must exist before creating things under it.

```json
{
  "controllerId": "robot-01",
  "properties": {
    "host": "robot-01.local",
    "model": "arm-v1"
  }
}
```

### Installed Plugin Thing

Installing a plugin on a controller creates a thing with:

- `thingId = pluginId`
- `type = Plugin`
- `schemaId = Plugin`
- `parentId = controllerId`

This gives real device things a plugin parent to attach to when their schema requires `parentType = Plugin`.

### Thing

A thing is a concrete device, robot component, logical capability, or adapter-managed resource.

Required by the REST validation path:

- `controllerId`
- `thingId`
- `friendlyName`
- `pluginId`
- `parentId`
- non-null `properties`

The manager also validates that:

- the controller exists;
- the plugin exists for installed plugin creation;
- a non-`Plugin` schema exists if `schemaId` is set;
- the parent relation matches the schema `parentType`.

Example thing:

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
    "busId": "12",
    "jointRole": "elbow"
  },
  "attributes": {}
}
```

When a thing is created or updated, `thing-svc` publishes a `CONFIG_UPDATE` command to the command topic so the owning controller can reload configuration.

## Command Flow

Commands are represented by `BasicCommandImpl`.

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

Supported command types:

- `SWITCH`
- `VALUE`
- `CONFIG_UPDATE`
- `SET_STATE`

Command flow:

1. POST to `command-svc` at `/api/command/`.
2. The service publishes the command on the local event bus.
3. `BasicCommandHandler` serializes the command as JSON.
4. The JSON is published to RabbitMQ on `command.producer.topic`.

## State Flow

Controllers and integrations publish state messages to the state topic as `ValueTransportMessage`.

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

State ingestion behavior:

- `state-svc` consumes JSON messages from `states.consumer.topic`.
- Each value is stored in memory under `controllerId + "-" + thingId`.
- The service also updates `lastSeen` with the current epoch time in milliseconds.
- If a value changes, a `StateUpdateEvent` is published locally.
- The WebSocket controller forwards the full state to `/topic/state`.
- Configured `StateStore` implementations receive changed values. The current InfluxDB store writes numeric values only.

## Integration Workflow For New Robot Or Device Capability

1. Create or update the plugin metadata in `thing-svc`.
2. Create schemas for each concrete capability or component.
3. Register the controller.
4. Install the plugin on the controller.
5. Create things under the installed plugin or other schema-required parents.
6. Have the runtime consume command-topic JSON commands.
7. Have the runtime publish state-topic `ValueTransportMessage` JSON.
8. Use `state-svc` REST or STOMP to observe resulting state.

## Agent Guidance

When extending the platform:

- Treat `thing-svc` as the source of truth for configuration and relationships.
- Do not bypass schema validation unless adding a deliberate migration path.
- Preserve the `CONFIG_UPDATE` command on thing updates; controller runtimes depend on it.
- Keep state messages attribute-oriented: one `ValueTransportMessage` can contain multiple attributes for the same thing.
- Use existing enums for command types, field types, value types, and attributes before adding new values.
- If adding an attribute type or command type, update both model enums and these docs.
- If adding endpoints, update `docs/api/backend-services-api.md`, `docs/api/backend-services-openapi.yaml`, and `docs/agents-descriptor.json`.

