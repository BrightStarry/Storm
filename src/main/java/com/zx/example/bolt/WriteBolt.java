package com.zx.example.bolt;

import java.io.File;
import java.io.FileWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

public class WriteBolt extends BaseBasicBolt {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(WriteBolt.class);
	
	private FileWriter writer ;
	public void execute(Tuple input, BasicOutputCollector collector) {
		//获取上一个组件所声明的Field
		String text = input.getStringByField("write");
		try {
			if(writer == null){
				//写入文件，需要现在d盘建好文件夹
				/*if(System.getProperty("os.name").equals("Windows 10")){
					writer = new FileWriter("D:\\099_test\\" + this);
				} else if(System.getProperty("os.name").equals("Windows 8.1")){
					writer = new FileWriter("D:\\099_test\\" + this);
				} else if(System.getProperty("os.name").equals("Windows 7")){
					writer = new FileWriter("D:\\099_test\\" + this);
				} else if(System.getProperty("os.name").equals("Linux")){
					System.out.println("----:" + System.getProperty("os.name"));
					writer = new FileWriter("/zx/temp/" + this);
				}*/
				System.out.println("----linux");
				writer = new FileWriter(File.separator + "zx" + File.separator
						+"temp" + File.separator + this);
			}
			log.info("【write】： 写入文件");
			writer.write(text);
			writer.write("\n");
			writer.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
	}
	


}
