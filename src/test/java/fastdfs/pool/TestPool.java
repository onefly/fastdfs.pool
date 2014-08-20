package fastdfs.pool;

import java.io.IOException;

import com.fast.server.ImageServer;
import com.fast.server.ImageServerImpl;

public class TestPool {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		ImageServer server = null;
		{
			try {
				server = new ImageServerImpl("192.168.6.110", 22122, 300,
						3);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//Thread.sleep(10000);
		for (int i = 0; i < 10; i++) {
			UploadThread run = new UploadThread(server);
			Thread t = new Thread(run);
			t.start();
		}

	}

}
