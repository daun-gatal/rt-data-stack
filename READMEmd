# Real-Time Data Pipeline with Kafka, Flink, and Iceberg

This project is a learning-based implementation of a real-time data pipeline using various modern data engineering tools. The pipeline ingests dummy ride event data into Kafka, processes it using Apache Flink, stores it in MinIO (S3-compatible storage) using Iceberg, manages metadata with Nessie, queries the data using Trino, and visualizes insights in Grafana.

## Architecture Overview

1. **Kafka**: Acts as the event storage system for real-time ingestion.
2. **Python Producer**: A script that generates dummy ride event data and pushes it to Kafka.
3. **Kafka UI**: A monitoring tool for Kafka topics and events.
4. **MinIO**: Serves as an S3-compatible object storage for storing processed data.
5. **Flink**: Consumes events from Kafka and writes them to Iceberg tables in MinIO.
6. **Iceberg**: A table format that enables efficient querying and processing of large datasets.
7. **Nessie**: The metadata store for Iceberg tables, providing version control.
8. **Trino**: A distributed query engine used to analyze data stored in Iceberg.
9. **Grafana**: A visualization tool for monitoring real-time data insights.

## Services and Ports

| Service       | Description                                 | Ports | Full Link |
|--------------|---------------------------------------------|--------|------------|
| Kafka        | Message broker for event streaming         | 9092 (public), 9997 (internal) | N/A |
| Kafka UI     | Web UI to monitor Kafka topics            | 8080 | [http://localhost:8080](http://localhost:8080) |
| MinIO        | S3-compatible storage for Iceberg data    | 9000 (API), 9090 (Console) | [http://localhost:9090](http://localhost:9090) |
| Nessie       | Iceberg metadata store                    | 19120 | [http://localhost:19120](http://localhost:19120) |
| Trino        | SQL query engine for Iceberg              | 8082 (mapped to 8080) | [http://localhost:8082](http://localhost:8082) |
| Flink Job Manager | Manages Flink jobs                  | 8081 | [http://localhost:8081](http://localhost:8081) |
| Grafana      | Data visualization and monitoring         | 3000 | [http://localhost:3000](http://localhost:3000) |

## Getting Started

This project includes a Makefile with useful commands to manage the Docker Compose services efficiently.

### Makefile Commands

#### `up`
Starts all necessary services using Docker Compose.
- Creates a `.data/minio` directory to persist MinIO data.
- Ensures proper permissions are set for the MinIO data directory.
- Runs `docker-compose up -d` to start all services in detached mode.
- Displays a message indicating that services are up and running.

#### `down`
Stops and removes all running services.
- Executes `docker-compose down -v` to stop and remove containers, networks, and volumes.
- Deletes the `.data` directory to clean up any persisted data.
- Displays a message confirming cleanup completion.

#### `cleanup`
Performs a thorough cleanup by checking if Docker Compose is running and removing all related images.
- Checks if any Docker Compose services are running.
- If running, it first stops them using the `down` command.
- Removes all Docker Compose-related images using `docker-compose down -v --rmi all`.
- Displays a message confirming cleanup completion.

To use these commands, simply run:
```bash
make up       # Start services
make down     # Stop services and remove data
make cleanup  # Stop services and remove images
```

## Future Enhancements
- Implement schema evolution in Iceberg.
- Automate Flink job submission.
- Add data quality checks in the pipeline.

## License
MIT License. Feel free to use and modify this project for learning purposes.

---

### Contributions
If you find this project useful, feel free to contribute by submitting pull requests or opening issues.

