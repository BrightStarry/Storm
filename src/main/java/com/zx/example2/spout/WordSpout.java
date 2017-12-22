package com.zx.example2.spout;

import java.util.Map;
import java.util.UUID;

import com.zx.example2.util.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class WordSpout implements IRichSpout {

	private static final long serialVersionUID = 1L;
	
	private SpoutOutputCollector collector;
	 
	private int index = 0;
	
    private String[] sentences = {
            "my dog has fleas",
            "i like cold beverages",
            "the dog ate my homework",
            "don't have a cow man",
            "i don't think i like fleas"
    };
	
	@Override
	public void open(Map config, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;
	}
	
	@Override
	public void nextTuple() {
		this.collector.emit(new Values(sentences[index]));
        index++;
        if (index >= sentences.length) {
            index = 0;
        }
        Utils.waitForSeconds(1);
	}
	

	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("sentence"));
	}

	/**
	 * 发送成功执行的方法
	 *
	 */
	@Override
	public void ack(Object msgId) {
	}

	/**
	 * 发送失败执行的方法
	 * 包括一整个拓扑，就是它的下一个bolt发往下下个bolt的失败也触发
	 */
	@Override
	public void fail(Object msgId) {
	}

	@Override
	public void activate() {
		
	}

	@Override
	public void close() {
		
	}

	@Override
	public void deactivate() {
		
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
