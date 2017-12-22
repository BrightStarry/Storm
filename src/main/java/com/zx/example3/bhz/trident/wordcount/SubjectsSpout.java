package com.zx.example3.bhz.trident.wordcount;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.spout.IBatchSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <B>系统名称：</B>SubjectsSpout<BR>
 * <B>模块名称：</B>SubjectsSpout<BR>
 * <B>中文类名：</B>SubjectsSpout<BR>
 * <B>概要说明：</B>SubjectsSpout<BR>
 * @author bhz（Alienware）
 * @since 2016年4月8日
 */
public class SubjectsSpout implements IBatchSpout {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	//批处理大小
	private int batchSize;
	//容器
	private HashMap<Long, List<List<Object>>> batchesMap = new HashMap<Long, List<List<Object>>>();
	
	public SubjectsSpout(int batchSize){
		this.batchSize = batchSize;
	}
	
	private static final Map<Integer, String> DATA_MAP = new HashMap<Integer, String>();

	static {
		DATA_MAP.put(0, "java java php ruby c++");
		DATA_MAP.put(1, "java python python python c++");
		DATA_MAP.put(2, "java java java java ruby");
		DATA_MAP.put(3, "c++ java ruby php java");
	}
	
	@Override
	public void open(Map conf, TopologyContext context) {
		// TODO Auto-generated method stub
	}

	@Override
	public void emitBatch(long batchId, TridentCollector collector) {
		List<List<Object>> batches = new ArrayList<List<Object>>();
		for (int i= 0; i < this.batchSize; i++) {
			batches.add(new Values(DATA_MAP.get(i)));
		}		
		System.out.println("batchId: " + batchId);
		this.batchesMap.put(batchId, batches);
		for(List<Object> list : batches){
            collector.emit(list);
        }
	}
	
	@Override
	public Fields getOutputFields() {
		return new Fields("subjects");
	}

	@Override
	public void ack(long batchId) {
		System.out.println("remove batchId:" + batchId);
		this.batchesMap.remove(batchId);		
	}
	
	@Override
	public Map getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

}
