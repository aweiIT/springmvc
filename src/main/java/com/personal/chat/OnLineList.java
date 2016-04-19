package com.personal.chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class OnLineList {
    private static JSONObject onLine = new JSONObject();

    @RequestMapping("chat_list.do")
    public void execute(@RequestBody String inData, HttpServletResponse resp) {
        System.out.println("11111111111111111"+inData);
        JSONObject in=new JSONObject(inData);
        System.out.println(in+"-----------");
        if (in.optString("type").equals("add")) {
            // 群发给所有用户新添加的用户信息。
            saveUserInfo(in);
            WebSocket.sendMsgToAll(in);
        }
        // TODO Auto-generated method stub
        Iterator iterator = onLine.keys();
        JSONArray array = new JSONArray();
        JSONObject result = new JSONObject();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (key.equals(in.optString("ID"))) {
                result.put("myDevice", onLine.optJSONObject(key));
            } else array.put(onLine.optJSONObject(key));
        }
        result.put("list", array);
        write(resp, result);
    }

    /**
     * 添加用户信息
     * @param resp
     * @param object
     */
    void saveUserInfo(JSONObject in) {
        onLine.put(in.optString("ID"), in);
    }

    void write(HttpServletResponse resp, Object object) {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out2 = null;
        try {
            out2 = resp.getWriter();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        out2.write(object.toString());
    }
}
