# Backend Service Documentation

This directory documents the IoT backend services that provide dynamic device configuration, command dispatch, and state tracking.

The primary services are:

- `thing-svc`: system metadata, plugins, schemas, controllers, things, groups, and rules.
- `command-svc`: command ingress API and RabbitMQ command publishing.
- `state-svc`: state event consumption, in-memory latest state, optional time-series storage, REST state lookup, and STOMP state updates.

The docs are written for two consumers:

- Engineers integrating new devices, robots, plugins, schemas, and capabilities.
- LLM agents that need stable context before changing or extending the platform.

## Start Here

- [LLM integration guide](llm-integration-guide.md): canonical concepts, extension workflow, and common integration patterns.
- [Thing service](services/thing-svc.md): metadata model and dynamic configuration flow.
- [Command service](services/command-svc.md): command API and message flow.
- [State service](services/state-svc.md): state ingestion, lookup, storage, and WebSocket publishing.
- [Robot model integration guide](robot-model-integration-guide.md): how robots use `iot-agent`, `thing-svc`, commands, and events, based on `robo-maximus`.
- [API reference](api/backend-services-api.md): endpoint-by-endpoint REST and STOMP reference.
- [OpenAPI descriptor](api/backend-services-openapi.yaml): machine-readable API contract for REST endpoints.
- [Agent descriptor](agents-descriptor.json): machine-readable pointers to these docs and relevant source files.

## Source Locations

- `backend/thing-svc`
- `backend/command-svc`
- `backend/state-svc`
- Shared model and command types: `iot-base/iot-core`
- Storage implementation used by `thing-svc`: `iot-base/home-storage`
- RabbitMQ connector support: `backend/queue-connectors`

## Generated Java API Docs

Javadoc output is produced by Maven under each service module:

- `backend/thing-svc/target/apidocs/index.html`
- `backend/command-svc/target/apidocs/index.html`
- `backend/state-svc/target/apidocs/index.html`

Regenerate these with:

```bash
mvn -pl backend/thing-svc,backend/command-svc,backend/state-svc -am javadoc:javadoc
```
