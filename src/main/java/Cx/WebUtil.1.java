// package Cx;

// import java.io.File;
// import java.io.FileOutputStream;
// import java.io.PrintStream;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import org.jsoup.Connection;
// import org.jsoup.Jsoup;
// import org.jsoup.Connection.Method;
// import org.jsoup.Connection.Response;
// import org.jsoup.nodes.Document;
// import org.jsoup.nodes.Element;
// import org.jsoup.select.Elements;

// public class WebUtil {

//     Map<String, HashMap<String, String>> hostCookies = new HashMap<String, HashMap<String, String>>();
//     String UserID, PWD;

//     //主流程0
//     void mainLC() {
//         System.out.println("WebUtil Start");
//         try {
//             ExcelUtil ExcelFile = new ExcelUtil();
//             HashMap<String, String> UserInfo = ExcelFile.GetAllData();
//             // UserInfo.clear();
//             // UserInfo.put("57205000509167", "01131X");
//             int mspSize = UserInfo.size();
//             int Cnt = 0;
//             ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
//             for (String key : UserInfo.keySet()) {
//                 String UserID = key;
//                 String PWD = UserInfo.get(key);
//                 System.out.printf("Progress: %s/%s \n", Cnt++, mspSize);
//                 System.out.printf("UserID,PWD = %s,%s \n", UserID, PWD);
//                 fixedThreadPool.execute(new Runnable() {
//                     public void run() {
//                         try {
//                             System.out.println("第" +1 + "个线程" +Thread.currentThread().getName());
//                             MainDo(UserID,PWD);
//                             Thread.sleep(500);
//                         } catch (InterruptedException e) {
//                             e.printStackTrace();
//                         }
//                     }
//                 });
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//         System.out.println("WebUtil end");
//     }

//     void MainDo(String UserID ,String PWD) {
//         hostCookies.clear();
//         String yhb_id = UserLogin(UserID,PWD);

//         int score = GetUserScore(yhb_id);
//         if (score > 49 || score == -1) {
//             log("user score is " + score);
//             log("continue...");
//             return;
//         }
//         WatchVideos();

//         score = GetUserScore(yhb_id);
//         log("after work,score is " + score);
//         if (score < 50) {
//             log("system  out");
//             System.exit(0);
//         }

//     }

//     String UserLogin(String UserID ,String PWD) {

//         String LoginPageUrl = "http://ln.zjlll.cn/zsjypt/oou_redirectLogin.action?login=login";
//         Connection conn = GetConnection(LoginPageUrl);
//         conn.method(Method.GET);
//         ExcuteConn(conn);

//         String LoginPostUrl = "http://ln.zjlll.cn/zsjypt/oou_login.action?sign2=1";
//         conn = GetConnection(LoginPostUrl);
//         conn.method(Method.POST);
//         HashMap<String, String> QueryString = new HashMap<String, String>();
//         QueryString.put("yhm", UserID);
//         QueryString.put("mm", PWD);
//         conn.data(QueryString);
//         conn.followRedirects(false);
//         ExcuteConn(conn);

//         String LoginRedirectUrl = "http://ln.zjlll.cn/zsjypt/oou_updateinfo.action";
//         conn = GetConnection(LoginRedirectUrl);
//         conn.method(Method.GET);
//         Response resp = ExcuteConn(conn);
//         DownHTML(resp);

//         int yhb_id;
//         Document Doc = null;
//         try {
//             Doc = resp.parse();
//         } catch (Exception e) {
//         }

//         Elements text = Doc.select("#yhb_id");
//         // log(text.attr("value"));
//         return text.attr("value");
//     }

//     int GetUserScore(String yhb_id) {
//         // log("Load video " + zyb_id);
//         String VideoOKUrl = "http://ln.zjlll.cn/zsjypt/space_personalSpace.action?yhlybmc=XSJBXXB&yhlybid=" + yhb_id;
//         Connection conn = GetConnection(VideoOKUrl);
//         conn.method(Method.GET);
//         Response resp = ExcuteConn(conn);
//         DownHTML(resp);
//         Document Doc = null;
//         try {
//             Doc = resp.parse();
//         } catch (Exception e) {
//         }
//         Elements text = Doc.select(
//                 "body > div:nth-child(6) > div > div:nth-child(1) > div > div > dl > dd:nth-child(3) > span:nth-child(1) > a");
//         int score = -1;
//         try {
//             score = Integer.parseInt(text.text());
//         } catch (NumberFormatException e) {
//             e.printStackTrace();
//             DownHTML(resp);
//         }
//         return score;
//     }

