package Cx;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.apache.poi.hssf.record.PageBreakRecord.Break;

import Cx.cookie.CookieJarImpl;
import Cx.cookie.store.MemoryCookieStore;
import okhttp3.*;

class TakeTestUtil implements Runnable {
    OkHttpClient client;

    String UserID, PWD;
    int Cnt, MapSite;
    String ThreadID;

    // public CrawlerUtil(String UserID1, String PWD1, int mapCnt, int mapSize) {
    public TakeTestUtil(String UserID1, String PWD1) {
        this.UserID = UserID1;
        this.PWD = PWD1;

        init();
        // System.out.println(client.connectTimeoutMillis());
        // client.connectTimeoutMillis(5000);
    }

    void init() {
        MemoryCookieStore mcs = new MemoryCookieStore();
        CookieJar cj = new CookieJarImpl(mcs);
        client = new OkHttpClient.Builder().cookieJar(cj).followRedirects(true).build();
    }

    public void run() {
        //     ThreadID = Thread.currentThread().getName();
        //     // System.out.printf("UserID,PWD = %s,%s \n", UserID, PWD);
        try {
            MainDo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //     System.out.printf("-------------Progress: %s/%s \n", Cnt++, MapSite);
    }

    void MainDo() {
        System.out.println("TakeTest");
        //1.获得验证码，识别，登陆。确定你已经登陆成功。

        //     hostCookies.clear();
        String yhb_id = UserLogin();
    }

    String UserLogin() {
        String yhb_id = "";
        while (yhb_id.equals("")) {
            yhb_id = DoUserLogin();
        }
        System.out.println("Success");
        return "";
    }

    String DoUserLogin() {
        String YZMUrl = "http://cx.zjlll.cn/zsjypt/yzm.jsp";
        Request.Builder builder = getReqBuilder(YZMUrl);
        Response resp = ExcuteConn(builder);
        String YZMPic = DownYZM(resp).substring(0, 4);
        System.out.println("YZM " + YZMPic);
        String LoginURL = "http://cx.zjlll.cn/zsjypt/Login.action?sign=jgwz&sqwzb_id=121";
        builder = getReqBuilder(LoginURL);
        RequestBody formBody = new FormBody.Builder().add("yzm", YZMPic).add("yhm", UserID).add("mm", PWD).build();
        builder.post(formBody);
        resp = ExcuteConn(builder);
        // System.out.println(resp.headers().toString());
        String yhb_id = resp.request().url().queryParameter("yhb_id");
        return yhb_id;
    }

    // //----------------------------------------------------------------通用组件
    // //返回设置好基础Header的Connection
    // OkHttp
    Request.Builder getReqBuilder(String siteURL) {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(siteURL);

        String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36";
        requestBuilder.addHeader("User-Agent", USER_AGENT_VALUE);
        requestBuilder.addHeader("Accept-Encoding", "gzip, deflate");
        requestBuilder.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
        requestBuilder.addHeader("Cache-Control", "no-cache");
        requestBuilder.addHeader("Connection", "keep-alive");
        requestBuilder.addHeader("Host", "cx.zjlll.cn");
        return requestBuilder;
        // application/x-www-form-urlencoded
    }

    Response ExcuteConn(Request.Builder requestBuilder) {
        Response resp = null;
        int tryCnt = 0; //重试
        Request Req = requestBuilder.build();
        while (true) {
            try {
                resp = client.newCall(Req).execute();
                break;
            } catch (Exception e) {
                String err = String.format("Err.when req to %s,retry time %d", Req.url(), tryCnt);
                System.out.println(err);
                if (tryCnt > 5) {
                    e.printStackTrace();
                    RetryTimeOut();
                }
                tryCnt++;
                UtilSleep(3 * tryCnt);
            }
        }
        // if (!resp.isSuccessful()) {
        //     System.err.println("conn failed,StatusCode:%s" + resp.code());
        // }
        return resp;
    }

    String DownYZM(Response resp) {
        FileUtil fu = new FileUtil();
        String PicPath = "src/main/res/download/yzm.jpg";
        File file = fu.DownLoad(PicPath, resp.body().byteStream());
        String dataPath = "src/main/res/download/tessdata";
        String Res = fu.tess4JOCR(file, dataPath);
        return Res;
    }

    File DownHTML(Response resp) {
        FileUtil fu = new FileUtil();
        try {
            // System.out.println(resp.body().string());
        } catch (Exception e) {
            //TODO: handle exception
        }

        String pagePath = "src/main/res/download/Resp.HTML";

        return fu.DownLoad(pagePath, resp.body().byteStream());
    }

    void UtilSleep(int timeSleep) {
        try {
            Thread.sleep(timeSleep * 1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    void RetryTimeOut() {
        // System.out.printf("RetryTimeOut!\nUserID,PWD = %s,%s \n", UserID, PWD);
    }

    //----------------------------------------------------------------Common //
    //----------------------------------------------------------------制定    
}
