package com.ljt.study.mapreduce.wc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * @author LiJingTang
 * @date 2024-12-09 11:45
 */
public class WordCountLocal {

    public static void main(String[] args) throws Exception {
        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration conf = new Configuration();
        conf.set("mapreduce.framework.name", "local");
        conf.set("hadoop.tmp.dir", "/Users/lijingtang/Downloads/java/hadoop/wc");

        Job job = Job.getInstance(conf, "wc-local-job");
        job.setJarByClass(WordCountLocal.class);

        Path inpath = new Path("/data/wc/input");
        FileInputFormat.addInputPath(job, inpath);
        Path outpath = new Path("/data/wc/local");
        if (outpath.getFileSystem(conf).exists(outpath)) {
            outpath.getFileSystem(conf).delete(outpath, true);
        }
        FileOutputFormat.setOutputPath(job, outpath);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
