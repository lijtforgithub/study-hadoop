package com.ljt.study.hbase.client;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * @author LiJingTang
 * @date 2024-11-12 14:34
 */
public class HbaseTest {

    private static Connection connection;

    public static void main(String[] args) {
        try {
            // 创建 HBase 配置
            Configuration config = HBaseConfiguration.create();
            config.set("hbase.zookeeper.quorum", "localhost"); // 替换为你的 ZooKeeper 地址
            config.set("hbase.zookeeper.property.clientPort", "2181"); // 替换为你的 ZooKeeper 端口

            // 创建连接
            connection = ConnectionFactory.createConnection(config);

            // 创建表
            createTable("users", "info");

            // 插入数据
            insertData("users", "1", "info", "name", "John Doe");
            insertData("users", "1", "info", "email", "john@example.com");

            // 查询数据
            getData("users", "1");

            // 删除数据
            deleteData("users", "1");

            // 删除表
            deleteTable("users");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createTable(String tableName, String... columnFamilies) throws IOException {
        Admin admin = connection.getAdmin();
        TableName tn = TableName.valueOf(tableName);
        if (admin.tableExists(tn)) {
            System.out.println("Table already exists");
            return;
        }

        TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(tn);
        for (String cf : columnFamilies) {
            builder.setColumnFamily(ColumnFamilyDescriptorBuilder.of(cf));
        }
        admin.createTable(builder.build());
        System.out.println("Table created");
    }

    public static void insertData(String tableName, String rowKey, String family, String qualifier, String value) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
        table.put(put);
        table.close();
        System.out.println("Data inserted");
    }

    public static void getData(String tableName, String rowKey) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(Bytes.toBytes(rowKey));
        // 服务端过滤字段 缩小IO
//        get.addFamily()
//        get.addColumn()
        Result result = table.get(get);
        for (Cell cell : result.rawCells()) {
            System.out.println("Row Key: " + Bytes.toString(result.getRow()));
            System.out.println("Column Family: " + Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength()));
            System.out.println("Column Qualifier: " + Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()));
//            byte[] bytes = CellUtil.cloneValue(cell);
            System.out.println("Value: " + Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
        }
        table.close();
    }

    public static void deleteData(String tableName, String rowKey) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        table.delete(delete);
        table.close();
        System.out.println("Data deleted");
    }

    public static void deleteTable(String tableName) throws IOException {
        Admin admin = connection.getAdmin();
        TableName tn = TableName.valueOf(tableName);
        if (admin.tableExists(tn)) {
            admin.disableTable(tn);
            admin.deleteTable(tn);
            System.out.println("Table deleted");
        } else {
            System.out.println("Table does not exist");
        }
    }

}
