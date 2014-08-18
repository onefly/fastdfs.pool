package com.fast.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerGroup;
import org.csource.fastdfs.TrackerServer;

/**
 * 具体连接池，对使用人员透明。
 * @author zhanghua
 *
 */
public class ConnectionPool {

	// busy connection instances
	private ConcurrentHashMap<TrackerServer, Object> busyConnectionPool = null;
	// idle connection instances
	private ArrayBlockingQueue<TrackerServer> idleConnectionPool = null;
	// delay lock for initialization

	// the connection string for ip
	private String tgStr = null;
	// the server port
	private int port = 22122;
	// the limit of connection instance
	private int size = 2;
	
	private Object obj = new Object();
	
	//heart beat
	HeartBeat beat=null;

	public ConnectionPool(String tgStr, int port, int size) throws IOException {
		this(tgStr, port, size, 60*60*1);
	}
	
	public ConnectionPool(String tgStr, int port, int size,int heartBeatTimes) throws IOException {
		this.tgStr = tgStr;
		this.port = port;
		this.size = size;
		initClientGlobal();
		init();
		//注册心跳
		beat=new HeartBeat(this);
		beat.setHeartbeatTime(heartBeatTimes);
		beat.beat();
	}

	/**
	 * init the connection pool
	 * 
	 * @param size
	 * @throws IOException 
	 */
	private void init() throws IOException {
		busyConnectionPool = new ConcurrentHashMap<TrackerServer, Object>();
		idleConnectionPool = new ArrayBlockingQueue<TrackerServer>(this.size);
		TrackerServer trackerServer = null;
		try {
			TrackerClient trackerClient = new TrackerClient();
			for (int i = 0; i < size; i++) {
				trackerServer = trackerClient.getConnection();
				org.csource.fastdfs.ProtoCommon.activeTest(trackerServer.getSocket());
				idleConnectionPool.add(trackerServer);
			}
		} catch (IOException e) {
			ImageServerPoolSysout.warn("connection pool constructor throw ioexception");
			throw e;
		} 
	}

	// 1. pop one connection from the idleConnectionPool,
	// 2. push the connection into busyConnectionPool;
	// 3. return the connection
	// 4. if no idle connection, do wait for wait_time seconds, and check again
	/**
	 * 如果在等待时间已经过时，则抛出null异常。
	 */
	public TrackerServer checkout(int waitTimes) throws InterruptedException,NullPointerException {
		TrackerServer client1 = idleConnectionPool.poll(waitTimes,
				TimeUnit.SECONDS);
		if (client1 == null) {
			ImageServerPoolSysout
					.warn("connection pool  wait tracker time out ,return null");
			throw new NullPointerException(
					"connection pool wait time        out ,return null");
		}
		busyConnectionPool.put(client1, obj);
		return client1;
	}

	// 1. pop the connection from busyConnectionPool;
	// 2. push the connection into idleConnectionPool;
	// 3. do nessary cleanup works.
	public void checkin(TrackerServer client1) {
		if (busyConnectionPool.remove(client1) != null) {
			idleConnectionPool.add(client1);
		}
	}

