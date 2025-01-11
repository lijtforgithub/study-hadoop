

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
      <!--HA模式不配置下面执行mr的jar会报错-->
      <property>
        <name>yarn.resourcemanager.webapp.address.rm1</name>
        <value>node01:8088</value>
      </property>
      <property>
        <name>yarn.resourcemanager.webapp.address.rm2</name>
        <value>node02:8088</value>
      </property>
  ```


## Hive

数据仓库

- 内部表和外部表
  		hive内部表创建的时候数据存储在hive的默认存储目录中，外部表在创建的时候需要制定额外的目录。
    		hive内部表删除的时候，会将元数据和数据都删除，而外部表只会删除元数据，不会删除数据。
  应用场景:
  		内部表：需要先创建表，然后向表中添加数据，适合做中间表的存储。
  		外部表：可以先创建表，再添加数据，也可以先有数据，再创建表，本质上是将hdfs的某一个目录的数据跟hive的表关联映射起来，因此适合原始数据的存储，不会因为误操作将数据给删除掉。

- 分区
  		hive默认将表的数据保存在某一个hdfs的存储目录下，当需要检索符合条件的某一部分数据的时候，需要全量遍历数据，IO量比较大，效率比较低，因此可以采用分而治之的思想，将符合某些条件的数据放置在某一个目录		此时检索的时候只需要搜索指定目录即可，不需要全量遍历数据。

   		添加分区列的值的时候，如果定义的是多分区表，那么必须给所有的分区列都赋值；删除分区列的值的时候，无论是单分区表还是多分区表，都可以将指定的分区进行删除。

- 动态分区
- 分桶：分桶是根据字段值取hash；分区是根据字段值。 MySQL分表中有根据年份分表或者根据用户ID取hash分表。

### MySQL

```shell
# 更换阿里云镜像
cp /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.bak
wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo

# 下载 MySQL Yum 仓库配置文件
wget https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm
# 安装 MySQL Yum 仓库配置文件
rpm -ivh mysql80-community-release-el7-3.noarch.rpm
# 安装 MySQL 5.7 （yum install mysql-community-server）
yum-config-manager --disable mysql80-community
yum-config-manager --enable mysql57-community
# 更新缓存 yum-config-manager --save --setopt=mysql57-community.gpgcheck=0
yum clean all
yum makecache

yum install mysql-community-server
systemctl start mysqld

# 查找默认密码
cat /var/log/mysqld.log | grep 'temporary password'

mysql
use mysql
select host,user from user;
# UNINSTALL PLUGIN validate_password; 卸载密码校验插件
grant all privileges on *.* to root@'%' identified by 'admin' with grant option;
flush privileges;
delete from user where host = 'localhost';
# 开机启动
systemctl enable mysqld.service
```

### 直连MySQL

- mv hive-default.xml.template hive-site.xml

- 末行模式 

  ```shell
   :.,$-1d
  :!echo $JAVA_HOME
  ```

```xml
    <property>
      <name>hive.metastore.warehouse.dir</name>
      <value>/user/hive/warehouse</value>
    </property>
    <property>
      <name>javax.jdo.option.ConnectionURL</name>
      <value>jdbc:mysql://node02:3306/hive?createDatabaseIfNotExist=true</value>
    </property>
		<!--上传mysql驱动包-->
    <property>
      <name>javax.jdo.option.ConnectionDriverName</name>
      <value>com.mysql.jdbc.Driver</value>
    </property>
    <property>
      <name>javax.jdo.option.ConnectionUserName</name>
      <value>root</value>
    </property>
    <property>
      <name>javax.jdo.option.ConnectionPassword</name>
      <value>admin</value>
    </property>

		<property>
  			<name>hive.exec.scratchdir</name>
  			<value>/root/java/hive/tmp/exec</value>
		</property>
```

```shell
bin/schematool -dbType mysql -initSchema

bin/hive

bin/hiveserver2
```



```hive
create database test_hive;
use test_hive;

