package Cx;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.*;

class TakeTestUtil implements Runnable {
    OkHttpClient client;
    private HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

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
        client = new OkHttpClient.Builder().followRedirects(true).cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> newCookie) {
                // System.out.println("目前 cookieStore:"+cookieStore.toString());              
                // System.out.println("收到 Cookies: " + newCookie.toString());
                String host  = httpUrl.host();
                List<Cookie> hostCookie = cookieStore.get(host);
                System.out.println("host 对应的 Cookies: " + hostCookie.toString());
                hostCookie.addAll(newCookie);
                // System.out.println("加完后，host 对应的 Cookies: " + hostCookie.toString());
                
                if (!hostCookie.isEmpty()){
                    cookieStore.put(host, hostCookie);
                }
                // cookieStore.put(host, newCookie);
                
                System.out.println("结果的 Cookies: " + cookieStore.toString());
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                List<Cookie> cookies = null;
                List<Cookie> cookiesInStore = cookieStore.get(httpUrl.host());
                cookies = cookiesInStore != null ? cookiesInStore : new ArrayList<Cookie>();
                //   System.out.println("loadForRequest");
                // System.out.println("url host: " + httpUrl.host()); 
                // System.out.println("Req cookies: " + cookies.toString()); 
                return cookies;
            }
        }).build();
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
    //     try {
    //         Doc = resp.parse();
    //     } catch (Exception e) {
    //     }

    //     Elements text = Doc.select("#yhb_id");
    //     // log(text.attr("value"));
    //     return text.attr("value");

    //     Document Doc = null;
    //     try {
    //         Doc = resp.parse();
    //     } catch (Exception e) {
    //     }
    //     Elements text = Doc.select(
    //             "body > div:nth-child(6) > div > div:nth-child(1) > div > div > dl > dd:nth-child(3) > span:nth-child(1) > a");
    //     int score = -1;
    //     try {
    //         score = Integer.parseInt(text.text());
    //     } catch (NumberFormatException e) {
    //         e.printStackTrace();
    //         DownHTML(resp);
    //     }
    //     return score;
    // }

    String UserLogin() {

        String YZMUrl = "http://cx.zjlll.cn/zsjypt/yzm.jsp";
        Request.Builder builder = getReqBuilder(YZMUrl);
        Response resp = ExcuteConn(builder);
        String YZMPic = DownYZM(resp);
        // System.out.println(YZMPic);

        // System.out.println(cookieStore.toString()); //2

        String LoginURL = "http://cx.zjlll.cn/zsjypt/Login.action?sign=jgwz&sqwzb_id=121";
        builder = getReqBuilder(LoginURL);
        //post参数
        RequestBody formBody = new FormBody.Builder().add("yhm", UserID).add("mm", PWD).add("yzm", YZMPic).build();
        builder.post(formBody);
        resp = ExcuteConn(builder);

        // System.out.println(cookieStore.toString()); //3

        System.out.println(resp.headers().toString());
        // DownHTML(resp);
        return "";
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
        if (!resp.isSuccessful()) {
            System.err.println("conn failed,StatusCode:%s" + resp.code());
        }
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
            System.out.println(resp.body().string());

        } catch (Exception e) {
            //TODO: handle exception
        }

        String pagePath = "src/main/res/download/Resp.HTML";

        return fu.DownLoad(pagePath, resp.body().byteStream());
    }

    //req
    // requestBuilder.post(formBody)
    // Response response = client.newCall(requestBuilder.build()).execute();
    // response.body().string();

    //post参数
    //     FormBody formBody = new FormBody.Builder()
    //                     .add("name", "dsd")
    //                     .build();

    //     conn.timeout(100000);
    //     return conn;
    // }

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
