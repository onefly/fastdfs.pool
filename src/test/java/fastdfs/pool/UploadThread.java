package fastdfs.pool;

import java.io.File;
import java.io.IOException;

import com.fast.server.ImageServer;

public class UploadThread implements Runnable{
	ImageServer server;
	
	public UploadThread(ImageServer server) {
		super();
		this.server = server;
	}

	@Override
	public void run() {
		File file = new File("E:/2048.jpg");
		try {
			long start = System.currentTimeMillis();
			String path = server.uploadFile(file, "jpg");
			System.out.println("ThreadId:"+Thread.currentThread().getId()+"  "+path);
			long end = System.currentTimeMillis();
			System.out.println("ThreadId:"+Thread.currentThread().getId()+"cost time: "+(end-start));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
