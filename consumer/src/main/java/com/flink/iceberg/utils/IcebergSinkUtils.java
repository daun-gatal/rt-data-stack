package com.flink.iceberg.utils;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.StringData;
import org.apache.hadoop.conf.Configuration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class IcebergSinkUtils {

    // Load environment variables once at class level
    private static final String S3_ENDPOINT = System.getenv("AWS_S3_ENDPOINT");
    private static final String S3_PATH_STYLE_ACCESS = System.getenv("AWS_S3_PATH_STYLE_ACCESS");
    private static final String ACCESS_KEY = System.getenv("AWS_ACCESS_KEY_ID");
    private static final String SECRET_KEY = System.getenv("AWS_SECRET_ACCESS_KEY");
    private static final String NESSIE_ENDPOINT = System.getenv("NESSIE_ENDPOINT");
    private static final String FLINK_WAREHOUSE = System.getenv("FLINK_WAREHOUSE");
    private static final String FLINK_CATALOG = System.getenv("FLINK_CATALOG");
    private static final String FLINK_NAMESPACE = System.getenv("FLINK_NAMESPACE");

    public static <T> Class<T> getStreamType(DataStream<T> stream) {
        return (Class<T>) stream.getType().getTypeClass();
    }

    public static Configuration setS3HadoopConfig(Configuration config) {
        config.set("fs.s3a.access.key", ACCESS_KEY);
        config.set("fs.s3a.secret.key", SECRET_KEY);
        config.set("fs.s3a.path.style.access", S3_PATH_STYLE_ACCESS);
        config.set("fs.s3a.endpoint", S3_ENDPOINT);
        return config;
    }

    public static void validateEnv() {
        if (Optional.ofNullable(ACCESS_KEY).orElse("").trim().isEmpty()) {
            throw new IllegalArgumentException("AWS_ACCESS_KEY_ID env is empty, please set it to run the job locally");
        }
        if (Optional.ofNullable(SECRET_KEY).orElse("").trim().isEmpty()) {
            throw new IllegalArgumentException("AWS_SECRET_KEY env is empty, please set it to run the job locally");
        }
        if (Optional.ofNullable(S3_ENDPOINT).orElse("").trim().isEmpty()) {
            throw new IllegalArgumentException("AWS_S3_ENDPOINT env is empty, please set it to run the job locally");
        }
    }

    public static Object generateRowData(Object fieldValue) {
        if (fieldValue instanceof String) {
            return StringData.fromString((String) fieldValue);
        } else if (fieldValue instanceof Long || fieldValue instanceof Integer || fieldValue instanceof Boolean || fieldValue instanceof Double) {
            return fieldValue;
        // } else if (isClass(fieldValue)) {
        //     return StringData.fromString(new JSONObject(fieldValue).toString());
        } else {
            return StringData.fromString(fieldValue.toString());
        }
    }

    public static String[] parseS3Path(String s3Path) {
        String regex = "s3a?://.*/([^/]+)/([^/]+)";
        if (s3Path.matches(regex)) {
            return s3Path.replaceAll(regex, "$1,$2").split(",");
        }
        throw new IllegalArgumentException("The S3 path format is incorrect");
    }

    public static Map<String, String> getCatalogProperties() {
        Map<String, String> catalogProperties = new HashMap<>();

        // Set Iceberg catalog properties
        catalogProperties.put("type", "iceberg");
        catalogProperties.put("catalog-impl", "org.apache.iceberg.nessie.NessieCatalog");
        catalogProperties.put("warehouse", FLINK_WAREHOUSE);
        catalogProperties.put("uri", NESSIE_ENDPOINT);
        catalogProperties.put("ref", "main");
        catalogProperties.put("fs.s3a.endpoint", S3_ENDPOINT);
        catalogProperties.put("fs.s3a.path.style.access", S3_PATH_STYLE_ACCESS);
        catalogProperties.put("fs.s3a.access.key", ACCESS_KEY);
        catalogProperties.put("fs.s3a.secret.key", SECRET_KEY);

        return catalogProperties;
    }

    public static GenericRowData addPartitionsRowData(GenericRowData row, ZonedDateTime nowUTC) {
        int offset = row.getArity() - 4;
        row.setField(offset, StringData.fromString(String.valueOf(nowUTC.getYear())));
        row.setField(offset + 1, StringData.fromString(String.format("%02d", nowUTC.getMonthValue())));
        row.setField(offset + 2, StringData.fromString(String.format("%02d", nowUTC.getDayOfMonth())));
        row.setField(offset + 3, StringData.fromString(String.format("%02d", nowUTC.getHour())));
        return row;
    }

    private static Map<String, String> getFieldNamesAndTypes(Class<?> clazz) {
        Map<String, String> fieldMap = new LinkedHashMap<>();
        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
            String fieldType;
            switch (field.getType().getSimpleName()) {
                case "String":
                    fieldType = "STRING";
                    break;
                case "int":
                case "Integer":
                    fieldType = "INT";
                    break;
                case "long":
                case "Long":
                    fieldType = "BIGINT";
                    break;
                case "double":
                case "Double":
                    fieldType = "DOUBLE";
                    break;
                case "boolean":
                case "Boolean":
                    fieldType = "BOOLEAN";
                    break;
                default:
                    fieldType = "STRING";
                    break;
            }
            fieldMap.put(field.getName().toLowerCase(), fieldType);
        });
        return fieldMap;
    }

    public static String generateIcebergTableDDL(Class<?> clazz, String tableName, List<String> partitionFields, List<String> primaryKeyFields) {
        Map<String, String> fields = getFieldNamesAndTypes(clazz);
        
        List<String> fieldDefs = new ArrayList<>();
        fields.forEach((name, dataType) -> fieldDefs.add("`" + name + "` " + dataType));
        
        List<String> partitionFieldDefs = new ArrayList<>();
        for (String field : partitionFields) {
            partitionFieldDefs.add("`" + field + "` STRING NOT NULL");
        }
        
        List<String> allFields = new ArrayList<>();
        allFields.addAll(fieldDefs);
        allFields.addAll(partitionFieldDefs);
        
        String partitionBy = partitionFields.isEmpty() ? "" : "PARTITIONED BY (" + partitionFields.stream()
            .map(field -> "`" + field + "`") // Escape field names
            .collect(Collectors.joining(", ")) + ")";
            
        String primaryKey = primaryKeyFields.isEmpty() ? "" : ",\n  PRIMARY KEY (" + String.join(", ", primaryKeyFields) + ") NOT ENFORCED";
        
        return String.format(
                "CREATE TABLE IF NOT EXISTS `%s`.`%s` (\n  %s%s\n) %s\nWITH (\n  'catalog-name' = '%s',\n  'format-version' = '3',\n  'write.format.default' = 'parquet',\n  'write.target-file-size-bytes' = '10485760',\n  'write.parquet.compression-codec' = 'snappy',\n  'warehouse' = '%s'\n);",
                FLINK_NAMESPACE, tableName, String.join(",\n  ", allFields), primaryKey, partitionBy, FLINK_CATALOG, FLINK_WAREHOUSE
        );
    }

    // private static boolean isClass(Object obj) {
    //     return obj != null && obj.getClass() != null;
    // }

    public static String generateIcebergCatalogDDL() {

        return String.format(
                "CREATE CATALOG %s WITH (\n" +
                        "  'type' = 'iceberg',\n" +
                        "  'catalog-impl' = 'org.apache.iceberg.nessie.NessieCatalog',\n" +
                        "  'uri' = '%s',\n" +
                        "  'ref' = 'main',\n" +
                        "  'warehouse' = '%s',\n" +
                        "  'fs.s3a.endpoint' = '%s',\n" +
                        "  'fs.s3a.path.style.access' = '%s',\n" +
                        "  'fs.s3a.access.key' = '%s',\n" +
                        "  'fs.s3a.secret.key' = '%s'\n" +
                        ");",
                FLINK_CATALOG, NESSIE_ENDPOINT, FLINK_WAREHOUSE, S3_ENDPOINT, S3_PATH_STYLE_ACCESS, ACCESS_KEY, SECRET_KEY
        );
    }

    public static String generateIcebergNamespaceDDL() {
        return String.format(
                "CREATE DATABASE IF NOT EXISTS %s;", 
                FLINK_NAMESPACE
        );
    }
}

