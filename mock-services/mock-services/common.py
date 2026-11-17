import io
import os
import random
import uuid
from datetime import datetime, timezone

from minio import Minio
from PIL import Image

INTERVAL = float(os.getenv("INTERVAL_SECONDS", "2"))

MINIO_ENDPOINT = os.getenv("MINIO_ENDPOINT", "minio:9000")
MINIO_ACCESS_KEY = os.getenv("MINIO_ACCESS_KEY", "minioadmin")
MINIO_SECRET_KEY = os.getenv("MINIO_SECRET_KEY", "minioadmin")
MINIO_BUCKET = os.getenv("MINIO_BUCKET", "violations")

VIOLATION_TYPES = ["SPEEDING", "RED_LIGHT", "NO_SEATBELT", "WRONG_WAY", "MOBILE_PHONE"]

minio = Minio(MINIO_ENDPOINT, MINIO_ACCESS_KEY, MINIO_SECRET_KEY, secure=False)


def ensure_bucket():
    if not minio.bucket_exists(MINIO_BUCKET):
        minio.make_bucket(MINIO_BUCKET)


def upload_random_image(violation_id):
    img = Image.new("RGB", (320, 240), tuple(random.randint(0, 255) for _ in range(3)))
    buf = io.BytesIO()
    img.save(buf, "JPEG")
    buf.seek(0)
    key = f"{violation_id}.jpg"
    minio.put_object(MINIO_BUCKET, key, buf, length=buf.getbuffer().nbytes, content_type="image/jpeg")
    return f"{MINIO_BUCKET}/{key}"


def random_violation():
    vid = str(uuid.uuid4())
    speed_limit = random.choice([60, 80, 100, 120])
    return {
        "id": vid,
        "type": random.choice(VIOLATION_TYPES),
        "plateNumber": f"{random.choice('ABCDEFGH')}{random.choice('ABCDEFGH')}{random.randint(1000, 9999)}",
        "cameraId": f"CAM-{random.randint(1, 50):03d}",
        "speed": random.randint(speed_limit, speed_limit + 60),
        "speedLimit": speed_limit,
        "latitude": round(random.uniform(24.0, 25.0), 6),
        "longitude": round(random.uniform(46.0, 47.0), 6),
        "timestamp": datetime.now(timezone.utc).isoformat(),
        "imagePath": upload_random_image(vid),
    }
