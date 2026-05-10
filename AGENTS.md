# Agent Descriptor

Before extending `thing-svc`, `command-svc`, or `state-svc`, read:

- `docs/index.md`
- `docs/llm-integration-guide.md`
- `docs/api/backend-services-api.md`
- `docs/api/backend-services-openapi.yaml`
- `docs/agents-descriptor.json`

Service-specific docs:

- `docs/services/thing-svc.md`
- `docs/services/command-svc.md`
- `docs/services/state-svc.md`

Key rule: `thing-svc` owns dynamic configuration, `command-svc` dispatches commands, and `state-svc` consumes and exposes observed state. Keep these docs updated when changing REST endpoints, message payloads, configuration entities, or service responsibilities.

