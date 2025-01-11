package com.ljt.study.hdfs.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.net.URI;

/**
 * @author LiJingTang
 * @date 2024-12-03 09:20
 */
@Slf4j
public class HdfsTest {

    private static FileSystem fs;


    @BeforeAll
    static void init() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt("dfs.block.size", 1048576);
        URI uri = URI.create("hdfs://node01:8020");
        String user = "root";
        // 这种方式指定用户最为灵活 环境变量HADOOP_USER_NAME不灵活
        fs = FileSystem.get(uri, conf, user);
    }

    @AfterAll
    static void close() throws Exception {
        fs.close();
    }

    @Test
    void list() throws Exception {
        Path path = new Path("/");
        RemoteIterator<LocatedFileStatus> it = fs.listFiles(path, true);
        while (it.hasNext()) {
            LocatedFileStatus next = it.next();
            log.info("{} 存在文件: {}", path, next);
        }
    }

    @Test
    void mkdir() throws Exception {
        Path path = new Path("/user/root/temp");
        boolean exists = fs.exists(path);
        if (exists) {
            fs.delete(path, true);
        }

        fs.mkdirs(path);
        log.info("创建目录成功 {}", path);
    }

    @Test
    void upload() throws Exception {
        Path file = new Path("app.yml");
        FSDataOutputStream output = fs.create(file, true);

        ClassPathResource resource = new ClassPathResource("application.yml");
        IOUtils.copyBytes(resource.getInputStream(), output, 4096, true);

        log.info("上传文件成功 {} {}", file, output.size());
    }

    @Test
    void download() throws Exception {
        Path file = new Path("/user/root/hadoop-data.txt");
        FileStatus fileStatus = fs.getFileStatus(file);
        if (!fileStatus.isFile()) {
            return;
        }

        FSDataInputStream input = fs.open(file);
        BlockLocation[] blList = fs.getFileBlockLocations(fileStatus, 0, fileStatus.getLen());
        BlockLocation last = blList[blList.length - 1];
        input.seek(last.getOffset());

        log.info("读取最后一个块 {}", (char)input.read());
        log.info("{}", (char)input.read());
        log.info("{}", (char)input.read());
        log.info("{}", (char)input.read());
        log.info("{}", (char)input.read());
        log.info("{}", (char)input.read());
        log.info("{}", (char)input.read());
        log.info("{}", (char)input.read());
        log.info("{}", (char)input.read());
        log.info("{}", (char)input.read());
    }

}
