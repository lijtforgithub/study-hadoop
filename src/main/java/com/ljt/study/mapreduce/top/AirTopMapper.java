package com.ljt.study.mapreduce.top;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.time.LocalDate;

/**
 * @author LiJingTang
 * @date 2024-12-16 16:21
 */
@Slf4j
public class AirTopMapper extends Mapper<LongWritable, Text, AirTopMapKey, IntWritable> {

    private final AirTopMapKey airKey = new AirTopMapKey();
    private final IntWritable day = new IntWritable();

    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, AirTopMapKey, IntWritable>.Context context) {
        try {
            String text = value.toString();
            String[] array = text.split("\t");
            LocalDate localDate = LocalDate.parse(array[0]);
            airKey.setYear(localDate.getYear());
            airKey.setMonth(localDate.getMonthValue());
            airKey.setDay(localDate.getDayOfMonth());
            airKey.setTemperature(Integer.parseInt(array[1]));

            day.set(localDate.getDayOfMonth());

            context.write(airKey, day);
        } catch (Exception e) {
            log.error("解析数据异常 {}", value, e);
        }
    }

}
