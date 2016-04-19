package com.personal.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FileController {
    @RequestMapping("files.do")
    public void execute(@RequestBody String inData, HttpServletResponse resp) {
        JSONObject inJson = new JSONObject(inData);
        JSONArray array = new JSONArray();
        File file = new File(inJson.optString("name"));
        int num = inJson.optInt("num");
        System.out.println(inJson);
        InputStream in = null;
        JSONObject result = new JSONObject();
        try {
            in = new FileInputStream(file);
            System.out.println("以字节为单位读取文件内容，一次读多个字节：");
            // 一次读多个字节
            byte[] tempbytes = new byte[2048];
            int byteread = 0;
            // 读入多个字节到字节数组中，byteread为一次读入的字节数
            int i = 0;
            if (num != 0) {
                in.skip(20000 * 2048 * num);
            }
            String flag = "1";
            while ((byteread = in.read(tempbytes)) != -1 && i < 20000) {
                array.put(encode(tempbytes));
                i++;
            }
            if (i == 20000) flag = "0";
            result.put("flag", flag);
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out2 = null;
        try {
            out2 = resp.getWriter();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        result.put("list", array);
        out2.write(result.toString());
    }

    public static String encode(byte[] bstr) {
        return new sun.misc.BASE64Encoder().encode(bstr);
    }

    /**
     * 解码
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
}
