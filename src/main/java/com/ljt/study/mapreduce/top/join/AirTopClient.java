package com.ljt.study.mapreduce.top.join;

import com.ljt.study.mapreduce.top.AirTopGroupingComparator;
import com.ljt.study.mapreduce.top.AirTopMapKey;
import com.ljt.study.mapreduce.top.AirTopPartitioner;
import com.ljt.study.mapreduce.top.AirTopSortComparator;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * @author LiJingTang
 * @date 2024-12-16 16:15
 */
@Slf4j
public class AirTopClient {

    public static void main(String[] args) throws Exception {
        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration conf = new Configuration();
        conf.set("mapreduce.framework.name", "local");
        conf.set("hadoop.tmp.dir", "/Users/lijingtang/Downloads/top");
//        conf.set("mapreduce.am.resource.mb", "1024");
        Job job = Job.getInstance(conf);

        job.setJobName("air-top-client");
        job.setJarByClass(AirTopClient.class);
//        job.setJar("/Users/lijingtang/workspace/study/study-hadoop/target/study-hadoop-0.0.1-SNAPSHOT.jar");

        /**
         * MapTask
         *
         * input
         * key
         * map
         * partitioner 分区
         * sortComparator 组排序
         *
         * combine 合并
         */

        job.setMapperClass(AirTopMapper2.class);
        job.setMapOutputKeyClass(AirTopMapKey.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setPartitionerClass(AirTopPartitioner.class);
        job.setSortComparatorClass(AirTopSortComparator.class);
//        job.setCombinerClass();
        FileInputFormat.addInputPath(job, new Path("/data/top/input"));
        Path outpath = new Path("/data/top/output2");
        if (outpath.getFileSystem(conf).exists(outpath)) {
            outpath.getFileSystem(conf).delete(outpath, true);
        }
        FileOutputFormat.setOutputPath(job, outpath);
        job.addCacheFile(new Path("/data/top/hadoop-data-top-city.txt").toUri());


        /**
         * ReduceTask
         *
         * shuffle 洗牌
         * groupingComparator map数据来自多个节点 再reduce之前相同的key要合并到一起
         * reduce
         */
        job.setReducerClass(AirTopReducer2.class);
        job.setGroupingComparatorClass(AirTopGroupingComparator.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
