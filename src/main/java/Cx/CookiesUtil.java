// package Cx;

// import java.util.HashMap;

// public class CookiesUtil {




//1.cookies是多个站点的。
//     private static HashMap<String, HashMap<String, String>> hostCookies = new HashMap<String, HashMap<String, String>>();


//连接一个站点时，得到一个host,host就是一个key,这个站点的cookies
//此时，连接前要把这个cookies取出来。给conn
//     private static void loadCookiesByHost(URL url, Connection con) {
//         try {
//             String host = url.getHost();
//             if (hostCookies.containsKey(host)) {
//                 HashMap<String, String> cookies = hostCookies.get(host);
//                 for (Entry<String, String> cookie : cookies.entrySet()) {
//                     con.cookie(cookie.getKey(), cookie.getValue());
//                 }
//             }
//         } catch (Throwable t) {
//             // MTMT move to log
//             System.err.println(t.toString()+":: Error loading cookies to: " + url);
//         }
//     }
    

//     public static String[] DownloadPage(URL url) throws Exception
//     {
//         Connection con = Jsoup.connect(url.toString()).timeout(600000);
//         loadCookiesByHost(url, con);  第一个函数
    
    
//         Document doc = con.get();
//         url = con.request().url();
    
//         storeCookiesByHost(url, con);
    
//         return new String[]{url.toString(), doc.html()};
//     }
    



//     private static void storeCookiesByHost(URL url, Connection con) {
//             try {
//                 String host = url.getHost();
//                 HashMap<String, String> cookies = hostCookies.get(host);
//                 if (cookies == null) {
//                     cookies = new HashMap<String, String>();
//                     hostCookies.put(host, cookies);
//                 }
//                 cookies.putAll(con.response().cookies());
//             } catch (Throwable t) {
//                 // MTMT move to log
//                 System.err.println(t.toString()+":: Error saving cookies from: " + url);
//             }    
//     }   

// }
