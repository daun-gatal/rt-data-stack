package com.flink.iceberg.operators;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.util.Collector;
import org.apache.hadoop.conf.Configuration;
import org.apache.iceberg.catalog.TableIdentifier;
import org.apache.iceberg.flink.sink.FlinkSink;
import org.apache.iceberg.flink.sink.FlinkSink.Builder;

import com.flink.iceberg.utils.IcebergSinkUtils;

import org.apache.iceberg.flink.CatalogLoader;
import org.apache.iceberg.flink.TableLoader;

import java.lang.reflect.Field;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class IcebergSinkOperator {

    private static final String FLINK_CATALOG = System.getenv("FLINK_CATALOG");
    private static final String FLINK_NAMESPACE = System.getenv("FLINK_NAMESPACE");
    private static final String FLINK_CHECKPOINT_DIR = System.getenv("FLINK_CHECKPOINT_DIR");
    private static final String FLINK_TABLE_NAME = System.getenv("FLINK_TABLE_NAME");

    private static DataStreamSink<?> addSink(
            DataStream<RowData> rowDataStream,
            Configuration hadoopConfig,
            boolean upsert,
            List<String> primaryKeys) {

        Map<String, String> catalogProp = IcebergSinkUtils.getCatalogProperties();
        CatalogLoader catalog = CatalogLoader.custom(
                FLINK_CATALOG, catalogProp,
                hadoopConfig, catalogProp.get("catalog-impl")
        );
        TableLoader tableLoader = TableLoader.fromCatalog(catalog, TableIdentifier.parse(FLINK_NAMESPACE + "." + FLINK_TABLE_NAME));
        
        List<String> equalityColumns = upsert ?
                new ArrayList<>(Arrays.asList("year", "month", "day", "hour")) :
                new ArrayList<>();

        Builder sink = FlinkSink.forRowData(rowDataStream)
                .tableLoader(tableLoader)
                .upsert(upsert);

        if (upsert) {
            sink.equalityFieldColumns(equalityColumns);
        }

        return sink.set("write.format.default", "parquet")
                .set("write.target-file-size-bytes", "10485760")
                .set("write.parquet.compression-codec", "snappy")
                .uidPrefix("iceberg")
                .append();
    }

    private static <ITEM> DataStream<RowData> generateRowDataStream(
            DataStream<ITEM> incomingStream,
            String streamType) {

        return incomingStream.flatMap(new FlatMapFunction<ITEM, RowData>() {
            @Override
            public void flatMap(ITEM value, Collector<RowData> out) throws Exception {
                Field[] fields = value.getClass().getDeclaredFields();
                GenericRowData row = new GenericRowData(fields.length + 4);
                ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);
                
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    Object fieldValue = fields[i].get(value);
                    row.setField(i, IcebergSinkUtils.generateRowData(fieldValue));
                }
                out.collect(IcebergSinkUtils.addPartitionsRowData(row, nowUTC));
            }
        }).name("mappedRowData" + streamType)
          .uid("mappedRowData" + streamType);
    }

    public static <ITEM> DataStreamSink<?> buildLocal(
            StreamExecutionEnvironment env,
            Configuration config,
            DataStream<ITEM> incomingStream,
            boolean upsert,
            List<String> primaryKeys) {

        if (upsert && primaryKeys.isEmpty()) {
            throw new IllegalArgumentException("Primary keys must be specified for upsert operations.");
        } else if (!upsert && !primaryKeys.isEmpty()) {
            throw new IllegalArgumentException("Primary keys must not be specified for non-upsert operations.");
        }

        IcebergSinkUtils.validateEnv();

        env.enableCheckpointing(5000);
        env.getCheckpointConfig().setCheckpointStorage(new Path(FLINK_CHECKPOINT_DIR + String.format("/%s/%s", FLINK_NAMESPACE, FLINK_TABLE_NAME)));
        env.getCheckpointConfig().setCheckpointTimeout(600000);
        
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        String ddlTable = IcebergSinkUtils.generateIcebergTableDDL(
                incomingStream.getType().getTypeClass(), 
                FLINK_TABLE_NAME,
                new ArrayList<>(Arrays.asList("year", "month", "day", "hour")),
                primaryKeys
        );
        String ddlCatalog = IcebergSinkUtils.generateIcebergCatalogDDL();
        String ddlNamespace = IcebergSinkUtils.generateIcebergNamespaceDDL();

        tableEnv.executeSql(ddlCatalog);
        tableEnv.useCatalog(FLINK_CATALOG);
        tableEnv.executeSql(ddlNamespace);
        tableEnv.executeSql(ddlTable);

        Configuration hadoopConfig = IcebergSinkUtils.setS3HadoopConfig(config);
        String streamType = IcebergSinkUtils.getStreamType(incomingStream).toString().split("\\.")[1];
        DataStream<RowData> stream = generateRowDataStream(incomingStream, streamType);
        
        return addSink(stream, hadoopConfig, upsert, primaryKeys);
    }
}

