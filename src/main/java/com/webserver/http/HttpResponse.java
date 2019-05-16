package com.webserver.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 响应对象
 * 该类的每个实例用于表示发送给客户端的响应内容
 * 
 * 一个响应包含三部分:状态行,响应头,响应正文
 * @author xiaobao
 *
 */
public class HttpResponse {
	/*
	 * 与连接相关的信息
	 */
	private Socket socket;
	private OutputStream out;
	/*
	 * 状态行相关信息
	 */
	//状态代码,默认值200  默认值为200主要原因两个: 1 如果不指定默认值,int默认值为0,若我们没有设置状态代码的话,HTTP协议
	//是没有状态代码为0这个情况的,所以不能用0作默认值 2 通常一个请求都能正确处理,回复客户端200是比较多的情况,因此默认值用200
	//可以在大部分响应时不用指定状态代码和对应描述了
	private int statusCode = 200;
	//状态描述,默认值OK
	private String statusReason = "OK";
	/*
	 * 响应头相关信息
	 */
	private Map<String,String> headers = new HashMap<>();
	/*
	 * 响应正文相关信息
	 */
	//响应正文的实体文件
	private File entity;
	//字节数据作为正文内容(作为响应动态数据使用)
	private byte[] data;
	/**
	 * 创建HttpResponse的同时需要指定Socket当前响应对象就是通过这个Socket获取输出流给对应客户端发送响应内容的
	 * @param socket
	 */
	public HttpResponse(Socket socket) {
		try {
			this.socket = socket;
			this.out = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 将当前响应对象内容以一个标准HTTP响应格式发送给客户端
	 */
	public void flush() {
		try {
			sendStatusLine();
			sendHeaders();
			sendContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 发送状态行
	 * @throws IOException
	 */
	private void sendStatusLine() throws IOException{
		//1 发送状态行
		String line = "HTTP/1.1 "+statusCode+" "+statusReason;
		sendCRLF(line);
	}
	/**
	 * 发送响应头
	 * @throws IOException
	 */
	private void sendHeaders() throws IOException{
		//2 发送响应头
		Set<Entry<String,String>> entrySet = headers.entrySet();
		for(Entry<String,String> header : entrySet) {
			String name = header.getKey();
			String value = header.getValue();
			String line = name+": "+value;
			sendCRLF(line);
		}
		
		//单独发送CRLF表示响应头发送
		sendCRLF("");
	}
	/**
	 * 发送响应正文
	 * @throws IOException
	 */
	private void sendContent() throws IOException{
		/*
		 * 如果实体文件存在,则作为正文发送
		 * 注:响应与请求一样,可以不含有正文
		 */
		if(entity!=null) {
			try(FileInputStream fis = new FileInputStream(entity);){
				int len = -1;
				byte[] data = new byte[1024*10];
				while((len = fis.read(data)) != -1) {
					out.write(data, 0, len);
				}
			}catch(IOException e) {
				throw e;
			}
		}
		/*
		 * 如果正文字节数组存在,则将这组字节作为响应正文发送
		 */
		if(data!=null) {
			out.write(data);
		}
	}
	private void sendCRLF(String line) throws IOException{
		out.write(line.getBytes("ISO8859-1"));
		out.write(HttpContext.CR);
		out.write(HttpContext.LF);
	}
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusReason() {
		return statusReason;
	}

	public void setStatusReason(String statusReason) {
		this.statusReason = statusReason;
	}

	public File getEntity() {
		return entity;
	}
	/**
	 * 设置响应正文对应的实体文件
	 * 设置正文的同时会自动向响应对象中添加说明
	 * 响应正文的响应头:Content-Type和Content-Lenght
	 * @param entity
	 */
	public void setEntity(File entity) {
		this.entity = entity;
		this.data = null;
		String fileName = entity.getName();
		System.out.println("资源名:"+fileName);
		int index = fileName.lastIndexOf(".");
		String value = fileName.substring(index+1);
		putHeader("Content-Type", HttpContext.getValue(value));
		putHeader("Content-Length", entity.length()+"");
	}
	public void putHeader(String name,String value) {
		this.headers.put(name, value);
	}
	public byte[] getContentData() {
		return data;
	}
	/**
	 * 设置响应正文数据
	 * @param data 该组字节会作为响应正文内容发送给客户端
	 */
	public void setContentData(byte[] data) {
		this.data = data;
		this.entity = null;
		//自动添加Content-Length头
		putHeader("Content-Length",data.length+"");
	}
}