//     void WatchVideos() {
//         int[] vidosIDs = { 70339, 71785, 18515, 18956, 87901, 82139 };
//         for (int vidosID : vidosIDs) {
//             String sVidosID = Integer.toString(vidosID);
//             Get2WatchVideo(sVidosID);
//         }
//     }

//     void Get2WatchVideo(String zyb_id) {
//         String VideoOKUrl = "http://ln.zjlll.cn/zsjypt/zl_tips.action?xf=1&bj=&zyb_id=" + zyb_id;
//         Connection conn = GetConnection(VideoOKUrl);
//         conn.method(Method.GET);
//         ExcuteConn(conn);
//     }

//     // log(conn.request().headers().toString());

//     // Response resp = ExcuteConn(conn);
//     // // System.out.println("resp:" + resp.body());

//     // DownHTML(resp);
//     //----------------------------------------------------------------通用组件
//     //返回设置好基础Header的Connection
//     Connection GetConnection(String siteURL) {
//         Connection conn = Jsoup.connect(siteURL);
//         String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36";
//         conn.header("User-Agent", USER_AGENT_VALUE);

//         conn.header("Accept", "*/*");
//         conn.header("Accept-Encoding", "gzip, deflate");
//         conn.header("Accept-Language", "zh-CN,zh;q=0.9");
//         conn.header("Cache-Control", "no-cache");
//         conn.header("Connection", "keep-alive");
//         conn.header("Content-Length", "337");
//         conn.header("Host", "ln.zjlll.cn");

//         conn.timeout(100000);
//         return conn;
//     }

//     Response ExcuteConn(Connection Conn) {
//         String host = Conn.request().header("Host"); //连接前处理cookies
//         if (hostCookies.containsKey(host)) {
//             HashMap<String, String> cookies = hostCookies.get(host);
//             Conn.cookies(cookies);
//         }
//         // Map<String, String> headers1 = Conn.request().headers(); //连接前所使用的headers
//         // log(headers1.toString());
//         // Map<String, String> reqCookies = Conn.request().cookies(); //连接前所使用的Cookies
//         // log("目前连接使用的Cookies " + reqCookies.toString());

//         Response resp = null;
//         int tryCnt = 0; //重试
//         while (true) {
//             try {
//                 resp = Conn.execute();
//                 break;
//             } catch (Exception e) {
//                 e.printStackTrace();
//                 if (tryCnt >= 3) {
//                     log("Error!retry time is " + tryCnt);
//                     System.exit(0);
//                 }
//                 tryCnt++;
//                 UtilSleep(2 * tryCnt);
//             }
//         }
//         // log("response:\n" + resp.headers().toString());

//         if (resp.statusCode() != 200 && resp.statusCode() != 302) {
//             log("conn failed,StatusCode:%s" + resp.statusCode());
//             log("exit!");
//             System.exit(0);
//         }
//         // System.out.println("返回的cookies值:" + resp.cookies());

//         HashMap<String, String> cookies = hostCookies.get(host);
//         if (cookies == null) {
//             cookies = new HashMap<String, String>();
//             hostCookies.put(host, cookies);
//         }
//         cookies.putAll(resp.cookies());
//         return resp;
//     }

//     void DownHTML(Response resp) {
//         String HtmlContent = resp.body();

//         String PicPath = "src/main/res/download/";
//         File file = null;
//         try {
//             //创建文件目录  
//             File files = new File(PicPath);
//             if (!files.exists()) {
//                 files.mkdirs();
//             }
//             file = new File(PicPath + "Resp.HTML"); //路径+文件名
//             PrintStream ps = new PrintStream(new FileOutputStream(file));
//             ps.println(HtmlContent);// 往文件里写入字符串
//             ps.close();
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     void UtilSleep(int timeSleep) {
//         try {
//             Thread.sleep(timeSleep * 1000);
//         } catch (InterruptedException ex) {
//             Thread.currentThread().interrupt();
//         }
//     }

//     //用来打印
//     void log(String args) {
//         System.out.println(args);
//     }

//     //----------------------------------------------------------------Common //
//     //----------------------------------------------------------------制定    
// }
