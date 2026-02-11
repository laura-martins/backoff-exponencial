#!/bin/bash
set -euo pipefail

echo "Criando filas SQS..."

awslocal sqs create-queue --queue-name check-payment-queue
awslocal sqs create-queue --queue-name check-payment-queue-DLQ
awslocal sqs set-queue-attributes \
    --queue-url http://sqs.sa-east-1.localhost.localstack.cloud:4566/000000000000/check-payment-queue \
    --attributes '{
    "RedrivePolicy": "{\"deadLetterTargetArn\":\"arn:aws:sqs:sa-east-1:000000000000:check-payment-queue-DLQ\",\"maxReceiveCount\":\"10\"}"
}'

echo "Filas SQS criadas com sucesso"
