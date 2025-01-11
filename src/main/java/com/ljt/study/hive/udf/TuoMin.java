package com.ljt.study.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

/**
 * jar包上传到hdfs
 *
 * @author LiJingTang
 * @date 2024-12-24 18:42
 */
public class TuoMin extends UDF {

    public Text evaluate(final Text s) {
        if (s == null) {
            return null;
        }
        String str = s.toString().substring(0, 1) + "udf";
        return new Text(str);
    }

}
