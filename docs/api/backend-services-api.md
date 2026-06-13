# Backend Services API

This API reference was generated from the Spring MVC controller mappings in:

- `backend/command-svc/src/main/java/com/oberasoftware/iot/command/CommandRestSvc.java`
- `backend/state-svc/src/main/java/com/oberasoftware/home/core/state/StateRestSvc.java`
- `backend/thing-svc/src/main/java/com/oberasoftware/home/data/*Rest*.java`

The services use OData-like path segments such as `/controllers({controllerId})`.

Some read-only controller methods use `@RequestMapping` without an explicit HTTP method. This reference documents those as `GET` because the Java methods are read operations, but Spring will accept more methods unless the controller mapping is tightened.

## command-svc

Default port: `9004`

### POST `/api/command/`

Publishes a command to the configured command topic.

Request body:

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

Response: the submitted command body.

Command types:

- `SWITCH`
- `VALUE`
- `CONFIG_UPDATE`
- `SET_STATE`

## state-svc

Default port: `9006`

### GET `/api/state/controllers({controllerId})/things({thingId})`

Returns the latest state for a single thing.

Responses:

- `200`: state found.
- `404`: state has not been observed.

Example response:

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
    }
  ]
}
```

### GET `/api/state/controllers({controllerId})`

Returns all latest states currently held in memory for a controller.

Response shape: JSON object keyed by the internal key format `controllerId-thingId`.

```json
{
  "robot-01-left-elbow-servo": {
    "itemId": "left-elbow-servo",
    "controllerId": "robot-01",
    "stateItems": []
  }
}
```

### STOMP `/ws` and `/topic/state`

SockJS/STOMP endpoint: `/ws`

Subscribe to `/topic/state` for full `State` objects whenever an attribute changes.

## thing-svc

Default port: `9010`

### Controllers And Things

#### GET `/api/controllers`

Returns all controllers.

#### GET `/api/controllers({controllerId})`

Returns one controller.

Responses:

- `200`: controller found.
- `404`: controller not found.

#### POST `/api/controllers({controllerId})`

Creates or updates a controller.

The path `controllerId` must match the request body's `controllerId`.

Request:

```json
{
  "controllerId": "robot-01",
  "properties": {
    "host": "robot-01.local"
  }
}
```

Responses:

- `201`: created or updated.
- `400`: invalid or mismatched `controllerId`.

#### DELETE `/api/controllers({controllerId})`

Deletes a controller if it exists and has no things.

Responses:

- `202`: deleted.
- `404`: not found.
- `500`: may occur when dependent things still exist.

#### GET `/api/controllers({controllerId})/things`

Returns all things owned by a controller.

#### POST `/api/controllers({controllerId})/things`

Creates or updates a thing.

The path `controllerId` must match the request body's `controllerId`.

Request:

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

Responses:

- `201`: created or updated.
- `400`: invalid thing, missing fields, schema error, or mismatched controller.

Side effect: publishes a `CONFIG_UPDATE` command to the command topic.

#### GET `/api/controllers({controllerId})/things({thingId})`

Returns a single thing.

Responses:

- `200`: thing found.
- `404`: thing not found.

#### DELETE `/api/controllers({controllerId})/things({thingId})`

Deletes a thing if it has no children.

Responses:

- `202`: deleted.
- `404`: not found or dependent children exist.

#### GET `/api/controllers({controllerId})/plugins`

Returns things on the controller with `type = Plugin`.

#### POST `/api/controllers({controllerId})/plugins`

Installs a plugin on a controller by creating or updating the plugin thing.

Request:

```json
{
  "pluginId": "robot-arm"
}
```

Responses:

- `201`: installed.
- `400`: missing controller or plugin id.
- `500`: plugin metadata does not exist.

#### GET `/api/controllers({controllerId})/plugins({pluginId})/things`

Returns things on a controller for a plugin.

Optional query:

- `type`: filters by thing type.

Example: `/api/controllers(robot-01)/plugins(robot-arm)/things?type=ServoJoint`

#### GET `/api/controllers({controllerId})/schemas({schemaId})/things`

Returns things on a controller that use a schema.

#### GET `/api/schemas({schemaId})/things`

Returns things across all controllers that use a schema.

#### GET `/api/controllers({controllerId})/things({thingId})/children`

Returns children of a thing.

Optional query:

- `type`: filters child type.

#### GET `/api/controllers({controllerId})/children`

Returns things whose parent is the controller itself.

#### GET `/api/controllers({controllerId})/things({thingId})/linked`

Returns linked things based on `LINK` schema properties.

Optional query:

- `type`: selects link fields whose schema field descriptor `defaultValue` equals the requested type.

### System Metadata

#### GET `/api/system/plugins`

Returns all plugin metadata.

#### POST `/api/system/plugins`

Creates or updates plugin metadata.

Request:

```json
{
  "pluginId": "robot-arm",
  "friendlyName": "Robot Arm"
}
```

Responses:

- `201`: created or updated.
- `400`: missing `pluginId` or `friendlyName`.

#### DELETE `/api/system/plugins({pluginId})`

Deletes a plugin and first deletes all schemas for that plugin.

Responses:

- `202`: deleted.
- `404`: not found.
- `400`: missing plugin id.

#### GET `/api/system/plugins({pluginId})/schemas`

Returns schemas for a plugin.

#### GET `/api/system/plugins({pluginId})/schemas({schemaId})`

Returns one schema.

Responses:

- `200`: schema found.
- `404`: schema not found.

#### POST `/api/system/schemas`

Creates or updates a schema.

Request:

```json
{
  "pluginId": "robot-arm",
  "schemaId": "ServoJoint",
  "type": "ServoJoint",
  "parentType": "Plugin",
  "properties": {
    "busId": {
      "fieldType": "TEXT",
      "defaultValue": ""
    }
  },
  "attributes": {
    "position": "DEGREES"
  }
}
```

Responses:

- `201`: created or updated.
- `400`: missing `pluginId` or `schemaId`.
- `500`: invalid schema relation or plugin id may surface as an unhandled runtime exception.

#### DELETE `/api/system/plugins({pluginId})/schemas({schemaId})`

Deletes a schema.

Responses:

- `202`: deleted.
- `404`: not found.
- `400`: missing plugin id or schema id.

### Groups

#### GET `/api/groups`

Returns all groups.

#### GET `/api/groups({groupId})`

Returns a group by id.

#### GET `/api/groups/controller({controllerId})`

Returns groups for a controller.

#### GET `/api/groups({groupId})/things`

Returns things referenced by the group's `deviceIds`.

#### POST `/api/groups`

Creates or updates a group.

Request:

```json
{
  "id": "living-room",
  "controllerId": "robot-01",
  "name": "Living Room",
  "deviceIds": ["left-elbow-servo"],
  "properties": {}
}
```

#### DELETE `/api/groups({groupId})`

Deletes a group.

### Rules

#### GET `/api/rules`

Returns all rules.

#### GET `/api/rules/controller({controllerId})`

Returns rules for a controller.

#### POST `/api/rules`

Creates or updates a rule.

Request:

```json
{
  "id": "rule-1",
  "name": "Move arm on trigger",
  "controllerId": "robot-01",
  "blocklyData": "{}",
  "properties": {}
}
```

Responses:

- `200`: stored.
- `400`: rule validation failed.

#### DELETE `/api/rules({ruleId})`

Deletes a rule.
