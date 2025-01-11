package com.ljt.study.mapreduce.top;

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
public class AirTopLocal {

    public static void main(String[] args) throws Exception {
        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration conf = new Configuration();
        conf.set("mapreduce.framework.name", "local");
        conf.set("hadoop.tmp.dir", "/Users/lijingtang/Downloads/java/hadoop/top");
        Job job = Job.getInstance(conf);

        job.setJobName("air-top-local");
        job.setJarByClass(AirTopLocal.class);

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

        job.setMapperClass(AirTopMapper.class);
        job.setMapOutputKeyClass(AirTopMapKey.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setPartitionerClass(AirTopPartitioner.class);
        job.setSortComparatorClass(AirTopSortComparator.class);
//        job.setCombinerClass();
        FileInputFormat.addInputPath(job, new Path("/data/top/input"));
        Path outpath = new Path("/data/top/output");
        if (outpath.getFileSystem(conf).exists(outpath)) {
            outpath.getFileSystem(conf).delete(outpath, true);
        }
        FileOutputFormat.setOutputPath(job, outpath);


        /**
         * ReduceTask
         *
         * shuffle 洗牌
         * groupingComparator map数据来自多个节点 再reduce之前相同的key要合并到一起
         * reduce
         */
        job.setReducerClass(AirTopReducer.class);
        job.setGroupingComparatorClass(AirTopGroupingComparator.class);
//        job.setNumReduceTasks(0);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
