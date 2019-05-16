package com.webserver.servlet;

import java.io.File;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 所有Servlet的超类
 * @author xiaobao
 *
 */
public abstract class HttpServlet {
	public abstract void service(HttpRequest request,HttpResponse response);
	/**
	 * 跳转指定路径
	 * (TomCat的该方法实际上是通过HttpRequest获取的转发器dispatcher对应的方法)
	 * @param paht
	 * @param request
	 * @param response
	 */
	public void forward(String path,HttpRequest request,HttpResponse response) {
		File file = new File("webapps"+path);
		response.setEntity(file);
	}
}
