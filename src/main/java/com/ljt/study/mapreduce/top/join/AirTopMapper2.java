package com.ljt.study.mapreduce.top.join;

import com.ljt.study.mapreduce.top.AirTopMapKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LiJingTang
 * @date 2024-12-16 16:21
 */
@Slf4j
public class AirTopMapper2 extends Mapper<LongWritable, Text, AirTopMapKey, IntWritable> {

    private final AirTopMapKey airKey = new AirTopMapKey();
    private final IntWritable day = new IntWritable();

    private final Map<String, String> cityMap = new HashMap<>();

    @Override
    protected void setup(Mapper<LongWritable, Text, AirTopMapKey, IntWritable>.Context context) throws IOException, InterruptedException {
        URI[] files = context.getCacheFiles();
        for (URI uri : files) {
            if (uri.getPath().endsWith("hadoop-data-top-city.txt")) {
                Path path = new Path(uri.getPath());
                File file = new File(path.getName());
                log.info("读取文件开始 {} {}", file.getPath(), file.exists());
                if (file.exists()) {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] array = line.split("\t");
                        cityMap.put(array[0], array[1]);
                    }
                    log.info("读取文件结束 {}", cityMap);
                }

            }
        }
    }

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

            airKey.setCity(cityMap.get(array[2]));

            day.set(localDate.getDayOfMonth());

            context.write(airKey, day);
        } catch (Exception e) {
            log.error("解析数据异常 {}", value, e);
        }
    }

}
