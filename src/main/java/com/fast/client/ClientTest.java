package com.fast.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


import com.fast.pool.FastdfsPoolConfig;
import com.fast.pool.Pool;
import com.fast.pool.StorageClient;

public class ClientTest extends Thread {
	private static Pool pool;
	static{
		FastdfsPoolConfig config = new FastdfsPoolConfig();
		config.setMaxActive(300);
		pool = new FastDfsPool(config,
				new FastdfsClientFactory());
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
		for(int i=0;i<1200;i++){
			ClientTest ct = new ClientTest();
			ct.start();
		}
	}

	@Override
	public void run() {
		try {
			StorageClient client = pool.getResource();
			byte[] bytes = image2Bytes("E:/2048.jpg");
			String[] arr = client.upload_file(bytes, "jpg", null);
			System.out.println(Thread.currentThread().getId()+" return "+Arrays.toString(arr));
			pool.returnResource(client);
		} catch (Exception e) {
			System.out.println("==================================================");
			e.printStackTrace();
		}
	}

	public static byte[] image2Bytes(String imagePath) {
		ImageIcon ima = new ImageIcon(imagePath);
		BufferedImage bu = new BufferedImage(ima.getImage().getWidth(null), ima
				.getImage().getHeight(null), BufferedImage.TYPE_INT_RGB);
		ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
		try {
			// 把这个jpg图像写到这个流中去,这里可以转变图片的编码格式
			boolean resultWrite = ImageIO.write(bu, "png", imageStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] tagInfo = imageStream.toByteArray();
		return tagInfo;
	}
}
