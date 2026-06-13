# Robot Model Integration Guide

This guide explains how a robot can configure itself from `thing-svc`, receive commands through `iot-agent`, and emit state events through the base IoT services. It is based on the fully integrated `robot-models/robo-maximus` implementation.

## Architecture

The runtime flow is:

1. `thing-svc` stores the controller, robot plugin thing, robot root thing, component things, schema metadata, and relationships.
2. `iot-agent` starts on the robot controller host and loads robot model jars through Java `ServiceLoader`.
3. The robot model contributes a Spring configuration class and an `AutomationExtension`.
4. The agent registers the controller and extension plugin thing in `thing-svc`.
5. The robot extension reads configured robot things from `thing-svc`.
6. Schema-specific activators convert `IotThing` records into hardware capabilities, sensors, joints, wheels, and behaviours.
7. The agent subscribes to MQTT command topics, converts `BasicCommandImpl` messages into typed commands, and routes them to the extension command handler.
8. Robot hardware and sensors publish local events; the agent forwards `ThingValueEvent` and `ThingMultiValueEvent` to MQTT so `state-svc` can update state.

Relevant source:

- `iot-agent/agent-core/src/main/java/com/oberasoftware/home/agent/core/Agent.java`
- `iot-agent/agent-core/src/main/java/com/oberasoftware/home/agent/core/AgentBootstrapImpl.java`
- `iot-agent/agent-core/src/main/java/com/oberasoftware/home/agent/core/extension/ExtensionManagerImpl.java`
- `robot-models/robo-maximus/src/main/java/com/oberasoftware/robo/maximus/RobotConfiguration.java`
- `robot-models/robo-maximus/src/main/java/com/oberasoftware/robo/maximus/RobotExtension.java`
- `robot-models/robo-maximus/src/main/java/com/oberasoftware/robo/maximus/IotRobotInitializer.java`

## Agent Startup

`Agent` loads every `SpringExtension` declared on the classpath:

```text
META-INF/services/com.oberasoftware.iot.core.extensions.SpringExtension
```

Maximus declares:

```text
com.oberasoftware.robo.maximus.RobotConfiguration
```

`RobotConfiguration` imports the robot base and Dynamixel configuration:

```java
@Configuration
@ComponentScan
@Import({CoreConfiguration.class, DynamixelConfiguration.class})
public class RobotConfiguration implements SpringExtension {
}
```

For a future robot model, create the same three pieces:

- a model module jar;
- a Spring configuration class implementing `SpringExtension`;
- a `META-INF/services/com.oberasoftware.iot.core.extensions.SpringExtension` file pointing at that configuration class.

## Agent Runtime Configuration

`AgentBootstrapImpl` requires these stored settings before it starts:

- `thing-svc.baseUrl`
- `thing-svc.apiToken`
- `state-svc.baseUrl`
- `mqtt.host`
- `mqtt.port`

The Maximus resource defaults include:

```properties
thing-svc.baseUrl=http://localhost:9010
state-svc.baseUrl=http://localhost:9006
thing-svc.apiToken=testToken
```

The agent configures:

- `AgentClient` for `thing-svc`;
- `StateClient` for `state-svc`;
- `MQTTTopicEventBus` for command and state/event transport.

Then it:

1. creates or updates the controller in `thing-svc`;
2. activates all `AutomationExtension` beans;
3. optionally starts rules for the controller.

## Extension Contract

Robot models integrate through `AutomationExtension`.

Maximus implementation: `RobotExtension`

Important methods:

- `getId()`: returns `RobotExtension`; this is the plugin id used by configured robot things.
- `getName()`: returns the plugin display name.
- `getCommandHandler()`: returns `RobotCommandHandler`.
- `activate(IotThing pluginThing)`: starts robot initialization.
- `discoverThings(DiscoveryListener listener)`: currently empty for Maximus because the robot graph is already configured in `thing-svc`.

Current Maximus note: `isReady()` returns `false`, so the generic extension manager's readiness wait never completes for discovery. The practical Maximus initialization still happens because `activate()` calls `robotInitializer.initialize()` before the wait. Future robot integrations should return `true` when initialization is complete if they want `discoverThings` to run.

## Thing Graph Used By Maximus

