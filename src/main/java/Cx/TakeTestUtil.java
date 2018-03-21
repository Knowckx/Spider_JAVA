package Cx;

import java.io.File;

import okhttp3.*;

class TakeTestUtil implements Runnable {
    OkHttpClient client = new OkHttpClient();

    String UserID, PWD;
    int Cnt, MapSite;
    String ThreadID;

    // public CrawlerUtil(String UserID1, String PWD1, int mapCnt, int mapSize) {
    public TakeTestUtil() {
        // System.out.println(client.connectTimeoutMillis());
        // client.connectTimeoutMillis(5000);
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

        //     int score = GetUserScore(yhb_id);
        //     if (score < 50) {
        //         System.err.printf("error!!! UserID,PWD = %s,%s,Score is %s\n", UserID, PWD, score);
        //     }
        //     // if (score > 50 || score == -1) {
        //     //     log("user score is " + score);
        //     //     log("continue...");
        //     //     return;
        //     // }
        //     // WatchVideos();

        //     // score = GetUserScore(yhb_id);
        //     // log("after work,score is " + score);
        //     // if (score < 51) {
        //     //     System.err.printf("error!!! UserID,PWD = %s,%s \n", UserID, PWD);
        //     // }

    }

    // 

    //     try {
    //         Doc = resp.parse();
    //     } catch (Exception e) {
    //     }

    //     Elements text = Doc.select("#yhb_id");
    //     // log(text.attr("value"));
    //     return text.attr("value");

    // int GetUserScore(String yhb_id) {
    //     // log("Load video " + zyb_id);
    //     String VideoOKUrl = "http://ln.zjlll.cn/zsjypt/space_personalSpace.action?yhlybmc=XSJBXXB&yhlybid=" + yhb_id;
    //     Connection conn = GetConnection(VideoOKUrl);
    //     conn.method(Method.GET);
    //     Response resp = ExcuteConn(conn);
    //     DownHTML(resp);
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
        System.out.println(YZMPic);
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
        while (true) {
            try {
                resp = client.newCall(requestBuilder.build()).execute();
                break;
            } catch (Exception e) {
                if (tryCnt > 5) {
                    System.out.println("Error!retry time is " + tryCnt);
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
        File file =  fu.DownLoad(PicPath, resp.body().byteStream());
        String dataPath = "src/main/res/download/tessdata";
        String Res =  fu.tess4JOCR(file, dataPath);
        return Res;
    }
    File DownHTML(Response resp) {
        FileUtil fu = new FileUtil();
        String PicPath = "src/main/res/download/yzm.jpg";
        return fu.DownLoad(PicPath, resp.body().byteStream());
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
