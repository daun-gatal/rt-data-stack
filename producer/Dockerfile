# Use an official Python runtime as a parent image
FROM python:3.9-slim

# Set the working directory in the container
WORKDIR /app

# Install dependencies
RUN pip install --no-cache-dir confluent-kafka orjson

# Copy the Python script into the container
COPY producer.py .

# Run the Python script when the container starts
CMD ["python", "producer.py"]
