package com.zx.example.topology;

import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import com.zx.example.bolt.PrintBolt;
import com.zx.example.bolt.WriteBolt;
import com.zx.example.spout.PWSpout;


public class PWTopology1 {

	public static void main(String[] args) throws Exception {
		//
		Config cfg = new Config();
		//开启几个worker（jvm）去运行
		//就是几台机器去运行
		cfg.setNumWorkers(2);
		cfg.setDebug(true);

		/**
		 * 通过TopologyBuilder创建Topology类，并在TopologyBuilder
		 * 中设置好对应的流程走向。
		 */
		TopologyBuilder builder = new TopologyBuilder();
		//设置数据源
		builder.setSpout("spout", new PWSpout());
		//设置处理器，在spout之后执行这个处理器
		builder.setBolt("print-bolt", new PrintBolt()).shuffleGrouping("spout");
		//设置处理器，在print bolt之后执行这个处理器
		builder.setBolt("write-bolt", new WriteBolt()).shuffleGrouping("print-bolt");
		
		
		//1 本地模式
//		LocalCluster cluster = new LocalCluster();
//		//提交拓扑任务，参数1：拓扑名字，参数2：storm相关配置，参数3：topology对象
//		cluster.submitTopology("top1", cfg, builder.createTopology());
//		Thread.sleep(10000);
//		//停止刚才的topology，如果不停止，会一直执行spout的nextTuple()方法去获取数据
//		cluster.killTopology("top1");
//		cluster.shutdown();
		
		//2 集群模式
		StormSubmitter.submitTopology("top1", cfg, builder.createTopology());
		
	}
}
