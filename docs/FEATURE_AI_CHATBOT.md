# AI Chatbot (OpenAI only)

```
React → API Gateway (8080) → ai-service (8085) → OpenAI (or FAQ fallback)
```

## Setup — `ai-service/.env`

```env
AI_PROVIDER=openai
OPENAI_API_KEY=sk-proj-...
OPENAI_MODEL=gpt-4o-mini
```

Get key: https://platform.openai.com/api-keys

Copy `ai-service/.env.example` → `ai-service/.env`, paste key, restart ai-service.

## Providers

| Mode | Config |
|------|--------|
| **openai** | `AI_PROVIDER=openai` + `OPENAI_API_KEY` |
| **faq** | `AI_PROVIDER=faq` (offline, no key) |

## Test

```
http://localhost:8080/api/ai/status?probe=true
```

Chatbot supports **Auto / English / Hindi / Arabic / Urdu** in the language dropdown.
