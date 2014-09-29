package com.fast.test;

/**
 * 此线程池的测试类                                                
 * @author Amylh           
 */
import org.apache.commons.pool.impl.GenericObjectPool;

public class TestThreadPool {
	public static void main(String[] args) {
		MyPoolableObjectFactory factory = new MyPoolableObjectFactory();
		GenericObjectPool pool = new GenericObjectPool(factory);
		pool.setMaxActive(20); // 能从池中借出的对象的最大数目
		pool.setMaxIdle(20); // 池中可以空闲对象的最大数目
		pool.setMaxWait(100); // 对象池空时调用borrowObject方法，最多等待多少毫秒
		pool.setTimeBetweenEvictionRunsMillis(600000);// 间隔每过多少毫秒进行一次后台对象清理的行动
		pool.setNumTestsPerEvictionRun(-1);// －1表示清理时检查所有线程
		pool.setMinEvictableIdleTimeMillis(3000);// 设定在进行后台对象清理时，休眠时间超过了3000毫秒的对象为过期
		for (int i = 0; i < 20; i++) {
			try {
				SimpleThread simpleThread = (SimpleThread) pool.borrowObject();
				simpleThread.setPool(pool);
				simpleThread.setIsRunning(true);
			} catch (Exception ex) {
				System.out.println(ex);
			}
		}
		try {
			Thread.sleep(8000); // 休息一会儿，再使用线程池
		} catch (InterruptedException ex1) {
		}
		System.out.println("------------------------------");
		for (int i = 0; i < 10; i++) {
			try {
				SimpleThread simpleThread = (SimpleThread) pool.borrowObject();
				simpleThread.setPool(pool);
				simpleThread.setIsRunning(true);
			} catch (Exception ex) {
				System.out.println(ex);
			}
		}
	}
}