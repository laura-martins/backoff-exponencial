#!/bin/bash
set -euo pipefail

echo "Criando filas SQS"
awslocal sqs create-queue --queue-name "test-queue"
echo "Filas SQS criadas com sucesso"
