package com.ljt.study.mapreduce.top;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Partitioner;

/**
 * 按照年分区 一个分区只会到一个reducer
 * 数据可能倾斜
 *
 * @author LiJingTang
 * @date 2024-12-16 16:30
 */
public class AirTopPartitioner extends Partitioner<AirTopMapKey, IntWritable> {

    @Override
    public int getPartition(AirTopMapKey airTopMapKey, IntWritable intWritable, int numPartitions) {
        return airTopMapKey.getYear() % numPartitions;
    }

}
