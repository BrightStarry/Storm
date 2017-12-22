package com.zx.example3.bhz.trident.strategy;

import java.io.FileWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.tuple.TridentTuple;


public class WriteFunction extends BaseFunction {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	private FileWriter writer ;

	private static final Log log = LogFactory.getLog(WriteFunction.class);
	
	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		String text = tuple.getStringByField("sub");
		try {
			if(writer == null){
				if(System.getProperty("os.name").equals("Windows 10")){
					writer = new FileWriter("D:\\099_test\\" + this);
				} else if(System.getProperty("os.name").equals("Windows 8.1")){
					writer = new FileWriter("D:\\099_test\\" + this);
				} else if(System.getProperty("os.name").equals("Windows 7")){
					writer = new FileWriter("D:\\099_test\\" + this);
				} else if(System.getProperty("os.name").equals("Linux")){
					System.out.println("----:" + System.getProperty("os.name"));
					writer = new FileWriter("/usr/local/temp/" + this);
				}
			}
			log.info("【write】： 写入文件");
			writer.write(text);
			writer.write("\n");
			writer.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
