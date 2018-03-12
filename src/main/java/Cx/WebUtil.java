package Cx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebUtil {
    String UserID = "57205000537025";
    String PWD = "035127";

    //主流程
    void mainLC() {

        // test();

        ConnGet();
        System.out.println("WebUtil end");
    }

    // void HttpGet(String siteURL) {
    //     try {

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    void ConnGet() {
        // String CXSite = "http://www.baidu.com";
        String CXSite = "http://cx.zjlll.cn/zsjypt/";

        Connection conn = GetConnection(CXSite);
        Document doc = null;
        try {
            doc = conn.get();
            log(doc.title());
            Response resp = conn.response();
            Map<String, String> cookies = resp.cookies();
            System.out.println("cookie值:" + cookies);
            //获取返回头文件值
            // String headerValue = resp.header(header);
            // System.out.println("头文件"+header+"的值："+headerValue);
            //获取所有头文件值
            Map<String, String> headersOne = resp.headers();
            System.out.println("所有头文件值：" + headersOne);
            // cookies.get(key)
            log("cookies", cookies);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void GetYZM() {
        String url = "http://cx.zjlll.cn/zsjypt/yzm.jsp";
        String ID = "1520824710447";
        Connection conn = GetConnectionYZM(ID);

    }

    //返回设置好Header的Connection
    Connection GetConnection(String siteURL) {
        String USER_AGENT = "User-Agent";
        String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36";

        Connection conn = Jsoup.connect(siteURL);
        conn.header(USER_AGENT, USER_AGENT_VALUE);
        // con.header("Cookie", cook);
        conn.timeout(100000);
        return conn;

    }

    //返回设置好Header的Connection
    Connection GetConnectionYZM(String siteURL,String QueryString) {
        Connection conn = Jsoup.connect(siteURL);
        conn.header("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
        conn.header("Referer", "http://cx.zjlll.cn/zsjypt/");
        conn.data(QueryString, ""); 
        return conn;

    }

    //用来打印
    void log(String format, Object... args) {
        System.out.println(format);
    }

    //get 一个地址
    void test() {
        ExcelUtil Excel = new ExcelUtil();
        HashMap<String, String> UserInfo = Excel.GetAllData();
    }
}
