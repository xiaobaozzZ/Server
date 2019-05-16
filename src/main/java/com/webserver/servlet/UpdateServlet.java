package com.webserver.servlet;

import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

public class UpdateServlet extends HttpServlet{

	public void service(HttpRequest request, HttpResponse response) {
		try(RandomAccessFile raf = new RandomAccessFile("user.dat","rw"))
		{	
			String method = request.getMethod();
			String username = request.getParameter("user");
			String password = request.getParameter("password");
			String newpassword = request.getParameter("newpassword");
			byte[] data = new byte[32];
			for(int i=0;i<raf.length()/100;i++) {
				raf.seek(i*100);
				raf.read(data);
				String name = new String(data,"UTF-8").trim();
				if(username.equals(name)) {
					raf.read(data);
					String pw = new String(data,"UTF-8").trim();
					if(password.equals(pw)) {
						raf.seek(i*100+32);
						data = newpassword.getBytes("UTF-8");
						data = Arrays.copyOf(data, 32);
						raf.write(data);
						forward("/myweb/update_success.html", request, response);
						return;
					}
					break;
				}
			}
				forward("/myweb/update_fail.html", request, response);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
