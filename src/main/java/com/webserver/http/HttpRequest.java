package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.webserver.core.EmptyRequestException;

/**
 * 请求对象
 * 该类的每一个实例用于表示浏览器发送过来的一个请求内容,每个请求由三部分构成(请求行,消息头,消息正文)
 * @author xiaobao
 *
 */
public class HttpRequest {
	/*
	 * 请求行相关信息
	 */
	//请求方式
	private String method;
	//请求资源的抽象路径
	private String url;
	//请求使用的协议版本
	private String protocol;
	//url中"?"左边的部分
	private String requestURI;
	//url中"?"右边的部分
	private String queryString;
	private Map<String,String> parameters = new HashMap<>();
	/*
	 * 消息头相关信息
	 * key存消息头的名字
	 * value存消息头对应的值
	 */
	//每一个参数key:参数名 value:参数值 
	private Map<String,String> headers = new HashMap<>();
	/*
	 * 消息正文相关信息
	 */
	private byte[] data;
	/*
	 * 与连接相关的属性
	 */
	private Socket socket;
	private InputStream in;
	/**
	 * 初始化HttpRequest对象
	 * 初始化的过程就是解析请求的过程,实例化完毕后当前HttpRequest对象就表示浏览器发送过来的这个请求内容了
	 */
	public HttpRequest(Socket socket) throws EmptyRequestException{
		System.out.println("HttpRequest:开始解析请求");
		try {
			this.socket = socket;
			this.in = socket.getInputStream();
			parseRequestLine();
			parseHeaders();
			parseContent();
		}catch(EmptyRequestException e) {
			throw e;
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("HttpRequest:解析请求完毕");
	}
	/**
	 * 解析请求行
	 * @throws EmptyRequestException 
	 */
	private void parseRequestLine() throws EmptyRequestException {
		System.out.println("开始解析请求行");
		/*
		 * 1:通过输入流读取第一行字符串(请求行内容)
		 * 2:将请求行内容按照空格拆分为三部分
		 * 3:将拆分后的内容设置到对应的属性上(method,url,protocol)
		 */
		try {
			/*
			 * 后期循环接受客户端连接后,下面代码可能会出现下标越界,这是由于空请求引起的,后面会解决
			 */
			String line = readLine();
			/*
			 * 判断是否为空请求
			 */
			if("".equals(line)) {
				throw new EmptyRequestException();
			}
			String[] linesp = line.split(" ");
			this.method = linesp[0];
			this.url = linesp[1];
			this.protocol = linesp[2];
			System.out.println("method:"+method);
			System.out.println("url:"+url);
			System.out.println("protocol:"+protocol);
			//进一步解析抽象路径部分
			parseURL();
		}catch(EmptyRequestException e) {
			throw e;
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("解析请求行完毕");
	}
	private void parseURL() {
		System.out.println("开始解析抽象路径");
		/*
		 * 一个请求的请求行当中抽象路径部分有两种情况:
		 * 1:不含有参数的,如:
		 *  /myweb/index.html
		 * 2:含有参数的,如:
		 *	/myweb/reg?user.....
		 * 因此我们对抽象路径进一步解析:
		 * 1:首先判断url的值是否含有 ?
		 * 1.1:若不含有"?"则直接将url的值赋值给属性requestURI即可
		 * 1.2:若含有"?"执行步骤2
		 * 
		 * 2:将url按照"?"拆分为两部分,第一部分应当是请求部分,赋值给属性requestURI
		 *	 第二部分应当是参数部分,赋值给属性queryString
		 * 3:进一步解析queryString,将其按照"&"拆分为若干个参数,每一个参数再按照"="拆分为参数名与参数值.
		 * 并将参数名作为key,参数值作为value保存到属性parameters这个Map中完成解析工作
		 */
		if(url.indexOf("?")==-1) {
			requestURI = url;
		}else {
				String[] sp = url.split("\\?");
				requestURI = sp[0];
				if(sp.length>1) {
					queryString = sp[1];
					putParameters(queryString);
				}
		}
		System.out.println("requestURI:"+requestURI);
		System.out.println("queryString:"+queryString);
		System.out.println("parameters:"+parameters);
		
		System.out.println("解析抽象路径结束");
	}
	/**
	 * 解析消息头
	 */
	private void parseHeaders() {
		System.out.println("开始解析消息头");
		/*
		 * 1:循环调用readLine方法读取每一个消息头
		 * 2:将消息头按照": "拆分,并将消息头的名字作为key,消息头的值作为value保存到属性
		 * headers这个Map中
		 * 3:如果调用readLine方法返回的是一个空字符串,则说明本次单独读取到了CRLF,那么久可以停止解析消息头了
		 */
		try {
			String line = null;
			while( !"".equals((line = readLine()))) {
				String[] linesp = line.split(": ");
				headers.put(linesp[0], linesp[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("消息头:"+headers);
		System.out.println("解析消息头完毕");
	}
	/**
	 * 解析消息正文
	 */
	private void parseContent() {
		System.out.println("开始解析消息正文");
		if("POST".equals(method)) {
			try {
				if(headers.containsKey("Content-Length")) {
					String length = headers.get("Content-Length");
					data = new byte[Integer.parseInt(length)];
				}
				String type = headers.get("Content-Type");
				in.read(data);
				if("application/x-www-form-urlencoded".equals(type)) {
					String line = new String(data,"ISO8859-1");
					putParameters(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("解析消息正文完毕");
	}
	private void putParameters(String line) {
		String[] linesp = line.split("&");
		for(String s : linesp) {
			String[] linesp1 = s.split("=");
			if(linesp1.length>1) {
				parameters.put(linesp1[0], linesp1[1]);
			}else {
				parameters.put(linesp1[0], null);
			}
		}
	}
	
	/**
	 * 通过对应客户端的输入流读取一行字符串
	 * @return
	 */
	private String readLine() throws IOException{
		StringBuilder buileder = new StringBuilder();
		int b1 = -1,b2 = -1;
		while((b2 = in.read()) != -1) {
			//13 CR  10 LF
			if(b1 == HttpContext.CR && b2 == HttpContext.LF) {
				break;
			}
			buileder.append((char)b2);
			b1 = b2;
		}
		String line = buileder.toString().trim();
		return line;
	}
	
	public String getMethod() {
		return method;
	}
	public String getUrl() {
		return url;
	}
	public String getProtocol() {
		return protocol;
	}
	/**
	 * 获取指定名字消息头对应的值
	 * @param name 传需要获取的消息头的名字
	 * @return 返回消息头对应的值
	 */
	public String getHeader(String name) {
		return headers.get(name);
	}
	public String getRequestURI() {
		return requestURI;
	}
	public String getQueryString() {
		return queryString;
	}
	/**
	 * 根据给定的参数名获取对应的参数值
	 * @param name
	 * @return
	 */
	public String getParameter(String name) {
		return parameters.get(name);
	}
}
