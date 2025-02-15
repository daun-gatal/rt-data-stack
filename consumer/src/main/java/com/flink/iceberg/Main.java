package com.flink.iceberg;

import java.util.ArrayList;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.hadoop.conf.Configuration;

import com.flink.iceberg.models.IcebergRideEvent;
import com.flink.iceberg.models.RideEvent;
import com.flink.iceberg.operators.IcebergSinkOperator;
import com.flink.iceberg.operators.JsonToRideEventMapFunction;
import com.flink.iceberg.operators.IcebergRideEventFlatMapFunction;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;

public class Main {

    private static final String KAFKA_SOURCE_TOPIC = System.getenv("KAFKA_SOURCE_TOPIC");
    private static final String KAFKA_BROKER = System.getenv("KAFKA_BROKER");

    public static void main(String[] args) throws Exception {
        // Set up the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Create the FlinkKafkaConsumer
        KafkaSource<String> source = KafkaSource.<String>builder()
        .setBootstrapServers(KAFKA_BROKER)
        .setTopics(KAFKA_SOURCE_TOPIC)
        .setGroupId("kafka-group")
        .setStartingOffsets(OffsetsInitializer.earliest())
        .setValueOnlyDeserializer(new SimpleStringSchema())
        .build();

        // Add the Kafka source to the data stream
        DataStream<String> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "flink-consumer");

        DataStream<RideEvent> rideEvent = stream
        .map(new JsonToRideEventMapFunction());

        DataStream<IcebergRideEvent> simpleRideEvent = rideEvent
        .flatMap(new IcebergRideEventFlatMapFunction());

        IcebergSinkOperator.buildLocal(
            env, 
            new Configuration(), 
            simpleRideEvent, 
            false, 
            new ArrayList<>()
        );

        // Execute the Flink job
        env.execute("Flink Kafka Consumer");
    }
}
