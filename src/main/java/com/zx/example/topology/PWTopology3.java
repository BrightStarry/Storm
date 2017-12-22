package com.zx.example.topology;

import org.apache.storm.Config;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import com.zx.example.bolt.PrintBolt;
import com.zx.example.bolt.WriteBolt;
import com.zx.example.spout.PWSpout;


public class PWTopology3 {

	public static void main(String[] args) throws Exception {
		//
		Config cfg = new Config();
		cfg.setNumWorkers(2);
		cfg.setDebug(true);
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("spout", new PWSpout(), 4);
		builder.setBolt("print-bolt", new PrintBolt(), 4).shuffleGrouping("spout");
		//设置字段分组
		builder.setBolt("write-bolt", new WriteBolt(), 8).fieldsGrouping("print-bolt", new Fields("write"));
		//设置广播分组
		//builder.setBolt("write-bolt", new WriteBolt(), 4).allGrouping("print-bolt");
		//设置全局分组
		//builder.setBolt("write-bolt", new WriteBolt(), 4).globalGrouping("print-bolt");

		//1 本地模式
//		cfg.setDebug(false);
//		LocalCluster cluster = new LocalCluster();
//		cluster.submitTopology("top3", cfg, builder.createTopology());
//		Thread.sleep(10000);
//		cluster.killTopology("top3");
//		cluster.shutdown();
		
		//2 集群模式
//		StormSubmitter.submitTopology("top3", cfg, builder.createTopology());
		
	}
}
