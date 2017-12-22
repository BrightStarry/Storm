package com.zx.example2.topology;

import com.zx.example2.bolt.WordCountBolt;
import com.zx.example2.bolt.WordReportBolt;
import com.zx.example2.bolt.WordSplitBolt;
import com.zx.example2.spout.WordSpout;
import com.zx.example2.util.Utils;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;


public class WordTopology {

	//定义常量
    private static final String WORD_SPOUT_ID = "word-spout";
    private static final String SPLIT_BOLT_ID = "split-bolt";
    private static final String COUNT_BOLT_ID = "count-bolt";
    private static final String REPORT_BOLT_ID = "report-bolt";
    private static final String TOPOLOGY_NAME = "word-count-topology";

    public static void main(String[] args) throws Exception {

    	//实例化对象
        WordSpout spout = new WordSpout();
        WordSplitBolt splitBolt = new WordSplitBolt();
        WordCountBolt countBolt = new WordCountBolt();
        WordReportBolt reportBolt = new WordReportBolt();

        //构建拓扑
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(WORD_SPOUT_ID, spout);
        
        // WordSpout --> WordSplitBolt
        builder.setBolt(SPLIT_BOLT_ID, splitBolt).shuffleGrouping(WORD_SPOUT_ID);
        
        // WordSplitBolt --> WordCountBolt
        builder.setBolt(COUNT_BOLT_ID, countBolt).fieldsGrouping(SPLIT_BOLT_ID, new Fields("word"));
        
        // WordCountBolt --> WordReportBolt
        builder.setBolt(REPORT_BOLT_ID, reportBolt).globalGrouping(COUNT_BOLT_ID);

        //本地配置
        Config config = new Config();
        config.setDebug(false);
        LocalCluster cluster = new LocalCluster();

        cluster.submitTopology(TOPOLOGY_NAME, config, builder.createTopology());
        Utils.waitForSeconds(10);
        cluster.killTopology(TOPOLOGY_NAME);
        cluster.shutdown();
        
    }
}
