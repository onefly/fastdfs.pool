package com.fast.pool;

import java.io.File;
import java.io.IOException;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.ProtoCommon;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
/**
 * 连接池实现类
 * @Project 	: fastdfs.pool
 * @Program Name: com.fast.pool.FastdfsPool.java
 * @ClassName	: FastdfsPool 
 * @Author 		: caozhifei 
 * @CreateDate  : 2014年8月18日 下午1:10:48
 */
public class FastdfsPool extends PoolAdapter {
	public FastdfsPool(Config poolConfig, PoolableObjectFactory factory) {
		super(poolConfig, factory);
	}

	public FastdfsPool(Config poolConfig) {
		super(poolConfig, new FastdfsClientFactory());
	}

	private static class FastdfsClientFactory extends BasePoolableObjectFactory {
		public Object makeObject() throws Exception {
			String classPath = new File(getClass().getResource("/").getFile())
					.getCanonicalPath();
			String configFilePath = classPath + File.separator
					+ "fdfs_client.conf";
			ClientGlobal.init(configFilePath);
			TrackerClient tracker = new TrackerClient();
			TrackerServer trackerServer = tracker.getConnection();
			StorageServer storageServer = null;
			StorageClient client = new StorageClient(trackerServer,
					storageServer);
			return client;
		}

		public void destroyObject(Object obj) throws Exception {
			if ((obj != null) && ((obj instanceof StorageClient))) {
				StorageClient storageClient = (StorageClient) obj;
				TrackerServer trackerServer = storageClient.getTrackerServer();
				StorageServer storageServer = storageClient.getStorageServer();
				trackerServer.close();
				storageServer.close();
			}
		}

		public boolean validateObject(Object obj) {
			StorageClient storageClient = (StorageClient) obj;
			try {
				return ProtoCommon.activeTest(storageClient.trackerServer
						.getSocket());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
	}
}