	// so if the connection was broken due to some erros (like
	// : socket init failure, network broken etc), drop this connection
	// from the busyConnectionPool, and init one new connection.
	public synchronized void drop(TrackerServer trackerServer) {
		// first less connection
		//删除一个无效的连接，如果得到新连建也是无效，则启动detector线程，用于检测什么时候可以正常连接起来
		//一旦检查成功，将相应属性修改hasConnectionException修改为false，释放先前的连接，并重新建立连接池。
		try {
			trackerServer.close();
		} catch (IOException e1) {
		}
		if (busyConnectionPool.remove(trackerServer) != null) {
			try {
				ImageServerPoolSysout
						.warn("connection pool drop a tracker connnection,remainder size:"+(busyConnectionPool.size() + idleConnectionPool
								.size()));
				TrackerClient trackerClient = new TrackerClient();
				trackerServer = trackerClient.getConnection();
				org.csource.fastdfs.ProtoCommon.activeTest(trackerServer.getSocket());
			} catch (Exception e) {
				trackerServer = null;
				ImageServerPoolSysout
						.warn("when connection pool create new tracker connection throw "+e);
			} finally {
				if (!isContinued(trackerServer)) {
					return;
				}
				// 变成传过数据的
				idleConnectionPool.add(trackerServer);
				ImageServerPoolSysout.warn("ImageServerPool add a connnection,has size:"+ (busyConnectionPool.size() + idleConnectionPool
						.size()));
			}
		}
	}
	
	
	public boolean isContinued(TrackerServer trackerServer){
		if (trackerServer == null && hasConnectionException) {
			return false;
		}
		if (trackerServer == null) {
			hasConnectionException = true;
			// only a thread;
			detector();
		}
		if (hasConnectionException) {
			//代表detector正在运行，就算获得连接，也要等detector做完
			return false;
		}
		return true;
	}

	private void detector() {

		new Thread() {
			@Override
			public void run() {
				String msg="connection pool detector new trakcer connection fail to "+tgStr;
				TrackerClient trackerClient = new TrackerClient();
				while (true) {
					TrackerServer trackerServer = null;
					try {
						Thread.sleep(5000);
						trackerServer = trackerClient.getConnection();
						org.csource.fastdfs.ProtoCommon.activeTest(trackerServer.getSocket());
					} catch (Exception e) {
						ImageServerPoolSysout
						.warn("when connection pool detector new tracker connection throw "+e);
						trackerServer=null;
					}					finally{
						if(trackerServer!=null){
							msg="connection pool detector new tracker connection success to "+tgStr;
							try {
								trackerServer.close();
								trackerServer=null;
							} catch (IOException e) {
								trackerServer=null;
								ImageServerPoolSysout
								.warn("when connection pool detector temp new tracker connection to close trackerServer throw IOException");
							}
							break;
						}
						ImageServerPoolSysout.warn("connection pool detector find to current  remainder pool size:"
								+ (busyConnectionPool.size() + idleConnectionPool
										.size()));
						ImageServerPoolSysout.warn(msg);
					}
				}
				ImageServerPoolSysout.warn(msg);
				
				if(idleConnectionPool.size()!=0){
					ImageServerPoolSysout.warn("connection pool idleConnectionPool remander tracker connection, start  close former tracker connection");
					for(int i=0;i<size;i++){
						TrackerServer ts=idleConnectionPool.poll();
						if(ts!=null){
							try {
								ts.close();
							} catch (IOException e) {
								ts=null;
							}
						}
					}
				}
				//re init
				hasConnectionException=false;
				try {
					init();
				} catch (IOException e) {
					ImageServerPoolSysout
					.warn("when connection pool detector init() IOException,i am so sorry.");	
				}
			}
		}.start();
	}

	boolean hasConnectionException = false;

	private void initClientGlobal() {
		InetSocketAddress[] trackerServers = new InetSocketAddress[1];
		trackerServers[0] = new InetSocketAddress(tgStr, port);
		ClientGlobal.setG_tracker_group(new TrackerGroup(trackerServers));
		// 连接超时的时限，单位为毫秒
		ClientGlobal.setG_connect_timeout(20000);
		// 网络超时的时限，单位为毫秒
		ClientGlobal.setG_network_timeout(30000);
		ClientGlobal.setG_anti_steal_token(false);
		// 字符集
		ClientGlobal.setG_charset("ISO-8859-1");
		ClientGlobal.setG_secret_key(null);
	}

	public ArrayBlockingQueue<TrackerServer> getIdleConnectionPool() {
		return idleConnectionPool;
	}
	
	public ConcurrentHashMap<TrackerServer, Object> getBusyConnectionPool(){
		return busyConnectionPool;
	}
	
	
}
