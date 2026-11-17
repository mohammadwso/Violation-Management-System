import json
import os
import time

from kafka import KafkaProducer

from common import INTERVAL, ensure_bucket, random_violation

BOOTSTRAP = os.getenv("KAFKA_BOOTSTRAP", "kafka:9092")
TOPIC = os.getenv("KAFKA_TOPIC", "violations")

ensure_bucket()
producer = KafkaProducer(
    bootstrap_servers=BOOTSTRAP,
    value_serializer=lambda v: json.dumps(v).encode(),
    retries=10,
)

while True:
    v = random_violation()
    producer.send(TOPIC, v)
    producer.flush()
    print(f"[kafka] sent {v['id']} {v['type']} -> {TOPIC}", flush=True)
    time.sleep(INTERVAL)
