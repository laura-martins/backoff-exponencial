# Backoff Exponencial com Amazon SQS

Este projeto demonstra a implementação de **backoff exponencial utilizando o Visibility Timeout do Amazon SQS**, evitando retries agressivos e consumo desnecessário de recursos quando ocorre falha no processamento de mensagens.

A estratégia utiliza o atributo **ApproximateReceiveCount**, incrementado automaticamente pelo SQS a cada novo recebimento, para calcular dinamicamente o tempo que a mensagem permanece invisível antes da próxima tentativa.

---

## Estratégia de Backoff

Configurações adotadas:

- Delay base: **30 segundos**
- Delay máximo: **15 minutos (900 segundos)**
- Total de tentativas: **10**
- Após exceder o número máximo de tentativas, a mensagem é encaminhada para a **DLQ**

---

## Tabela de Tentativas

| Tentativa | Delay aplicado       |
|----------:|----------------------|
|         1 | 30 segundos          |
|         2 | 1 minuto             |
|         3 | 2 minutos            |
|         4 | 4 minutos            |
|         5 | 8 minutos            |
|         6 | 15 minutos           |
|         7 | 15 minutos           |
|         8 | 15 minutos           |
|         9 | 15 minutos           |
|        10 | 15 minutos → **DLQ** |

> A partir da 6ª tentativa, o delay é limitado ao valor máximo configurado, respeitando o limite do Visibility Timeout do SQS.

---

## Fila de Teste (LocalStack)

URL da fila:
http://sqs.sa-east-1.localhost.localstack.cloud:4566/000000000000/test-queue

## Enviar mensagens (LocalStack / awslocal)

### Sucesso

```bash
awslocal sqs send-message \
  --queue-url "http://sqs.sa-east-1.localhost.localstack.cloud:4566/000000000000/test-queue" \
  --message-body '{
    "paymentId": "123"
  }'
```

### Erro (gera retry)

```bash
awslocal sqs send-message \
  --queue-url "http://sqs.sa-east-1.localhost.localstack.cloud:4566/000000000000/test-queue" \
  --message-body '{
    "paymentId": "Mensagem com erro"
  }'
```

### Como executar a aplicação (local)

*Requisitos*:
- Java 17
- Maven
- Docker para executar LocalStack

*Configurações*: 
- SPRING_PROFILES_ACTIVE=local

![readme_exec_application.png](images/readme_exec_application.png)


https://nerddevs.com/retry-smarter-with-aws-sqs/
