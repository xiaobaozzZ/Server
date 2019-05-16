package com.webserver.core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WebServer主类
 * @author xiaobao
 *
 */
public class WebServer {
	private ServerSocket server;
	private ExecutorService threadPool;
	/**
	 * 构造方法,用于初始化
	 */
	public WebServer() {
		try {
			System.out.println("正在启动服务器");
			server = new ServerSocket(7777);
			threadPool = Executors.newFixedThreadPool(50);
			System.out.println("服务器启动成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 服务端开始工作的方法
	 */
	public void start() {
		try {
			/*
			 * 暂时只接受一次客户端连接,后期功能实现完毕后再启用
			 */
			while(true) {
				System.out.println("等待客户端连接");
				Socket socket = server.accept();
				ClientHandler c = new ClientHandler(socket);
				threadPool.execute(c);
				System.out.println("一个客户端已经连接");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		WebServer server = new WebServer();
		server.start();
	}
}
