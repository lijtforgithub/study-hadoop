package com.ljt.study.mapreduce.top;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * map 输出阶段按照年月-温度倒序
 *
 * @author LiJingTang
 * @date 2024-12-16 16:30
 */
public class AirTopSortComparator extends WritableComparator {

    public AirTopSortComparator() {
        super(AirTopMapKey.class, true);
    }

    @Override
    public int compare(WritableComparable o1, WritableComparable o2) {
        AirTopMapKey k1 = (AirTopMapKey) o1;
        AirTopMapKey k2 = (AirTopMapKey) o2;

        int y = Integer.compare(k1.getYear(), k2.getYear());
        if (y == 0) {
            int m = Integer.compare(k1.getMonth(), k2.getMonth());
            if (m == 0) {
                // 温度倒序
                return Integer.compare(k2.getTemperature(), k1.getTemperature());
            }
            return m;
        }
        return y;
    }

}
