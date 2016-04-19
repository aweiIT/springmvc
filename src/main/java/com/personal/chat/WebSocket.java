package com.personal.chat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
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
 * 实现实时聊天推送 注意：当出现异常时 （1）客户端断网，服务端获取不到状态时，仍以为客户是在线状态，此时发消息给该客户会报异常
 * 方案：每次给客户端发送消息时，发送一个唯一ID，当客户端接收到此消息时，往服务端返回此ID，服务端判断，如果３－５秒之内没有收到此ID的回复状态，
 * 则判断该用户掉线，之前发送的消息保留 若收到此ID的回复，则确认用户收到消息，删除之前的发送消息 （２）客户端断网时 功能说明：websocket处理类,
 * 使用J2EE7的标准 切忌直接在该连接处理类中加入业务处理代码 04:20)
 */
// relationId和userCode是我的业务标识参数,websocket.ws是连接的路径，可以自行定义
@ServerEndpoint("/websocket.ws/{relationId}/{userCode}")
public class WebSocket {
	private static HashMap<String, Object> map = new HashMap<String, Object>();
	private Session session;
	private static JSONObject msgList = new JSONObject();

	/**
	 * 打开连接时触发
	 * 
	 * @param relationId
	 * @param userCode
	 * @param session
	 */
	@OnOpen
	public void onOpen(@PathParam("relationId") String relationId, @PathParam("userCode") int userCode,
			Session session) {
		if (map.get(relationId) == null) {
			this.session = session;
			map.put(relationId, this);
		}
		log("onOpen", relationId);
		if (msgList.optJSONObject(relationId) != null) {
			try {
				session.getBasicRemote().sendText(msgList.get(relationId).toString());
				msgList.remove(relationId);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	@OnMessage
	public void onMessage(String message, Session session, @PathParam("relationId") String relationId,
			@PathParam("userCode") int userCode) {
		// 接受到消息时调用方法，session就是用户的session，message就是接受到的用户的信息
		JSONObject in = new JSONObject(message);
		WebSocket obj = (WebSocket) map.get(in.optString("sendId"));
		JSONObject result = new JSONObject();
		result.put("imgID", in.optString("imgID"));
		result.put("relationId", in.optString("sendId"));
		result.put("msg", in.optString("msg"));
		if (obj == null) {
			JSONObject msgObj = msgList.optJSONObject(in.optString("sendId"));
			boolean hasObj = false;
			if (msgObj == null)
				msgObj = new JSONObject();
			else {
				hasObj = true;
			}
			JSONArray array = msgObj.optJSONArray(relationId);
			if (array == null) {
				array = new JSONArray();
				array.put(result);
				msgObj.put(relationId, array);
			} else {
				msgList.optJSONObject(in.optString("sendId")).optJSONArray(relationId).put(result);
			}
			if (!hasObj) {
				msgList.put(in.optString("sendId"), msgObj);
			}
		} else {
			JSONObject msg = new JSONObject();
			JSONArray array = new JSONArray();
			array.put(result);
			msg.put(relationId, array);
			try {
				obj.session.getBasicRemote().sendText(msg.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("对方连接中断");
			}
		}

	}

	/**
	 * 异常时触发
	 * 
	 * @param relationId
	 * @param userCode
	 * @param session
	 */
	@OnError
	public void onError(@PathParam("relationId") String relationId, @PathParam("userCode") int userCode,
			Throwable throwable, Session session) {
		log("onError", relationId);
		System.out.println(throwable.getLocalizedMessage());
		System.out.println(throwable.getMessage());
	}

	/**
	 * 关闭连接时触发
	 * 
	 * @param relationId
	 * @param userCode
	 * @param session
	 */
	@OnClose
	public void onClose(@PathParam("relationId") String relationId, @PathParam("userCode") int userCode,
			Session session) {
		map.remove(relationId);
		log("onClose", relationId);
	}

	static void sendMsgToAll(JSONObject in) {
		Iterator iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String sendId = (String) iterator.next();
			if (in.optString("ID").equals(sendId))
				continue;
			WebSocket obj = (WebSocket) map.get(sendId);
			in.put("type", "add");
			try {
				obj.session.getBasicRemote().sendText(in.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("对方连接中断");
			}
		}
	}
}