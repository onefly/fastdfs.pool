package com.fast.pool;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 连接池适配器抽象类
 * @Project 	: fastdfs.pool
 * @Program Name: com.fast.pool.PoolAdapter.java
 * @ClassName	: PoolAdapter 
 * @Author 		: caozhifei 
 * @CreateDate  : 2014年8月18日 上午11:55:21
 */
public abstract class PoolAdapter implements Pool{
	private final static Logger log = LoggerFactory.getLogger(PoolAdapter.class);
	private final GenericObjectPool internalPool;

	public PoolAdapter(Config poolConfig,
			PoolableObjectFactory factory) {
		this.internalPool = new GenericObjectPool(factory, poolConfig);
	}

	public StorageClient getResource() throws Exception {
		try {
			return (StorageClient) this.internalPool.borrowObject();
		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new Exception("Could not get a resource from the pool", e);
		}

	}

	public void returnResource(StorageClient resource) throws Exception {
		try {
			this.internalPool.returnObject(resource);
		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new Exception("Could not return the resource to the pool", e);
		}
	}

	public void returnBrokenResource(StorageClient resource) throws Exception {
		try {
			this.internalPool.invalidateObject(resource);
		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new Exception("Could not return the resource to the pool", e);
		}
	}

	public void destroy() throws Exception {
		try {
			this.internalPool.close();
		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new Exception("Could not destroy the pool", e);
		}
	}
}