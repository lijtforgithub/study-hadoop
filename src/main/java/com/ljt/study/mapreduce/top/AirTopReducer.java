package com.ljt.study.mapreduce.top;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.time.LocalDate;

/**
 * @author LiJingTang
 * @date 2024-12-16 16:21
 */
public class AirTopReducer extends Reducer<AirTopMapKey, IntWritable, Text, IntWritable> {

    private final Text outKey = new Text();
    private final IntWritable outValue = new IntWritable();

    @Override
    protected void reduce(AirTopMapKey key, Iterable<IntWritable> values,
                          Reducer<AirTopMapKey, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
        int index = 0;
        Integer day = null;

        for (IntWritable val : values) {
            if (0 == index) {
                write(context, key);
                day = key.getDay();
            }
            if (index > 0 && !day.equals(val.get())) {
                write(context, key);
                return;
            }
            index++;
        }
    }

    private void write(Reducer<AirTopMapKey, IntWritable, Text, IntWritable>.Context context, AirTopMapKey key) throws IOException, InterruptedException {
        LocalDate localDate = LocalDate.of(key.getYear(), key.getMonth(), key.getDay());
        outKey.set(localDate.toString());
        outValue.set(key.getTemperature());
        context.write(outKey, outValue);
    }

}
