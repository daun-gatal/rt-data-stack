FROM maven:3.9.8-eclipse-temurin-11 AS builder

WORKDIR /opt/flink
COPY src/ /opt/flink/src/
COPY pom.xml /opt/flink/pom.xml

RUN mvn clean package

FROM flink:1.18.1-scala_2.12-java11

WORKDIR /opt/flink/lib
COPY --from=builder --chown=flink:flink /opt/flink/target/consumer-1.0-SNAPSHOT.jar /opt/flink/lib/consumer-1.0-SNAPSHOT.jar

## Iceberg Flink Library & Hadoop Dependencies (Combined)
RUN mkdir -p /opt/flink/lib && \
    curl -L -o /opt/flink/lib/iceberg-flink-runtime-1.18-1.7.0.jar https://repo1.maven.org/maven2/org/apache/iceberg/iceberg-flink-runtime-1.18/1.7.0/iceberg-flink-runtime-1.18-1.7.0.jar && \
    curl -L -o /opt/flink/lib/flink-s3-fs-hadoop-1.18.1.jar https://repo1.maven.org/maven2/org/apache/flink/flink-s3-fs-hadoop/1.18.1/flink-s3-fs-hadoop-1.18.1.jar && \
    curl -L -o /opt/flink/lib/hadoop-common-2.8.3.jar https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-common/2.8.3/hadoop-common-2.8.3.jar && \
    curl -L -o /opt/flink/lib/hadoop-hdfs-2.8.3.jar https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-hdfs/2.8.3/hadoop-hdfs-2.8.3.jar && \
    curl -L -o /opt/flink/lib/hadoop-aws-2.8.3.jar https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-aws/2.8.3/hadoop-aws-2.8.3.jar && \
    curl -L -o /opt/flink/lib/hadoop-hdfs-client-2.8.3.jar https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-hdfs-client/2.8.3/hadoop-hdfs-client-2.8.3.jar && \
    rm -rf /var/lib/apt/lists/*

CMD ["./bin/start-cluster.sh"]
