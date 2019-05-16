package com.webserver.servlet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Random;

import javax.imageio.ImageIO;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 随机验证码
 * @author xiaobao
 *
 */
public class RandomImageServlet extends HttpServlet{
	private static char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefgjhijklmnopqrstuvwxyz0123456789".toCharArray();
	private static int IMAGE_WIDTH = 65;
	private static int IMAGE_HEIGHT = 25;
	private StringBuilder builder;
	public void service(HttpRequest request, HttpResponse response) {
		System.out.println("RandomImageServlet:生成随机验证码图片...");
		//创建画布
		BufferedImage image = new BufferedImage(IMAGE_WIDTH,IMAGE_HEIGHT ,BufferedImage.TYPE_INT_RGB);
		//获取这张图的画笔,用于往这张图上画内容
		Graphics g = image.getGraphics();
		g.setColor(Color.CYAN);
		//使用画笔填充一个矩形,颜色为当前画笔指定颜色
		g.fillRect(0, 0, IMAGE_WIDTH,IMAGE_HEIGHT);
		g.setFont(new Font("Default",Font.BOLD,16));
		Random rand = new Random();
		for(int i=0;i<4;i++) {
			g.setColor(new Color(rand.nextInt(100),rand.nextInt(150),rand.nextInt(100)));
			int index = rand.nextInt(chars.length);
			String str = chars[index]+"";
			g.drawString(str, i*10+5, 18);
		}
		for(int i=0;i<6;i++) {
			g.setColor(new Color(rand.nextInt(100),rand.nextInt(150),rand.nextInt(100)));
			g.drawLine(rand.nextInt(IMAGE_WIDTH), rand.nextInt(IMAGE_HEIGHT), rand.nextInt(IMAGE_WIDTH), rand.nextInt(IMAGE_HEIGHT));
		}
		try {
			/*
			 * ByteArrayOutputStream是一个低级流,通过这个流写出的字节会保存到它内部的一个字节数组中
			 */
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", out);
			//获取输出流里已经写出来的字节(图片的所有字节)
			byte[] data = out.toByteArray();
			response.setContentData(data);
			response.putHeader("Content-Type", "image/jpeg");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("图片生成完毕,生成的验证码为:"+builder);
	}
}
