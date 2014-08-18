package com.fast.server;

import java.io.File;
import java.io.IOException;

/**
 * 图片上传文件接口
 * 通常初始化方法： 通过构造ImageServerImpl
 * 注：本实例是线程安全，建议通过spring单例配置
 * @author caozf
 *
 */
public interface ImageServer {
	/**
	 * 上传文件接口。把给定的文件上传到指定的服务器
	 * @param file 文件
	 *    文件扩展名通过file.getName()获得
	 * @return 文件存储id  格式：group1/M00/07/FB/wKgEdVE0AMC-L27NAAAmlrYh34k971.jpg
	 * @throws IOException
	 * @throws Exception
	 */
	public String uploadFile(File file) throws IOException, Exception ;
	/**
	 * 上传文件接口。把给定的文件按照指定的扩展名上传到指定的服务器
	 * @param file  文件
	 * @param suffix  文件扩展名，如 (jpg,txt)
	 * @return  文件存储路径
	 * @throws IOException
	 * @throws Exception
	 */
	public String uploadFile(File file, String suffix) throws IOException, Exception ;
	/**
	 * 上传文件接口。通过二进制流上传
	 * @param fileBuff 二进制数组
	 * @param suffix  文件扩展名 ，如(jpg,txt)
	 * @return
	 */ 
	public String uploadFile(byte[] fileBuff,String suffix) throws IOException, Exception;
	
	/**
	 * 得到域名或IP
	 * @return
	 */
	public String getConnnectString() ;
	/**
	 * 得到连接端口
	 * @return
	 */
	public int getPort() ;
	
	/**
	 *删除文件
	 * @param fileId   文件id,包含group及文件目录和名称信息
	 * @return true 删除成功，false删除失败
	 * @throws IOException
	 * @throws Exception
	 */
	public boolean deleteFile(String fileId) throws IOException,Exception;
	/**
	 * 查找文件内容
	 * @param fileId
	 * @return  文件内容的二进制流
	 * @throws IOException
	 * @throws Exception
	 */
	public byte[] getFileByID(String fileId)throws IOException,Exception;
}
