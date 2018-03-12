package Cx;

// import java.util.HashMap;

// public class CookiesUtil {
//     private static HashMap<String, HashMap<String, String>> hostCookies = new HashMap<String, HashMap<String, String>>();

//     public static String[] DownloadPage(URL url) throws Exception
//     {
//         Connection con = Jsoup.connect(url.toString()).timeout(600000);
//         loadCookiesByHost(url, con);
    
    
//         Document doc = con.get();
//         url = con.request().url();
    
//         storeCookiesByHost(url, con);
    
//         return new String[]{url.toString(), doc.html()};
//     }
    
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
