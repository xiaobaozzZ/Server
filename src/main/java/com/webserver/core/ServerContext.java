package com.webserver.core;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.webserver.servlet.HttpServlet;

/**
 * 服务端相关配置信息定义
 * @author xiaobao
 *
 */
public class ServerContext {
	/**
	 * 请求与对应Servlet的关系
	 * key:请求路径
	 * value:处理该请求的Servlet实例
	 */
	private static Map<String,HttpServlet> servletMapping = new HashMap<>();
	
	static {
		initSevrlet();
	}
	/**
	 * 初始化servlet
	 */
	private static void initSevrlet() {
//		servletMapping.put("/myweb/reg", new RegServlet());
//		servletMapping.put("/myweb/login", new LoginSevrlet());
//		servletMapping.put("/myweb/update", new UpdateServlet());
		/*
		 * 解析conf/servlets.xml文件
		 * 将根标签下所有的<servlet>标签解析出来并用其属性path的值作为key,
		 * className属性的值使用反射方式加载对应的Servlet类并进行实例化,然后将对应的实例作为value
		 * 保存到servletMapping这个Map中
		 */
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new File("conf/servlets.xml"));
			Element root = doc.getRootElement();
			List<Element> elements = root.elements("servlet");
			for(Element e : elements) {
				String path = e.attributeValue("path");
				String servlet = e.attributeValue("className");
				Class cls = Class.forName(servlet);
				HttpServlet obj = (HttpServlet)cls.newInstance();
				servletMapping.put(path, obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据请求路径获取对应的Servlet实例
	 * @param path
	 * @return
	 */
	public static HttpServlet getServlet(String path) {
		return servletMapping.get(path);
	}
}
