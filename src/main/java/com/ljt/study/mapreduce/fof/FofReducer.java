package com.ljt.study.mapreduce.fof;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @author LiJingTang
 * @date 2024-12-18 11:15
 */
public class FofReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    private final Text rk = new Text();
    private final IntWritable rv = new IntWritable();

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
        int sum = 0;

        for (IntWritable value : values) {
            // 是直接好友
            if (value.get() == 0) {
                return;
            }
            sum += value.get();
        }
        rk.set(key.toString());
        rv.set(sum);
        context.write(rk, rv);
    }

}
