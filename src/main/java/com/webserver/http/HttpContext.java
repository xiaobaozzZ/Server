package com.webserver.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
/**
 * HTTP协议规定的相关内容
 * @author xiaobao
 *
 */
public class HttpContext {
	/**
	 * 回车符 CR
	 * ASC编码对应值:13
	 */
	public static final int CR = 13;
	/**
	 * 换行符LF
	 * ASC编码对应值:10
	 */
	public static final int LF = 10;
	/*
	 * 资源后缀名与Content-Type响应头对应值的映射关系
	 * key:资源后缀名
	 * value:Content-Type对应的值
	 */
	private static Map<String,String> mimeMapping = new HashMap<>();
	static {
		initMimeMapping();
	}
	/**
	 * 初始化mimeMapping哈希表
	 */
	private static void initMimeMapping() {
		/*
		 * 使用DOM4J解析conf/web.xml文件
		 * 将根标签下所有名为<mime-mapping>的子标签获取回来,并将其中子标签
		 * <extension>中的文本作为key
		 * <mime-type>中的文本作为value来初始化mimeMapping这个Map
		 * 
		 * 初始化完毕后,这个Map应当有1010个元素
		 */
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read("conf/web.xml");
			Element root = doc.getRootElement();
			List<Element> mimeList = root.elements("mime-mapping");
			for(Element element : mimeList) {
				mimeMapping.put(element.elementText("extension"), element.elementText("mime-type"));
			}
			System.out.println(mimeMapping.size());
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据给定的资源后缀名获取对应的Content-Type值
	 * @param key 后缀名
	 * @return 后缀名对应的值
	 */
	public static String getValue(String key) {
		return mimeMapping.get(key);
	}
}
