package com.fast.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 日志打印类
 * 通过在log4j中配置imageServerLogger，当连接出现问题时，可方便查看日志。
 * @author caozf
 *
 */
public class ImageServerPoolSysout {
	
	private static Logger logger=LoggerFactory.getLogger(ImageServerPoolSysout.class);
	
	public static void info(Object o){
		logger.info(o.toString());
		//System.out.println(o);
	}
	public static void warn(Object o) {
		logger.warn(o.toString());
		//System.err.println(o);
	}
}
