package com.webserver.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 展示user.dat文件中的所有用户信息
 * @author varus
 *
 */
public class ShowAllUserDemo {
	public static void main(String[] args) throws IOException {
//		RandomAccessFile raf = new RandomAccessFile("user.dat","r");
//		for(int i=0;i<raf.length()/100;i++) {
//			//读取用户名
//			byte[] data = new byte[32];
//			raf.read(data);
//			String userName = new String(data,"UTF-8").trim();
//			//读取密码
//			raf.read(data);
//			String passWord = new String(data,"UTF-8").trim();
//			//读取昵称
//			raf.read(data);
//			String nickName = new String(data,"utf-8").trim();
//			//读取年龄
//			int age = raf.readInt();
//			System.out.println("用户名:"+userName+",密码:"+passWord+",昵称:"+nickName+",年龄:"+age);
//		}
		readUser(5);
	}
	public static void readUser(int sum) throws IOException {
		RandomAccessFile raf = new RandomAccessFile("user.dat","r");
		if(!(sum*100>raf.length())) {
			raf.seek((sum-1)*100);
			byte[] data = new byte[32];
			raf.read(data);
			String userName = new String(data,"UTF-8").trim();
			//读取密码
			raf.read(data);
			String passWord = new String(data,"UTF-8").trim();
			//读取昵称
			raf.read(data);
			String nickName = new String(data,"utf-8").trim();
			//读取年龄
			int age = raf.readInt();
			System.out.println("用户名:"+userName+",密码:"+passWord+",昵称:"+nickName+",年龄:"+age);
		}else {
			System.out.println("没有该用户信息");
		}
		raf.close();
	}
}