`IotRobotInitializer` starts with:

```java
agentClient.getThings(controllerId, RobotExtension.ROBOT_EXTENSION, "robot")
```

That means every configured robot root must be an `IotThing` with:

- `controllerId`: the running agent controller id;
- `pluginId`: `RobotExtension`;
- `type`: `robot`;
- `schemaId`: currently matched by `ActivatorFactory`, for Maximus this is `ConfigurableRobot`;
- `thingId`: robot id, also used as the runtime robot name.

Maximus then creates:

- `HardwareRobotBuilder`, keyed by the root robot `thingId`;
- `ConfigurableRobotBuilder`, keyed by the same controller and robot id;
- `RobotContext`, which carries both builders into activators.

## Activator Pattern

Maximus uses schema-specific activators:

```java
public interface Activator {
    String getSchemaId();
    List<IotThing> getDependents(RobotContext context, IotThing activatable);
    void activate(RobotContext context, IotThing activatable);
}
```

`ActivatorFactory` selects an activator by comparing `Activator.getSchemaId()` to `IotThing.getSchemaId()`.

Future robot integrations should use one activator per schema or capability. Keep activators small:

- read only the properties they own;
- fetch children or linked things through `AgentClient`;
- add hardware capabilities to `HardwareRobotBuilder`;
- add logical joints, wheels, sensors, or behaviours to the robot model builder.

## Maximus Activators

| Activator | Schema id | Role | Required thing properties |
| --- | --- | --- | --- |
| `ConfigurableRobotActivator` | `ConfigurableRobot` | Root robot activation; finds dependent driver, wheels, sensors, drive behaviours, and joints. | `servoDriver` optional thing id |
| `DynamixelActivator` | `DynamixelServoDriver` | Adds `DynamixelServoDriver`, `ServoSensorDriver`, and `DynamixelStateManager` to hardware. | `DXL_PORT`; optional `sensors=true` |
| `WheelActivator` | `Wheel` | Maps a wheel thing to a servo and adds it to the robot builder. | `servo`; optional `reverseDirection=true` |
| `MecanumDriveActivator` | `meccanum` | Adds mecanum drive behaviour. | `frontRight`, `frontLeft`, `rearRight`, `rearLeft` wheel thing ids |
| `Ina260Activator` | `Ina260` | Adds INA260 current sensor. | none |
| `LSM9DSActivator` | `LSM9DS1` | Adds LSM9DS1 gyro sensor. | none |

Maximus also expects joints as children of the robot with `type=Joint`. Each joint uses property `servo` to reference a servo thing. The referenced servo must already have been registered by `DynamixelActivator`.

## Recommended thing-svc Shape

Use schemas to make the graph explicit and valid.

Recommended parent relationships:

- `ConfigurableRobot`: parent type `Plugin`.
- `DynamixelServoDriver`: parent type `ConfigurableRobot`.
- `DynamixelServo`: parent type `DynamixelServoDriver`.
- `Joint`: parent type `ConfigurableRobot`.
- `Wheel`: parent type `ConfigurableRobot`.
- `meccanum`: parent type `ConfigurableRobot`.
- `Ina260`: parent type `ConfigurableRobot`.
- `LSM9DS1`: parent type `ConfigurableRobot`.

Recommended root robot thing:

```json
{
  "controllerId": "maximus-controller",
  "thingId": "maximus",
  "friendlyName": "Maximus",
  "pluginId": "RobotExtension",
  "schemaId": "ConfigurableRobot",
  "type": "robot",
  "parentId": "RobotExtension",
  "properties": {
    "servoDriver": "maximus-dxl"
  },
  "attributes": {
    "torgue": "SWITCH",
    "motion": "LABEL",
    "frame": "LABEL"
  }
}
```

Recommended Dynamixel driver thing:

```json
{
  "controllerId": "maximus-controller",
  "thingId": "maximus-dxl",
  "friendlyName": "Maximus Dynamixel Driver",
  "pluginId": "RobotExtension",
  "schemaId": "DynamixelServoDriver",
  "type": "driver",
  "parentId": "maximus",
  "properties": {
    "DXL_PORT": "/dev/tty.usbmodem61074701",
    "sensors": "true"
  },
  "attributes": {}
}
```

