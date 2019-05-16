package com.webserver.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import com.webserver.servlet.HttpServlet;

/**
 * 用于处理客户端请求
 * @author xiaobao
 *
 */
public class ClientHandler implements Runnable{
	private Socket socket;
	private FileInputStream fis;
	private OutputStream out;
	
	public ClientHandler(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		try {
			System.out.println("ClientHandler:开始处理请求");
			/*
			 * 1 准备工作
			 * 1.1实例化请求对象,解析请求
			 * 1.2实例化响应对象
			 */
			HttpRequest request = new HttpRequest(socket);
			HttpResponse response = new HttpResponse(socket);
			/* 
			 * 2 处理请求
			 * 2.1:通过request获取requestURI,用来得知用户请求的资源的路径
			 * 2.2:从webapps目录下根据该资源路径找到对应资源
			 * 2.3:判断该资源是否真实存在
			 * 2.4:存在则响应该资源
			 * 2.5:不存在则响应404NOTFOUND页面
			 */
			String path = request.getRequestURI();
			//判断该请求是否为请求业务处理
			HttpServlet servlet = ServerContext.getServlet(path);
			if(servlet != null) {
				servlet.service(request, response);
			}else {
				File file = new File("webapps"+path);
				if(file.exists()) {
					response.setEntity(file);
					//添加响应
				}else {
					file = new File("webapps/root/404.html");
					response.setEntity(file);
					response.setStatusCode(404);
					response.setStatusReason("NOT FOUND");
				}
			}
			
			
			
			
			/*
			 * 3 发送响应
			 */
			response.flush();
			System.out.println("ClientHandler:请求处理完毕");
		}catch(EmptyRequestException e) {
			System.out.println("叮叮叮");
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			//处理完毕后与客户端断开连接
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
