package com.webserver.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 处理注册业务
 * @author xiaobao
 *
 */
public class RegServlet extends HttpServlet{
	public void service(HttpRequest request,HttpResponse response) {
		System.out.println("RegServlet:开始处理注册业务...");
		/*
		 * 1:通过request获取用户在页面上输入的注册信息
		 * 
		 * 2:将该用户的注册信息写入到文件user.dat中
		 * 
		 * 3:设置response响应注册结果页面
		 */
		//获取表单信息
		String username = request.getParameter("user");
		String password = request.getParameter("password");
		String superpassword = request.getParameter("superpassword");
		String nickname = request.getParameter("nickname");
		int age = Integer.parseInt(request.getParameter("age"));
		System.out.println("username:"+username);
		System.out.println("password:"+password);
		System.out.println("nickname:"+nickname);
		System.out.println("age:"+age);
		FileOutputStream fos = null ;
		try (RandomAccessFile raf = new RandomAccessFile("user.dat", "rw"))
		{	
			/*
			 * 首先读取user.dat文件中的每个用户信息,比对用户名与当前注册用户名字是否一致,若
			 * 一致则说明该用户名已经被使用,这时这只response响应用户提示页面:该用户已存在,否则执行下面原有的
			 * 注册操作
			 */
			byte[] d = new byte[32];
			Set<String> nameSet = new HashSet<>();
			String name;
			for(int i=0;i<raf.length()/100;i++) {
				raf.seek(i*100);
				raf.read(d);
				name = new String(d).trim();
				nameSet.add(name);
			}
			if(!nameSet.contains(username)) {
				raf.seek(raf.length());
				byte[] data = username.getBytes("UTF-8");
				data = Arrays.copyOf(data, 32);
				raf.write(data);
				
				data = password.getBytes("UTF-8");
				data = Arrays.copyOf(data, 32);
				raf.write(data);
				
				data = nickname.getBytes("UTF-8");
				data = Arrays.copyOf(data, 32);
				raf.write(data);
				raf.writeInt(age);
				forward("/myweb/reg_success.html", request, response);
			}else {
				forward("/myweb/reg_fail.html", request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("RegServlet:处理注册业务完毕...");
	}
}
