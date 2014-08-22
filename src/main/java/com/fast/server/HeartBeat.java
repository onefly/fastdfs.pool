package com.fast.server;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 心跳包实现类
 * @Project 	: fastdfs.pool
 * @Program Name: com.fast.server.HeartBeat.java
 * @ClassName	: HeartBeat 
 * @Author 		: caozhifei 
 * @CreateDate  : 2014年8月22日 上午10:14:09
 */
class HeartBeat {
	private static final Logger log = LoggerFactory.getLogger(HeartBeat.class);
	private ConnectionPool pool = null;

	public HeartBeat(ConnectionPool pool) {
		this.pool = pool;
	}

	public void beat() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				TrackerServer ts = null;
				log.debug("ConnectionPool execution HeartBeat to fdfs server,have size:"
						+ (pool.getIdleConnectionPool().size()
								+ pool.getBusyConnectionPool().size()
								+ ", IdleConnectionPool has size:" + pool
								.getIdleConnectionPool().size()));
				for (int i = 0; i < pool.getIdleConnectionPool().size(); i++) {
					try {
						ts = pool.checkout(100);
						org.csource.fastdfs.ProtoCommon.activeTest(ts
								.getSocket());
						pool.checkin(ts);
					} catch (InterruptedException e) {
						log.error("HeartBeat execution beat throw :", e);
					} catch (NullPointerException e) {
						// 代表已经没有空闲长连接
						log.error("HeartBeat execution beat throw :", e);
						break;
					} catch (IOException e) {
						// 发生异常,要删除，进行重建
						log.error("HeartBeat execution beat throw :", e);
						pool.drop(ts);
					}
				}
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, ahour, ahour);
	}

	public static int ahour = 1000 * 60 * 60 * 1;
	public static int waitTimes = 0;

	/**
	 * 单位为秒
	 */
	public void setHeartbeatTime(int time) {
		ahour = time * 1000;
	}
}
