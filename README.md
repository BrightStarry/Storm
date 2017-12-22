### Storm 分布式实时计算系统，用于数据实时分析、持续计算、分布式RPC等
主要解决的场景：
* 实时推荐系统，比如淘宝根据每个用户需要推荐的商品。hadoop是无法用于实时的计算，只能离线计算。
* 车流量实时计算
* 股票实时计算

架构结构
Nimbus(主节点)通过zookeeper发布任务，控制多个Supervisor(监工)，
每个Supervisor，监听任务，并控制多个Worker（工作进程）进行计算。

Topology(拓扑):storm中运行的一个实时应用程序，有向，因为各个组件间的消息流动形成逻辑上的
拓扑结构。一个Topology是spouts(管，数据源)和bolts(处理器)组成的图，通过stream groupings将spouts和
bolts连接。
---
#### Storm搭建 3台机器 http://blog.csdn.net/lnho2015/article/details/51143726
需要JDK1.6以上，Zookeeper集群，
python2.6.6以上版本（我的CentOS中默认已经有了，可以通过 python -V 查看）

1. 解压,改名，修改环境变量（使用:分隔），新建data文件夹
tar -zxvf apache-storm-1.1.0.tar.gz 
mv apache-storm-1.1.0 storm   

vim /etc/profile  
export STORM_HOME=/zx/storm/    
export PATH=$ZOOKEEPER_HOME/bin:$STORM_HOME/bin;$PATH   #在path中也加上STORM_HOME
source /etc/profile

mkdir /zx/storm/data -p 
2. 修改配置文件,注意，配置的每行语句前要有空格，以及- "192.168.2.104" ,- 后面也要有空格，以及a:冒号后面也要有空格
vim /zx/storm/conf/storm.yaml
增加如下配置：

 storm.zookeeper.servers:
     - "192.168.2.106"
     - "192.168.2.105"
     - "192.168.2.104"
 storm.zookeeper.port: 2181
 nimbus.seeds: ["192.168.2.104"]
 storm.local.dir: "/zx/storm/data"
 ui.port: 18080
 supervisor.slots.ports:
        - 6700
        - 6701
        - 6702
        - 6703
 worker.childopts: " -Xmx1024m"
 supervisor.childopts: " -Xmx1024m"
 nimbus.childopts: " -Xmx1024m"



3. 启动，一台机器启动 nimbus，其他两台启动supervisor
/zx/storm/bin/storm nimbus   #启动nimbus，主节点
/zx/storm/bin/storm supervisor   #启动supervisor，启动工作节点
/zx/storm/bin/storm ui   #启动ui,管理界面，只能主节点启动
/zx/storm/bin/storm logviewer   #查看工作日志

或（使用配置的环境变量启动） &表示后台启动

storm nimbus &
storm supervisor & 
storm ui &   
storm logviewer & 

然后在浏览器输入 [ip]:[ui.port]进入集群管理页面

4. 查看日志 
tail -f -n 300 /zx/storm/logs/nimbus.log
tail -f -n 300 /zx/storm/logs/supervisor.log
tail -f -n 300 /zx/storm/logs/ui.log

5. 开启ui后，访问192.168.2.104:18080进入控制台

org.apache.storm.utils.NimbusLeaderNotFoundException: Could not find leader nimbus from seed hosts [“127.0.0.1”]. Did you specify a valid list of nimbus hosts for config nimbus.seeds?
有个解决办法，就是到zoopeeper上把storm节点删掉，重启zookeeper 

如果还有其他什么乱七八糟的问题，就把storm/data中的数据和logs中的数据清空

---
#### 在Linux上运行storm程序
1. 将程序打成jar上传到linux ,注意，不能指定main class（就是运行那个类的main方法）
http://blog.csdn.net/x356982611/article/details/41399777
2. 执行提交命令 ,注意，需要把maven中的storm.jar依赖的scope设置成provided
storm jar /zx/Storm.jar com.zx.example.topology.PWTopology1
3. 查看当前工作的进程
storm list
4. 查看日志，在storm/logs中会多一个worker-xxx.log的日志


如果提交拓扑后，nimbus节点出现 not alive 错误
http://blog.csdn.net/w1014074794/article/details/50686697
具体来说，就是内存不足
free -m 查看内存

---
---
---
#### Storm API 详见example包
在说明一下下面的spout和bolt。相当于数据从spout从取出，然后可以提交给任意个bolt处理，
然后处理完成后，又可以给任意个bolt节点处理，直到处理完成。
（类似Disruptor，这个storm中就有引入Disruptor.jar）

