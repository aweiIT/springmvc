package com.personal.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * 实现实时聊天推送 注意：当出现异常时 （1）客户端断网，服务端获取不到状态时，仍以为客户是在线状态，此时发消息给该客户会报异常
 * 方案：每次给客户端发送消息时 ，发送一个唯一ID，当客户端接收到此消息时，往服务端返回此ID，服务端判断，如果３－
 * ５秒之内没有收到此ID的回复状态，则判断该用户掉线，之前发送的消息保留 若收到此ID的回复，则确认用户收到消息，删除之前的发送消息 （２）客户端断网时
 * 
 * 功能说明：websocket处理类, 使用J2EE7的标准 切忌直接在该连接处理类中加入业务处理代码 04:20)
 */
// relationId和userCode是我的业务标识参数,websocket.ws是连接的路径，可以自行定义
@ServerEndpoint("/files.ws/{id}")
public class FileSocket {
	/**
	 * 打开连接时触发
	 * 
	 * @param relationId
	 * @param userCode
	 * @param session
	 */
	@OnOpen
	public void onOpen(@PathParam("id") String id, Session session) {
		log("open", id);
	}

	void log(String state, String ID) {
		System.out.println(ID + "          " + state);
	}

	/**
	 * 收到客户端消息时触发
	 * 
	 * @param relationId
	 * @param userCode
	 * @param message
	 * @return
	 */

	private File file;
	private OutputStream out = null;
	private String name = "";
	private List<String> list = new ArrayList<String>();

	@OnMessage
	public void onMessage(String message, Session session, @PathParam("id") String id) {
		// 接受到消息时调用方法，session就是用户的session，message就是接受到的用户的信息

		JSONObject object = new JSONObject(message);
		int num = object.optInt("num");
		if (name.equals("")) {
			name = "C:/" + System.currentTimeMillis() + ".rar";
			file = new File(name);
			try {
				out = new FileOutputStream(file, true);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (num == -1) {
			try {
				out = new FileOutputStream(file, true);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			list.add(object.optString("str"));
			for (int i = 0; i < list.size(); i++) {
				try {
					out.write(decode(list.get(i)));
					out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				out.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("传输完成！");
			try {
				session.getBasicRemote().sendText("finish");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			name = "";
			list = new ArrayList<String>();
			return;
		}
		if (num == 0) {

			try {
				out = new FileOutputStream(file, true);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (int i = 0; i < list.size(); i++) {
				try {
					out.write(decode(list.get(i)));
					out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			list = new ArrayList<String>();
		}
		list.add(object.optString("str"));
	}

	/**
	 * 异常时触发
	 * 
	 * @param relationId
	 * @param userCode
	 * @param session
	 */
	@OnError
	public void onError(@PathParam("id") String id, Throwable throwable, Session session) {
		log("onError", id);
	}

	public static String encode(byte[] bstr) {
		return new sun.misc.BASE64Encoder().encode(bstr);
	}

	/**
	 * 解码
	 * 
	 * @param str
	 * @return string
	 */
	public static byte[] decode(String str) {
		byte[] bt = null;
		try {
			sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
			bt = decoder.decodeBuffer(str);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bt;
	}

	/**
	 * 关闭连接时触发
	 * 
	 * @param relationId
	 * @param userCode
	 * @param session
	 */
	@OnClose
	public void onClose(@PathParam("id") String id, Session session) {
		log("onClose", id);
	}

}