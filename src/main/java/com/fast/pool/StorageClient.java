package com.fast.pool;

import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerServer;
/**
 * 封装的存储服务器访问客户端
 * @Project 	: fastdfs.pool
 * @Program Name: com.fast.pool.StorageClient.java
 * @ClassName	: StorageClient 
 * @Author 		: caozhifei 
 * @CreateDate  : 2014年8月18日 下午1:09:53
 */
public class StorageClient extends StorageClient1 {
	/**
	 * 跟踪服务器
	 */
	public TrackerServer trackerServer;
	/**
	 * 存储服务器
	 */
	public StorageServer storageServer;

	public StorageClient() {
	}

	public StorageClient(TrackerServer trackerServer,
			StorageServer storageServer) {
		super(trackerServer, storageServer);
		this.trackerServer = trackerServer;
		this.storageServer = storageServer;
	}

	public TrackerServer getTrackerServer() {
		return this.trackerServer;
	}

	public void setTrackerServer(TrackerServer trackerServer) {
		this.trackerServer = trackerServer;
	}

	public StorageServer getStorageServer() {
		return this.storageServer;
	}

	public void setStorageServer(StorageServer storageServer) {
		this.storageServer = storageServer;
	}
}
