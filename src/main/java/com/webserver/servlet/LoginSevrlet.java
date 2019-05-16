package com.webserver.servlet;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

public class LoginSevrlet extends HttpServlet{
	
	
	
	private Map<String,String> loginMap = new HashMap<>();
	public void service(HttpRequest request,HttpResponse response) {
		try(RandomAccessFile raf = new RandomAccessFile("user.dat","r");) 
		{
			String username = request.getParameter("user");
			String password = request.getParameter("password");
			
			byte[] data = new byte[32];
			for(int i=0;i<raf.length()/100;i++) {
				raf.seek(i*100);
				raf.read(data);
				String name = new String(data,"UTF-8").trim();
				raf.read(data);
				String pw = new String(data,"UTF-8").trim();
				loginMap.put(name, pw);
			}
			if(loginMap.containsKey(username) && (password.equals(loginMap.get(username)))) {
				forward("/myweb/login_success.html", request, response);
			}else {
				forward("/myweb/login_fail.html", request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
