package com.ljt.study.mapreduce.fof;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * @author LiJingTang
 * @date 2024-12-18 11:22
 */
public class FofLocal {

    public static void main(String[] args) throws Exception {
        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration conf = new Configuration();
        conf.set("mapreduce.framework.name", "local");
        conf.set("hadoop.tmp.dir", "/Users/lijingtang/Downloads/fof");
        Job job = Job.getInstance(conf);

        job.setJobName("fof-local");
        job.setJarByClass(FofLocal.class);

        FileInputFormat.addInputPath(job, new Path("/data/fof/input"));
        Path outpath = new Path("/data/fof/output");
        if (outpath.getFileSystem(conf).exists(outpath)) {
            outpath.getFileSystem(conf).delete(outpath, true);
        }
        FileOutputFormat.setOutputPath(job, outpath);

        job.setMapperClass(FofMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setReducerClass(FofReducer.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