---
Spout类，数据源。有两种方式：
1. 继承BaseRichSpout类
2. IRichSpout接口
需要重写或实现的方法：open(开启)、nextTuple(发送下一个数据)、declareOutputFields(声明发送的字段名转到下一个节点bolt)
---
Bolt类，处理器。有两种实现方式：
1. 继承BaseRichBolt类
2. 实现IRichBolt接口
需要重写或实现的方法：execute、declareOutputFields
---
Topology,主函数，进行提交一个任务
* 本地模式：无需storm集群，直接在java中即可运行，用于开发测试（执行main函数即可）；
* 集群模式:需要storm集群，把java程序打成jar，然后topology进行提交。使用storm命令把Topology提交到集群中去。
---

#### 设置Worker、并行度和任务数量，详见PWTopology2类
一个节点（linux机器，也可以说是jvm）相当于一个worker，一个线程池相当于一个并行度，然后一个worker可以
执行多个线程，每个线程可以执行多个任务。
---
#### Storm Grouping :详见PWTopology3类
为每个bolt（处理器）指定了应该接收哪个流（spout）作为输入，流分组定义了如何
在bolt的任务进行直接分发。
* shuffle grouping：随机分组，保证每个bolt接收到的tuple数目相同
* fields grouping：按字段分组，比如按userid分，具有同样userid的tuple（任务）
会被分配到相同的bolts
* all group:广播发送，每个tuple，会被所有bolts收到
* global grouping：全局分组，tuple被分配到storm中的一个bolt中的一个task。
* Non Grouping:无分组，和随机分组一样的效果，不同的是storm会把这个bolt放到bolt的订阅者的同一个
线程中执行
* Direct Group：直接分组，消息的发送者指定由消息接收者的那个task处理这个消息。只有
被声明为directStream的消息流（spout）可以声明这种分组方法。而且这种消息tuple必须使用
emitDirect来发送。消息处理者通过TopologyContext来获取处理它的消息的taskId
(OutputCollector.emit也会返回taskId)
---
---
### World Count 单词统计Demo 详见com.zx.example2包
简单的说，这个小例子就是每次获取一句英语句子，然后分割成单词，
然后统计每个单词出现的次数

WordTopology类：创建拓扑图
WordSpout类：（spout）数据源，提供语句
WordSplitBolt类：（bolt）分割语句成单词
WordCountBolt类：（bolt）统计单词
WordReportBolt类：（bolt）输出结果

WordSpout类 --》 WordSplitBolt类 是随机分组的分配方式
WordSplitBolt类 --》  WordCountBolt类 是按字段分组，把相同的单词分配给一个bolt
WordCountBolt类 --》 WordReportBolt类 是使用全局分组，因为这个要统计总的每个单词的出现次数
---
---
#### 可靠性
可以在实现IRichSpout接口的类中重写
ack()：发送成功方法，和fail():发送失败方法。
其中包括一整个拓扑，就是它的下一个bolt发往下下个bolt的失败也触发。

然后可以自己调用OutputCollector.ack()或fail()，触发成功或失败方法。
fail()方法中可以重发。

但是没有类似事务处理的机制。而且每次失败，不管你执行到了第几个bolt，都是
执行spout的fail()方法，所以如果想要保证不重复入库等bug，需要自行处理。
不过好像有一个IBatchSpout接口，可以批处理.
---
综上所述，其实如果在一个很大的拓扑中，有很多bolt的拓扑中。想要保证数据的
可靠性。也就是发送失败后从spout重新发送，又要确保每个bolt的处理不重复，
不产生bug，是很麻烦的一件事。

当spout发送一个消息时，分配给两个bolt分别处理，那么最后一个bolt接收的
时候会进行异或运算。
也就是说，数据每被一个bolt处理，都会生成一个id，然后如果一个数据需要被
3个bolt处理，那么这个数据的id就是a,b,c，那么在最后一bolt只要比对其id是不是
a,b,c，就可以知道这个数据是不是执行成功了。
如此类推的话，这样也可以知道这个数据在哪个bolt失败了，那么重新执行的时候，
如果又经过a这个bolt，但是a这个bolt发现这个数据是有a这个id的，就可以直接
放行。
---
---
#### Storm DRPC
http://blog.csdn.net/jmppok/article/details/16839363

差不多就是让其他机器可以访问storm集群，获取到处理后的数据
1. 修改配置文件
vim /zx/storm/conf/storm.yaml
增加
 drpc.servers:
    - "192.168.2.104"
2. 启动storm的drpc服务
storm drpc &
3. 上传对应的topology代码
storm jar /zx/Storm.jar com.zx.example.topology.PWTopology1 [传给Main的参数]
---
---
### Trident ，在Storm原生API上封装的一套框架，详见example3中的trident包
这个是 并发编程网 上的trident官方文档
http://ifeve.com/storm-trident-state/ 

http://www.aboutyun.com/thread-13479-1-1.html



