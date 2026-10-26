# 04 — Buno InfraPilot
Workflow de solicitações de infraestrutura com estados pendentes/aprovados e registro de decisão humana. Criado para conectar futuramente ao runtime Buno Aegis e a um runner Terraform isolado.
`docker compose up --build`; API `http://localhost:8104`.
- `POST /api/infra-requests` body `{"environment":"dev","service":"api-core","change":"Criar fila SQS","requestedBy":"joao"}`
- `GET /api/infra-requests`
- `POST /api/infra-requests/{id}/decision` body `{"approvedBy":"reviewer","note":"Plano revisado"}`
Não executa comandos nem aplica Terraform. Armazenamento em memória; para produção, usar DB, autenticação forte, RBAC, logs imutáveis e runner sandboxed. Nunca permitir que prompt de agente execute comandos arbitrários sem política e aprovação.