Recommended servo thing:

```json
{
  "controllerId": "maximus-controller",
  "thingId": "left-elbow-servo",
  "friendlyName": "Left Elbow Servo",
  "pluginId": "RobotExtension",
  "schemaId": "DynamixelServo",
  "type": "servo",
  "parentId": "maximus-dxl",
  "properties": {
    "servo_id": "12"
  },
  "attributes": {
    "position": "ABS_POSITION",
    "speed": "VELOCITY",
    "torgue": "SWITCH",
    "temperature": "TEMPERATURE",
    "voltage": "VOLTAGE"
  }
}
```

Recommended joint thing:

```json
{
  "controllerId": "maximus-controller",
  "thingId": "left-elbow",
  "friendlyName": "Left Elbow",
  "pluginId": "RobotExtension",
  "schemaId": "Joint",
  "type": "Joint",
  "parentId": "maximus",
  "properties": {
    "servo": "left-elbow-servo"
  },
  "attributes": {
    "degrees": "DEGREES",
    "position": "ABS_POSITION",
    "torgue": "SWITCH"
  }
}
```

Recommended wheel thing:

```json
{
  "controllerId": "maximus-controller",
  "thingId": "front-left-wheel",
  "friendlyName": "Front Left Wheel",
  "pluginId": "RobotExtension",
  "schemaId": "Wheel",
  "type": "wheel",
  "parentId": "maximus",
  "properties": {
    "servo": "front-left-servo",
    "reverseDirection": "false"
  },
  "attributes": {
    "speed": "VELOCITY"
  }
}
```

Recommended mecanum drive thing:

```json
{
  "controllerId": "maximus-controller",
  "thingId": "mecanum-drive",
  "friendlyName": "Mecanum Drive",
  "pluginId": "RobotExtension",
  "schemaId": "meccanum",
  "type": "drive",
  "parentId": "maximus",
  "properties": {
    "frontLeft": "front-left-wheel",
    "frontRight": "front-right-wheel",
    "rearLeft": "rear-left-wheel",
    "rearRight": "rear-right-wheel"
  },
  "attributes": {
    "drive": "LABEL"
  }
}
```

## Activation Order

For each root robot:

1. `IotRobotInitializer` gets an activator for the root `ConfigurableRobot` thing.
2. `ConfigurableRobotActivator.getDependents()` returns:
   - the `servoDriver` property target;
   - child things of type `wheel`;
   - child things of type `sensor`;
   - child things of type `drive`.
3. Each dependent is activated first.
4. The root robot activator then activates joints by loading child things of type `Joint`.
5. `HardwareRobotBuilder.build()` creates and initializes hardware.
6. `ConfigurableRobotBuilder.build()` creates the behavioural robot.
7. The configured robot is registered in `ConfiguredRobotRegistery`.
8. Hardware is registered in `RobotRegistry`.
9. A `RobotEventListener` is attached to hardware events.

This order matters. Servo-backed things such as joints and wheels need `DynamixelActivator` to register servo ids before they resolve `servo` references.

## Command Flow

Platform command path:

1. Caller posts a `BasicCommandImpl` to `command-svc`.
2. `command-svc` publishes command JSON to the command topic.
3. `iot-agent` subscribes to `/commands/#`.
4. `MQTTCommandListener` parses the MQTT payload as `BasicCommandImpl`.
5. `BasicCommandHandler` converts it by `commandType`.
6. `ItemCommandEventHandler` looks up the target thing in `thing-svc`.
7. The target thing's `pluginId` selects the extension.
8. `RobotCommandHandler` dispatches each command attribute to matching `RobotAttributeHandler` beans.

Maximus command handlers:

| Handler | Attribute(s) | Target |
| --- | --- | --- |
| `PositionHandler` | `position`, optional `speed` | Servo thing through `ServoRegistry` |
| `DegreesHandler` | `degrees` | Joint thing through `ConfiguredRobotRegistery` |
| `SpeedHandler` | `speed` | Wheel or servo thing |
| `TorgueHandler` | `torgue`, optional `servos` | Robot, joint, or servo |
| `DriveHandler` | `drive` with `x`, `y`, `z` values | Drive thing's parent robot |
| `MotionHandler` | `motion` or `frame` | Root robot thing |

