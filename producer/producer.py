from datetime import datetime, timedelta
import os
import orjson  # Faster JSON serialization
import time
import random
from confluent_kafka import Producer
from concurrent.futures import ThreadPoolExecutor

# Kafka configuration
KAFKA_BROKER = os.getenv("KAFKA_BROKER")
KAFKA_TOPIC = os.getenv("KAFKA_SOURCE_TOPIC")

# Configure the producer
producer_conf = {"bootstrap.servers": KAFKA_BROKER}
producer = Producer(producer_conf)

# Precompute choices for faster selection
STATUS_LIST = ["requested", "accepted", "ongoing", "completed", "cancelled"]
PAYMENT_METHODS = ["credit_card", "debit_card", "cash", "wallet"]
VEHICLE_TYPES = ["Sedan", "SUV", "Hatchback", "Motorbike", "Van"]
RIDE_TYPES = ["standard", "premium", "pool", "business"]
PROMO_CODES = ["DISCOUNT10", "FREERIDE", "SUMMER50", None]
CANCELLATION_REASONS = ["user_cancelled", "driver_cancelled", "system_cancelled", None]


# Function to generate enriched dummy booking data
def generate_dummy_data():
    status = random.choice(STATUS_LIST)

    return orjson.dumps(
        {
            "booking_id": random.randint(100000, 999999),
            "user_id": random.randint(1, 10000),
            "driver_id": random.randint(1, 5000),
            "pickup_location": {
                "latitude": random.uniform(-90, 90),
                "longitude": random.uniform(-180, 180),
                "address": f"{random.randint(1, 9999)} Main St, City {random.randint(1, 100)}",
            },
            "dropoff_location": {
                "latitude": random.uniform(-90, 90),
                "longitude": random.uniform(-180, 180),
                "address": f"{random.randint(1, 9999)} Elm St, City {random.randint(1, 100)}",
            },
            "fare": random.uniform(5, 100),
            "distance_km": random.uniform(1, 50),
            "duration_min": random.randint(5, 120),
            "status": status,
            "payment_method": random.choice(PAYMENT_METHODS),
            "timestamp_ns": time.time_ns(),
            "scheduled_time_ns": (
                datetime.now() + timedelta(minutes=random.randint(5, 1440))
            ).timestamp()
            * 1e9,
            "vehicle_type": random.choice(VEHICLE_TYPES),
            "ride_type": random.choice(RIDE_TYPES),
            "passenger_count": random.randint(1, 4),
            "driver_rating": round(random.uniform(1.0, 5.0), 1),
            "user_rating": round(random.uniform(1.0, 5.0), 1),
            "promo_code": random.choice(PROMO_CODES),
            "cancellation_reason": (
                random.choice(CANCELLATION_REASONS) if status == "cancelled" else None
            ),
        }
    ).decode()


# Kafka message delivery callback
def delivery_report(err, msg):
    if err:
        print(f"Message delivery failed: {err}")
    else:
        print(f"Message delivered to {msg.topic()} [{msg.partition()}]")


# Function to produce a single message
def produce_message():
    json_data = generate_dummy_data()
    booking_id = str(random.randint(100000, 999999))  # Random key
    producer.produce(
        KAFKA_TOPIC, key=booking_id, value=json_data, callback=delivery_report
    )
    producer.poll(0)  # Let the producer handle async queue


# Run producer loop with multi-threading
if __name__ == "__main__":
    try:
        batch_size = 10  # Flush after every batch_size messages
        count = 0

        with ThreadPoolExecutor(max_workers=4) as executor:  # Adjust workers as needed
            while True:
                executor.submit(produce_message)

                count += 1
                if count % batch_size == 0:
                    producer.flush()  # Flush messages in batches

    except KeyboardInterrupt:
        print("Producer stopped.")
        producer.flush()
