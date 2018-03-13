package Cx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebUtil {

    Map<String, String> cookieData = null;

    //主流程0
    void mainLC() {
        System.out.println("WebUtil Start");
        UserLogin();
        // test();
        // ConnGet();
        // GetYZM();
        System.out.println("WebUtil end");
    }

    void ConnGettest() {
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

    //用来打印
    void log(String format, Object... args) {
        System.out.printf(format, args);
    }

    //get 一个地址
    void test() {
        ExcelUtil Excel = new ExcelUtil();
        HashMap<String, String> UserInfo = Excel.GetAllData();
    }

    //----------------------Login - YZM处理
    void UserLogin() {

        GetConnectionLogin();
        // GetPoint();

    }

    //账号2 57205000536921	245727
    // Y9W5.。

    //Form Data 
    //:
    //:
    // :
    // :
    // :Y9W5

    //登陆成功
    //Date:Tue, 13 Mar 2018 07:30:18 GMT
    //新cookies
    //     Set-Cookie:timeout=30; Secure; HttpOnly
    // Set-Cookie:login-yhm-pwd=""; Expires=Thu, 01-Jan-1970 00:00:10 GMT
    Connection GetConnectionLogin() {
        String strYZM = GetYZM();
        String UserID = "57205000537025";
        String PWD = "035127";

        String LoginUrl = "http://cx.zjlll.cn/zsjypt/Login.action?sign=jgwz&sqwzb_id=121";
        Connection conn = GetConnection(LoginUrl);
        conn.method(Method.POST);
        conn.followRedirects(true);
        HashMap<String, String> QueryString = new HashMap<String, String>();
        QueryString.put("SqwzModel.sqwz_id", "121");
        QueryString.put("mkxsmc", "用户登录");
        QueryString.put("yhm", UserID);
        QueryString.put("mm", PWD);
        QueryString.put("yzm", strYZM);

        // QueryString.put("sign", "jgwz");
        // QueryString.put("sqwzb_id", "121");
        conn.data(QueryString);

        Response resp = ExcuteConn(conn);
        cookieData = resp.cookies(); //cookies值
        System.out.println("resp:" + resp.body());

        DownHTML(resp);
        

        return conn;
    }

    String GetYZM() {
        String CaptchaStr = "";
        try {
            String url = "http://cx.zjlll.cn/zsjypt/yzm.jsp";
            Connection conn = GetConnectionYZM(url);
            Response resp = conn.execute();
            File pic = DownLoadPic(resp.bodyStream(), "PicTest");
            CaptchaStr = GetTess4J(pic); //得到验证码

            cookieData = resp.cookies(); //cookies值
            System.out.println("cookie值:" + cookieData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CaptchaStr;
    }

    Connection GetConnectionYZM(String siteURL) {
        Connection conn = Jsoup.connect(siteURL);
        // conn.header("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
        conn.header("Referer", "http://cx.zjlll.cn/zsjypt/");
        conn.header("Host", "cx.zjlll.cn");
        conn.ignoreContentType(true);
        conn.method(Method.GET);
        return conn;
    }

    File DownLoadPic(InputStream is, String fileName) {
        String PicPath = "src/main/res/download/";
        File file = null;
        try {
            //创建文件目录  
            File files = new File(PicPath);
            if (!files.exists()) {
                files.mkdirs();
            }
            file = new File(PicPath + fileName + ".jpg"); //路径+文件名
            //根据输入流写入文件  
            FileOutputStream out = new FileOutputStream(file);
            int i = 0;
            while ((i = is.read()) != -1) { //从input流里读数据，写入到FileOutputStream
                out.write(i);
            }
            out.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    String GetTess4J(File file) {
        Tesseract tessreact = new Tesseract();
        tessreact.setDatapath("src/main/res/download/tessdata");
        String resString = "";
        try {
            resString = tessreact.doOCR(file);
            System.out.println("Tess4J:" + resString);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
        return resString;
    }
    //-----------------YZM

    //-----------------查询积分
    int GetPoint() {
        int point;
        String url = "http://cx.zjlll.cn/zsjypt/index_Updateyh.action";
        Connection Conn = GetConnection(url);

        Conn.header("Host", "cx.zjlll.cn");
        Conn.header("Origin", "http://cx.zjlll.cn");
        Conn.header("Referer", "http://cx.zjlll.cn/zsjypt/index_Updateyh.action");

        HashMap<String, String> QueryString = new HashMap<String, String>();

        QueryString.put("xmkid", "2");

        QueryString.put("yhb_id", "438234");

        QueryString.put("js", "student");
        QueryString.put("xm", "高粉娣");
        QueryString.put("nc", "57205000536921");
        QueryString.put("yhtx", "zcyhwd/yhtx/default/photo(722).jpg");
        QueryString.put("nc", "57205000536921");
        QueryString.put("sfzh", "330522196712245727");
        QueryString.put("tel", "13705828502");
        // QueryString.put("sign", "jgwz");
        // QueryString.put("sqwzb_id", "121");
        Conn.data(QueryString);
        Response resp = ExcuteConn(Conn);
        String Body = resp.body();
        return 0;
    }

    //----------------------------------------------------------------Common
    Response ExcuteConn(Connection Conn) {
        Conn.cookies(cookieData);
        Response resp = null;

        try {
            resp = Conn.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        cookieData = resp.cookies();
        System.out.println("新cookies值:" + cookieData);
        return resp;
    }

    void DownHTML(Response resp){
        String HtmlContent = resp.body();

        String PicPath = "src/main/res/download/";
        File file = null;
        try {
            //创建文件目录  
            File files = new File(PicPath);
            if (!files.exists()) {
                files.mkdirs();
            }
            file = new File(PicPath + "Resp.HTML"); //路径+文件名
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.println(HtmlContent);// 往文件里写入字符串
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //----------------------------------------------------------------Common //

}
