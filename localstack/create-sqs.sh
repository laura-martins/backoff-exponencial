#!/bin/bash
set -euo pipefail

echo "Criando filas SQS"

# Cria DLQ
DLQ_URL=$(awslocal sqs create-queue \
  --queue-name test-queue-dlq \
  --attributes VisibilityTimeout=10,DelaySeconds=0 \
  --query 'QueueUrl' \
  --output text)

# Obtém ARN da DLQ
DLQ_ARN=$(awslocal sqs get-queue-attributes \
  --queue-url "$DLQ_URL" \
  --attribute-names QueueArn \
  --query 'Attributes.QueueArn' \
  --output text)

# Cria fila principal com Redrive Policy + timeouts
awslocal sqs create-queue \
  --queue-name test-queue \
  --attributes "VisibilityTimeout=10,DelaySeconds=0,RedrivePolicy={\"deadLetterTargetArn\":\"$DLQ_ARN\",\"maxReceiveCount\":\"10\"}"

echo "Filas SQS criadas com sucesso"
