package com.ljt.study.mapreduce.wc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * @author LiJingTang
 * @date 2024-12-09 11:45
 */
public class WordCountClient {

    public static void main(String[] args) throws Exception {
        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration conf = new Configuration();
        GenericOptionsParser parser = new GenericOptionsParser(conf, args);
        String[] otherArgs = parser.getRemainingArgs();
        conf.set("mapreduce.am.resource.mb", "1024");

        Job job = Job.getInstance(conf, "wc-client-job");
        job.setJarByClass(WordCountClient.class);
        job.setJar("/Users/lijingtang/workspace/study/study-hadoop/target/study-hadoop-0.0.1-SNAPSHOT.jar");

        Path inpath = new Path("/data/wc/input");
        FileInputFormat.addInputPath(job, inpath);
        Path outpath = new Path("/data/wc/client");
        if (outpath.getFileSystem(conf).exists(outpath)) {
            outpath.getFileSystem(conf).delete(outpath, true);
        }
        FileOutputFormat.setOutputPath(job, outpath);

        job.setMapOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);

//        job.setNumReduceTasks();

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