create table p (id int, name string, likes array<string>, address map<string, string>)
row format delimited fields terminated by ',' collection items terminated by '-' map keys terminated by ':';
	
desc p;
desc formatted p;
	
insert into p(id, name) values(1, "SQL插入");
-- array类型、map类型
insert into p(id, name, likes, address) select 2, "SQL插入", array("movie", "book"), map("阜阳", "口孜", "合肥", "航空新城");

create table p1 (id int, name string);
-- overwrite会先清空原来的数据
insert overwrite table p1 SELECT id, name FROM p;

create table p2 (id int, name string);
create table p3 (id int);

from p
insert overwrite table p2 select id, name 
insert into table p3 select id;


-- 加载本地数据到hive表
load data local inpath "/root/java/hive/hive-data-p.txt" into table p;
-- 加载hdfs数据文件到hive表
load data inpath '/data/hive-data-p.txt' into table p;

hive -S -e 'show tables'
```

### HiveServer2

- 修改hdfs的core-site.xml

  ```xml
  		<!--hiveServer2-->
      <property>
        <name>hadoop.proxyuser.root.groups</name>
        <value>*</value>
      </property>
      <property>
        <name>hadoop.proxyuser.root.hosts</name>
        <value>*</value>
      </property>
  ```

- 刷新权限

  ```shell
  hdfs dfsadmin -fs hdfs://node01:8020 -refreshSuperUserGroupsConfiguration
  
  beeline
  !connect jdbc:hive2://node01:10000/test_hive root 123
  ```

- beeline

  1. beeline -u jdbc:hive2://node01:10000/test_hive -n root

  2. beeline
     		beeline> !connect jdbc:hive2://node01:10000/test_hive root 123

​		使用beeline方式登录的时候，默认的用户名和密码是不验证的，也就是说随便写用户名和密码即可。使用第一种beeline的方式访问的时候，用户名和密码可以不输入；使用第二种beeline方式访问的时候，必须输入用户名和密码，用户名和密码是什么无所谓。

### WordCount

```hive
-- 先上传文件到hdfs
use test_hive;
create external table wc(line string) location '/data/wc/input';
select * from wc;

select explode(split(line, ' ')) from wc;

create table wc_result(word string, count int);
from (select explode(split(line, ' ')) word from wc) t insert into wc_result select word, count(word) group by t.word;
```



## Hbase

### 开放端口号

#### node01

- 16000

- 16010
- 16020

#### node02

- 16010
- 16020

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
  export HBASE_MANAGES_ZK=false
  ```
  
  
  
- conf/hbase-site.xml

  ```xml
  <configuration>  
      <property>
          <name>hbase.cluster.distributed</name>
          <value>false</value>
      </property>
      <property>
          <name>hbase.rootdir</name>
          <value>hdfs://node01:8020/hbase</value>
      </property>
  </configuration>
  ```

  ```shell
  # 先开启mac的ssh （设置->共享->远程登录）
  
  bin/start-hbase.sh
  bin/stop-hbase.sh
  
  bin/hbase shell
  bin/hbase hfile -p -f ../tmp/hbase/data/default/p1/532ddddb8906e0d9e9601ec3ba215a50/cf1/b14fec216a5541f790ed50b14da32d95
  ```
  
  

### 完全分布式

- conf/hbase-env.sh

  ```shell
  export JAVA_HOME=/root/java/jdk/jdk1.8.0_431
  export HBASE_MANAGES_ZK=false
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
          <value>hdfs://node01:8020/hbase</value>
      </property>
    	<property>
      		<name>hbase.zookeeper.quorum</name>
      		<value>node02</value>
    	</property>
  </configuration>
  ```

- conf/regionservers

  ```
  node01
  node02
  ```

- conf/backup-masters

  ```
  node02
  ```

- copy of *hdfs-site.xml* (or *hadoop-site.xml*) or, better, symlinks, under *${HBASE_HOME}/conf*

  ```shell
   cp /root/java/hadoop/hadoop-full/etc/hadoop/hdfs-site.xml ./
  ```

