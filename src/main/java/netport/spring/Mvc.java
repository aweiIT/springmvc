package netport.spring;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Mvc {
    @RequestMapping("hello.do")
    public void hello(@RequestBody String name, HttpServletResponse response) {
        System.out.println("hello.do------------");
        JSONArray list = new JSONArray(name);
        System.out.println(list.length());
        System.out.println(list.optJSONObject(0));
        write(response, list.optJSONObject(1).toString());
    }

    void write(HttpServletResponse response, String str) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=utf-8");
            out = response.getWriter();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        out.write(str);
    }
}