Example joint command:

```json
{
  "controllerId": "maximus-controller",
  "thingId": "left-elbow",
  "commandType": "VALUE",
  "attributes": {
    "degrees": "45"
  }
}
```

Example servo command:

```json
{
  "controllerId": "maximus-controller",
  "thingId": "left-elbow-servo",
  "commandType": "VALUE",
  "attributes": {
    "position": "2048",
    "speed": "20"
  }
}
```

Example mecanum drive command:

```json
{
  "controllerId": "maximus-controller",
  "thingId": "mecanum-drive",
  "commandType": "VALUE",
  "attributes": {
    "drive": "true",
    "x": "0.25",
    "y": "1.0",
    "z": "0.0"
  }
}
```

## Event And State Flow

Robot event path:

1. Robot hardware, servo drivers, joints, or sensors publish local events on `LocalEventBus`.
2. `IotRobotInitializer.RobotEventListener` maps those events to `ThingValueEventImpl` or `ThingMultiValueEventImpl`.
3. `ValueEventHandler` publishes those events to MQTT.
4. `state-svc` consumes value messages from its state topic and updates latest state.
5. Consumers can read state through `state-svc` REST or subscribe to `/topic/state`.

Maximus mappings:

- `ServoUpdateEvent` is mapped to all registered servo things for that physical servo id.
- Servo property names are lowercased before publication.
- `Scale` values are filtered out of servo state events.
- Joint updates publish values against the joint `thingId`.
- Sensor data publishes a single attribute/value against the sensor thing.

Example state event produced from a joint update:

```json
{
  "controllerId": "maximus-controller",
  "thingId": "left-elbow",
  "values": {
    "position": { "type": "NUMBER", "value": 2048 },
    "degrees": { "type": "NUMBER", "value": 45 }
  }
}
```

## Building A Future Robot Model

Use this checklist:

1. Create a module under `robot-models/<robot-name>`.
2. Depend on the needed robotics modules, for example `robo-core`, `dynamixel-core`, and `robo-behaviours`.
3. Add a `SpringExtension` configuration class with `@ComponentScan`.
4. Register the configuration class in `META-INF/services/com.oberasoftware.iot.core.extensions.SpringExtension`.
5. Implement an `AutomationExtension`.
6. Implement a robot initializer that queries `AgentClient` for root robot things by `controllerId`, `pluginId`, and `type`.
7. Define one activator per schema or capability.
8. Define command handlers by implementing `RobotAttributeHandler` or another `ThingCommandHandler`.
9. Publish robot state through local events that become `ThingValueEvent` or `ThingMultiValueEvent`.
10. Create plugin, schema, controller, and thing records in `thing-svc`.
11. Start the agent with the robot model jar on the classpath.
12. Verify command and state round trips through `command-svc`, MQTT, `iot-agent`, and `state-svc`.

## Design Rules For New Integrations

- Keep `thing-svc` as the source of truth for the robot graph.
- Use `schemaId` as the activator selection key.
- Use `type` for broad discovery filters such as `robot`, `Joint`, `wheel`, `sensor`, and `drive`.
- Use `properties` for hardware binding such as servo ids, serial ports, and logical links.
- Make hardware dependencies explicit through parent/child relationships or schema `LINK` fields.
- Register physical-to-logical mappings before command handlers need them.
- Emit state against the logical thing that users and automation rules address.
- Prefer `ThingMultiValueEvent` when a hardware event reports multiple attributes for one thing.
- Return `true` from `AutomationExtension.isReady()` once activation is complete if the model uses discovery.

## Files To Copy Or Mirror

For a new robot model, start from these Maximus files:

- `RobotConfiguration.java`: Spring extension entrypoint.
- `RobotExtension.java`: platform extension contract.
- `IotRobotInitializer.java`: thing-svc driven robot construction.
- `activator/Activator.java`: schema activator contract.
- `activator/ActivatorFactory.java`: schema-to-activator routing.
- `handlers/RobotCommandHandler.java`: command fanout.
- `handlers/RobotAttributeHandler.java`: attribute handler contract.
- `model/SensorDataImpl.java` and `model/JointDataImpl.java`: state event payload patterns.

