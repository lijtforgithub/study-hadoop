package com.ljt.study.mapreduce.top;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * reduce 阶段同一个月份一组
 *
 * @author LiJingTang
 * @date 2024-12-16 16:30
 */
public class AirTopGroupingComparator extends WritableComparator {

    public AirTopGroupingComparator() {
        super(AirTopMapKey.class, true);
    }

    @Override
    public int compare(WritableComparable o1, WritableComparable o2) {
        AirTopMapKey k1 = (AirTopMapKey) o1;
        AirTopMapKey k2 = (AirTopMapKey) o2;

        int y = Integer.compare(k1.getYear(), k2.getYear());
        if (y == 0) {
            return Integer.compare(k1.getMonth(), k2.getMonth());
        }

        return y;
    }

}
