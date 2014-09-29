package com.fast.client;

import java.io.File;
import java.io.IOException;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.ProtoCommon;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import com.fast.pool.StorageClient;
/**
 * 继承抽象工厂类，创建fastdfs客户端对象
 * @Project 	: fastdfs.pool
 * @Program Name: com.fast.client.FastdfsClientFactory.java
 * @ClassName	: FastdfsClientFactory 
 * @Author 		: caozhifei 
 * @CreateDate  : 2014年9月29日 下午2:33:19
 */
public class FastdfsClientFactory extends BasePoolableObjectFactory {
	/**
	 * fastDfs配置文件路径
	 */
	private  String confPath = "fdfs_client.conf";
	/**
	 * 创建fastDfs客户端对象
	 */
	public Object makeObject() throws Exception {
		String classPath = new File(getClass().getResource("/").getFile())
				.getCanonicalPath();
		String configFilePath = classPath + File.separator
				+ confPath;
		ClientGlobal.init(configFilePath);
		TrackerClient tracker = new TrackerClient();
		TrackerServer trackerServer = tracker.getConnection();
		StorageServer storageServer = null;
		StorageClient client = new StorageClient(trackerServer,
				storageServer);
		return client;
	}
	/**
	 * 销毁fastDfs客户端对象
	 */
	public void destroyObject(Object obj) throws Exception {
		if ((obj != null) && ((obj instanceof StorageClient))) {
			StorageClient storageClient = (StorageClient) obj;
			TrackerServer trackerServer = storageClient.getTrackerServer();
			StorageServer storageServer = storageClient.getStorageServer();
			trackerServer.close();
			storageServer.close();
		}
	}
	/**
	 * 检查fastDfs客户端对象连接是否正常
	 */
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