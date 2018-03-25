package Cx;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import Cx.cookie.CookieJarImpl;
import Cx.cookie.store.MemoryCookieStore;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.FormBody.Builder;

class TakeTestUtil implements Runnable {
    OkHttpClient client;

    String UserID, PWD;
    int Cnt, MapSize;
    String ThreadID;
    ConcurrentHashMap<String, String> UserInfoFail;

    String okClick; //为了局部方便

    public TakeTestUtil(String UserID1, String PWD1, int Cnt, int MapSize,
            ConcurrentHashMap<String, String> UserInfoFail) {
        log("Progress: %s/%s \n", Cnt, MapSize);
        this.UserID = UserID1;
        this.PWD = PWD1;
        this.Cnt = Cnt;
        this.MapSize = MapSize;
        this.UserInfoFail = UserInfoFail;
    }

    public void run() {
        init();
        System.out.printf("UserID,PWD = %s,%s \n", UserID, PWD);
        try {
            MainDo();
            
            // MainTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.printf("-------------Progress: %s/%s \n", Cnt, MapSize);
    }

    void init() {
        MemoryCookieStore mcs = new MemoryCookieStore();
        CookieJar cj = new CookieJarImpl(mcs);
        client = new OkHttpClient.Builder().cookieJar(cj).followRedirects(true).build();
    }

    void MainTest() {
        //System.out.println("MainTest--------");
        try {
            String PicPath = "src/main/res/download/623F.jpg";
            File file = new File(PicPath); //路径+文件名
            FileUtil fu = new FileUtil();
            String aa = fu.tess4JOCR(file);

        } catch (Exception e) {
            //TODO: handle exception
        }
        System.exit(1);
    }

    void MainDo() {
        // UserID = "57205000537021";  //有分了。
        // PWD = "303724";

        if(Cnt>1850){
            System.err.println("--------------"+UserInfoFail.toString());
        }

        String yhb_id = UserLogin();
        signUptheTest();
        int score = getScore();
        if (score > -1) {
            return;
        }
        UserInfoFail.put(UserID, ""+score);

        FuckTheTest();
        score = getScore();
        if (score > -1) {
            log("User:%s Score is %s,Success..", UserID, score);
            return;
        } else {
            log("User:%s Score is %s,Fail..", UserID, score);
        }
    }

    String UserLogin() {
        String yhb_id = "";
        Response resp = null;
        while (yhb_id.equals("")) {
            resp = DoUserLogin();
            yhb_id = resp.request().url().queryParameter("yhb_id");
            resp.close();
        }
        //System.out.println("Login Success!");
        return yhb_id;
    }

    Response DoUserLogin() {
        String YZMUrl = "http://cx.zjlll.cn/zsjypt/yzm.jsp";
        Request.Builder builder = getReqBuilder(YZMUrl);
        Response resp = ExcuteConn(builder);
        String YZMPic = DownYZM(resp).substring(0, 4);
        //System.out.println("YZM " + YZMPic);
        String LoginURL = "http://cx.zjlll.cn/zsjypt/Login.action?sign=jgwz&sqwzb_id=121";
        builder = getReqBuilder(LoginURL);
        RequestBody formBody = new FormBody.Builder().add("yzm", YZMPic).add("yhm", UserID).add("mm", PWD).build();
        builder.post(formBody);
        resp = ExcuteConn(builder);
        return resp;
    }

    void signUptheTest() {
        String testID = "1453";
        String testUrl = "http://cx.zjlll.cn/zsjypt/indexSqcj.action";
        Request.Builder builder = getReqBuilder(testUrl);
        RequestBody formBody = new FormBody.Builder().add("xmId", testID).build();
        builder.post(formBody);
        ExcuteConn(builder).close();;
    }

    int getScore() {
        Document Doc = getScorePageDoc();
        String okClickSelect = "#ktMain > table > tbody > tr > td:nth-child(7) > a";
        okClick = Doc.select(okClickSelect).attr("onclick");
        Elements eleScore = Doc.select("#ktMain > table > tbody > tr > td:nth-child(6)");
        int score = -1;
        try {
            String scoreText = eleScore.text();
            if (!scoreText.isEmpty()) {
                score = Integer.parseInt(scoreText.substring(0, 2));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return score;
    }

    Document getScorePageDoc() {
        String getScoreUrl = "http://cx.zjlll.cn/zsjypt/kcFrame.action?kcdm=1001402&yhb_id=&kclmb_id=8&kclmszb_id=10617";
        Request.Builder builder = getReqBuilder(getScoreUrl);
        Response resp = ExcuteConn(builder);
        Document Doc = JsoupParse(resp);
        return Doc;
    }

    void FuckTheTest() {
        Document Doc = getTestDoc(); //获得考试页的doc
        postAnswersandSubmit(Doc);
    }

    void postAnswersandSubmit(Document Doc) {
        String yhksb_id = Doc.select("#yhksb_id").attr("value"); //yhksb_id
        String yhid = Doc.select("#yhb_id").attr("value"); //yhb_id
        String dtsysj = Doc.select("#dtsysj").attr("value"); //yhb_id

        // log("%s,%s", yhid,yhksb_id);    //438390,24121
        String quesSText = Doc.select("#tm").attr("value");
        String[] quesIDs = quesSText.split(","); //拿到所有试题
        for (String tkb_id : quesIDs) {
            tkb_id = tkb_id.trim();
            postOneAnswer(tkb_id, yhksb_id, yhid);
        }
        submitTest(yhid, dtsysj, yhksb_id, quesSText);
    }

    private void submitTest(String yhid, String dtsysj, String yhksb_id, String quesSText) {
        String submitURL = "http://cx.zjlll.cn/zsjypt/kcksSubmit.action";
        Request.Builder builder = getReqBuilder(submitURL);
        Builder formBody = new FormBody.Builder();
        formBody.add("yhb_id", yhid);
        formBody.add("dtsysj", dtsysj);
        formBody.add("yhksb_id", yhksb_id);
        formBody.add("tm", quesSText);

        formBody.add("kcdmb_id", "2406");
        formBody.add("kcdm", "1001402");
        formBody.add("sign", "kcksDj");
        formBody.add("kclmb_id", "8");
        formBody.add("kcksb_id", "1641");
        builder.post(formBody.build());
        ExcuteConn(builder).close();
    }

    private void postOneAnswer(String tkb_id, String yhksb_id, String yhid) {
        String testURL = "http://cx.zjlll.cn/zsjypt/kcksSaveYhda.action";
        Request.Builder builder = getReqBuilder(testURL);

        Builder formBody = new FormBody.Builder();
        formBody.add("tkb_id", tkb_id);
        formBody.add("yhda", "D");
        formBody.add("yhksb_id", yhksb_id);
        formBody.add("yhid", yhid);

        builder.post(formBody.build());
        ExcuteConn(builder).close();
    }

    Document getTestDoc() {
        String testURL = getBeginTestURL();
        Request.Builder builder = getReqBuilder(testURL);
        RequestBody formBody = formBeginTestBody();
        builder.post(formBody);
        Response resp = ExcuteConn(builder); //进入考试页
        return JsoupParse(resp);
    }

    String getBeginTestURL() {
        String testURL = "http://cx.zjlll.cn/zsjypt/kcksDj.action?kcksb_id=1641"; //like doKj(1641)
        if (okClick.startsWith("jxdoKs")) { //like"jxdoKs(1641,24121);"
            int sign1 = okClick.indexOf(",");
            int sign2 = okClick.indexOf(")");
            String yhksb_id = okClick.substring(sign1 + 1, sign2);
            // log("yhksb_id:%s", yhksb_id);
            testURL = "http://cx.zjlll.cn/zsjypt/kcksJxdj.action?kcksb_id=1641&yhksb_id=" + yhksb_id;
        }
        return testURL;
    }

    RequestBody formBeginTestBody() {
        Builder formBody = new FormBody.Builder();
        formBody.add("kcdm", "1001402");
        formBody.add("kcdmb_id", "2406");
        formBody.add("kclmszb_id", "10617");
        formBody.add("kclmb_id", "8");
        formBody.add("fid", "0");
        formBody.add("pageModel.first_page", "0");
        formBody.add("pageModel.last_page", "1");
        formBody.add("pageModel.initial_flag", "1");
        formBody.add("pageModel.pageNum_dq", "1");
        formBody.add("pageModel.count_eve", "10");
        return formBody.build();
    }

    // //----------------------------------------------------------------通用组件
    // //返回设置好基础Header的Connection
    // OkHttp
    Request.Builder getReqBuilder(String siteURL) {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(siteURL);

        String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36";
        requestBuilder.addHeader("User-Agent", USER_AGENT_VALUE);
        requestBuilder.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
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
                //System.out.println(err);
                if (tryCnt > 5) {
                    e.printStackTrace();
                    RetryTimeOut();
                }
                tryCnt++;
                UtilSleep(3 * tryCnt);
            }
        }
        if (!resp.isSuccessful()) {
            resp.close();
            System.err.println("conn failed,StatusCode:%s" + resp.code());
        }
        return resp;
    }

    String DownYZM(Response resp) {
        FileUtil fu = new FileUtil();
        String PicPath = "src/main/res/download/yzm.jpg";
        File file = fu.DownLoadFile(PicPath, resp.body().byteStream());
        resp.close();
        String Res = fu.tess4JOCR(file);
        return Res;
    }

    File DownHTML(Response resp) {
        FileUtil fu = new FileUtil();
        try {
            // //System.out.println(resp.body().string());
        } catch (Exception e) {
            //TODO: handle exception
        }

        String pagePath = "src/main/res/download/Resp.html";
        // return null;
        return fu.DownLoadHtml(pagePath, resp.body().byteStream());
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

    Document JsoupParse(Response resp) {
        Document doc = null;
        String data = "";
        try {
            data = resp.body().string();
            doc = Jsoup.parse(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            resp.close();
        }

        if (doc == null) {
            log("JsoupParse Fail.resp.body:\n%s", data);
        }
        return doc;
    }

    void log(String format, Object... args) {
        //System.out.println(String.format(format, args));
    }
    //----------------------------------------------------------------Common //
    //----------------------------------------------------------------制定    
}
