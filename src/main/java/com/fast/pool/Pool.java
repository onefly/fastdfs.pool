package com.fast.pool;
/**
 * 连接池接口
 * @Project 	: fastdfs.pool
 * @Program Name: com.fast.pool.Pool.java
 * @ClassName	: Pool 
 * @Author 		: caozhifei 
 * @CreateDate  : 2014年8月18日 上午11:52:52
 */
public interface Pool {
	StorageClient getResource() throws Exception;
	void returnResource(StorageClient resource) throws Exception;
	void returnBrokenResource(StorageClient resource) throws Exception;
	void destroy() throws Exception;
}
