import json
import os
import time

import pika

from common import INTERVAL, ensure_bucket, random_violation

RMQ_URL = os.getenv("RMQ_URL", "amqp://guest:guest@rabbitmq:5672/")
QUEUE = os.getenv("RMQ_QUEUE", "violations")

ensure_bucket()
conn = pika.BlockingConnection(pika.URLParameters(RMQ_URL))
channel = conn.channel()
channel.queue_declare(queue=QUEUE, durable=True)

while True:
    v = random_violation()
    channel.basic_publish(
        exchange="",
        routing_key=QUEUE,
        body=json.dumps(v),
        properties=pika.BasicProperties(content_type="application/json", delivery_mode=2),
    )
    print(f"[rmq] sent {v['id']} {v['type']} -> {QUEUE}", flush=True)
    time.sleep(INTERVAL)
