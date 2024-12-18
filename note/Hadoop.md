## HDFS

### 开放端口号

#### node01 

- 8020 
- 50010
- 50070

#### node02

- 50090
- 50010

#### 设置JDK

- etc/hadoop/hadoop-env.sh

  ```shell
  export JAVA_HOME=/root/java/jdk/jdk1.8.0_431
  ```

### 伪分布式

- etc/hadoop/core-site.xml

  ```xml
  <configuration>
      <property>
          <name>fs.defaultFS</name>
          <value>hdfs://node01:8020</value>
      </property>
      <property>
          <name>hadoop.tmp.dir</name>
          <value>/root/java/hadoop/local/tmp</value>
      </property>
  </configuration>
  ```

- etc/hadoop/hdfs-site.xml

  ```xml
  <configuration>
      <property>
          <name>dfs.replication</name>
          <value>1</value>
      </property>
      <!--
      <property>
          <name>dfs.datanode.hostname</name>
          <value>47.103.86.114</value>
      </property>
      -->
      <property>
          <name>dfs.namenode.name.dir</name>
          <value>/root/java/hadoop/local/name</value>
      </property>
      <property>
          <name>dfs.datanode.data.dir</name>
          <value>/root/java/hadoop/local/data</value>
      </property>
    
      <property>
          <name>dfs.namenode.checkpoint.dir</name>
          <value>/root/java/hadoop/local/secondary</value>
      </property>
    	<property>
          <name>dfs.namenode.secondary.http-address</name>
          <value>node01:50090</value>
      </property>
  </configuration>
  ```

- slaves

  ```
  node01
  ```

### 常用命令

```shell
# 第一次启动要先执行格式化 会生成name文件夹
bin/hdfs namenode -format
bin/hdfs dfs -ls /
bin/hdfs dfs -mkdir -p /user/root
bin/hdfs dfs -put ../hadoop-2.10.2.tar.gz 
# 1MB
bin/hdfs dfs -D dfs.block.size=1048576 -put ../hadoop-data-wc.txt /data/wc/input

bin/hdfs dfs -ls -R /data/top
bin/hdfs dfs -cat /data/top/output/part-r-00000


sbin/start-dfs.sh
sbin/stop-dfs.sh

for i in `seq 100000`; do echo "hello hadoop $i" >> hadoop-data-wc.txt; done

```

### 完全分布式

- etc/hadoop/core-site.xml

  ```xml
  <configuration>
      <property>
          <name>fs.defaultFS</name>
          <value>hdfs://node01:8020</value>
      </property>
      <property>
          <name>hadoop.tmp.dir</name>
          <value>/root/java/hadoop/full/tmp</value>
      </property>
  </configuration>
  ```

- etc/hadoop/hdfs-site.xml

  ```xml
  <configuration>
      <property>
          <name>dfs.replication</name>
          <value>2</value>
      </property>
   
      <property>
          <name>dfs.namenode.name.dir</name>
          <value>/root/java/hadoop/full/name</value>
      </property>
      <property>
          <name>dfs.datanode.data.dir</name>
          <value>/root/java/hadoop/full/data</value>
      </property>
    
      <property>
          <name>dfs.namenode.checkpoint.dir</name>
          <value>/root/java/hadoop/full/secondary</value>
      </property>
    	<property>
          <name>dfs.namenode.secondary.http-address</name>
          <value>node02:50090</value>
      </property>
  </configuration>
  ```

- slaves

  ```
  node01
  node02
  ```

### HA模式

解决NameNode单点问题

### 联邦模式

解决NameNode单个内存上限问题

## Yarn

### 开放端口号

#### node01

- 8088

### RM单机模式

- mapred-site.xml

  ```xml
  <configuration>
      <property>
          <name>mapreduce.framework.name</name>
          <value>yarn</value>
      </property>
  </configuration>
  ```

- yarn-site.xml

  ```xml
  <configuration>
      <property>
          <name>yarn.nodemanager.aux-services</name>
          <!--洗牌 协调map和reduce的拉取 两台机器建立一个TCP连接-->
          <value>mapreduce_shuffle</value>
      </property>
  </configuration>
  ```

### 常用命令

```shell
sbin/start-yarn.sh
sbin/stop-yarn.sh 

bin/hdfs dfs -D dfs.block.size=1048576 -put ../hadoop-data.txt /data/wc/input
bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.10.2.jar wordcount /data/wc/input /data/wc/output
bin/hadoop jar ../study-hadoop-wc.jar com.ljt.study.mapreduce.wc.WordCountJob

bin/hdfs dfs -get /data/wc/output/part-r-00000 ../

# HA模式 node02要单独启动
sbin/yarn-daemon.sh start resourcemanager
sbin/yarn-daemon.sh stop resourcemanager
```

### HA模式

> 单机模式的配置依旧配置

- yarn-site.xml

  ```xml
      <property>
        <name>yarn.resourcemanager.ha.enabled</name>
        <value>true</value>
      </property>
      <property>
        <name>yarn.resourcemanager.zk-address</name>
        <value>node02:2181</value>
      </property>
      <property>
        <name>yarn.resourcemanager.cluster-id</name>
        <value>study-yarn-ha</value>
      </property>
      <property>
        <name>yarn.resourcemanager.ha.rm-ids</name>
        <value>rm1,rm2</value>
      </property>
      <property>
        <name>yarn.resourcemanager.hostname.rm1</name>
        <value>node01</value>
      </property>
      <property>
        <name>yarn.resourcemanager.hostname.rm2</name>
        <value>node02</value>
      </property>
      <!--HA模式不配置执行mr的jar会报错-->
      <property>
        <name>yarn.resourcemanager.webapp.address.rm1</name>
        <value>node01:8088</value>
      </property>
      <property>
        <name>yarn.resourcemanager.webapp.address.rm2</name>
        <value>node02:8088</value>
      </property>
  ```

  



## Hbase

### 单机模式

- conf/hbase-site.xml

  ```xml
  <configuration>	
      <property>
          <name>hbase.cluster.distributed</name>
          <value>false</value>
      </property>
      <property>
          <name>hbase.tmp.dir</name>
          <value>./../tmp</value>
      </property>
      <property>
          <name>hbase.unsafe.stream.capability.enforce</name>
          <value>false</value>
      </property>
  </configuration>
  ```

  

### 伪分布式

- conf/hbase-env.sh

  ```shell
  export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_361.jdk/Contents/Home
  
  # 不要启用 Kerberos 认证
  export HBASE_SECURITY_AUTHENTICATION=none
  # 不要启用 SASL 认证
  export HBASE_RPC_PROTECTION=none
  ```

  

- conf/hbase-site.xml

  ```xml
  <configuration>  
      <property>
          <name>hbase.cluster.distributed</name>
          <value>true</value>
      </property>
      <property>
          <name>hbase.rootdir</name>
          <value>hdfs://47.122.0.148:8020/hbase</value>
      </property>
  </configuration>
  ```

  ```shell
  # 先开启mac的ssh （设置->共享->远程登录）
  
  bin/start-hbase.sh
  bin/stop-hbase.sh
  ```

  