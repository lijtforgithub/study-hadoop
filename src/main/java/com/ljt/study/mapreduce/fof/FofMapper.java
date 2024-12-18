package com.ljt.study.mapreduce.fof;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * @author LiJingTang
 * @date 2024-12-18 11:14
 */
public class FofMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final Text mk = new Text();
    private final IntWritable mv = new IntWritable(1);

    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context) throws IOException, InterruptedException {
        String[] array = value.toString().split(" ");
        for (int i = 1; i < array.length; i++) {
            mk.set(sort(array[0], array[i]));
            // 直接好友
            mv.set(0);
            context.write(mk, mv);
            for (int j = i + 1; j < array.length; j++) {
                mk.set(sort(array[i], array[j]));
                // 间接好友
                mv.set(1);
                context.write(mk, mv);
            }
        }
    }

    private String sort(String s1, String s2) {
        if (s1.compareTo(s2) < 0) {
            return s1 + "-" + s2;
        } else {
            return s2 + "-" + s1;
        }
    }

}
