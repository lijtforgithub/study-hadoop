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
public class WordCountJob {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "wc-job");
        job.setJarByClass(WordCountJob.class);

        Path inpath = new Path("/data/wc/input");
        FileInputFormat.addInputPath(job, inpath);
        Path outpath = new Path("/data/wc/output");
        if (outpath.getFileSystem(conf).exists(outpath)) {
            outpath.getFileSystem(conf).delete(outpath, true);
        }
        FileOutputFormat.setOutputPath(job, outpath);

        job.setMapOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
