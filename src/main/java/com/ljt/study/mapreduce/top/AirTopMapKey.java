package com.ljt.study.mapreduce.top;

import lombok.Data;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2024-12-16 16:20
 */
@Data
public class AirTopMapKey implements WritableComparable<AirTopMapKey> {

    private Integer year;
    private Integer month;
    private Integer day;
    private Integer temperature;

    private String city;
    /**
     * 正常排序规则
     */
    @Override
    public int compareTo(AirTopMapKey o) {
        int y = this.year.compareTo(o.year);
        if (y == 0) {
            int m = this.month.compareTo(o.month);
            if (m == 0) {
                return this.day.compareTo(o.day);
            }

            return m;
        }

        return y;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(year);
        dataOutput.writeInt(month);
        dataOutput.writeInt(day);
        dataOutput.writeInt(temperature);

        if (Objects.isNull(city)) {
            city = "";
        }
        dataOutput.writeUTF(city);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.year = dataInput.readInt();
        this.month = dataInput.readInt();
        this.day = dataInput.readInt();
        this.temperature = dataInput.readInt();

        this.city = dataInput.readUTF();
    }

}
