DLQ_QUEUE ?= webhook-dlq-queue
RABBIT_CONTAINER ?= rabbitmq

.PHONY: dlq-get dlq-purge

# Fetch up to 10 messages from the DLQ for inspection
dlq-get:
	docker exec $(RABBIT_CONTAINER) rabbitmqadmin get queue=$(DLQ_QUEUE) count=10

# Purge all messages from the DLQ (use with caution)
dlq-purge:
	docker exec $(RABBIT_CONTAINER) rabbitmqadmin purge queue=$(DLQ_QUEUE)

