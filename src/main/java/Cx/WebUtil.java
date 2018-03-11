package Cx;

import java.io.IOException;
import java.util.HashMap;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebUtil {
    String UserID = "57205000507875";
    String PWD = "13131X";

    //主流程
    void mainLC() {

        test();

        // ConnGet();
        System.out.println("WebUtil end");
    }

    // void HttpGet(String siteURL) {
    //     try {

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    void ConnGet() {
        String CXSite = "http://cx.zjlll.cn/zsjypt/";
        Connection conn = GetConnection(CXSite);
        Document doc = null;
        try {
            doc = conn.get();
            log(doc.title());
            Response resp = conn.response();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //返回设置好Header的Connection
    Connection GetConnection(String siteURL) {
        String USER_AGENT = "User-Agent";
        String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0";

        Connection conn = Jsoup.connect(siteURL);
        // conn.header(USER_AGENT, USER_AGENT_VALUE);
        // conn.timeout(4000);
        return conn;
    }

    //用来打印
    void log(String format, Object... args) {
        System.out.println(format);
    }

    //get 一个地址
    void test() {
        ExcelUtil Excel =new ExcelUtil();
        HashMap<String, String> UserInfo =Excel.GetAllData();
    }
}
