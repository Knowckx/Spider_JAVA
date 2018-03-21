package Cx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;


public class cookieUtil implements  CookieJar {
    private HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    
    @Override
    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> newCookie) {
        // System.out.println("目前 cookieStore:"+cookieStore.toString());              
        // System.out.println("收到 Cookies: " + newCookie.toString());
        // List<Cookie> hostCookie = cookieStore.get(host);
        // System.out.println("host 对应的 Cookies: " + hostCookie.toString());
        // hostCookie.addAll(newCookie);
        // System.out.println("加完后，host 对应的 Cookies: " + hostCookie.toString());
        // if (!hostCookie.isEmpty()){
        //     cookieStore.put(host, hostCookie);
        // }
        System.out.println("---------我们收到Response，url是" +httpUrl);
        String host  = httpUrl.host();
        
        System.out.println("host是: " + httpUrl.host()); 
        System.out.println("此时本地的cookies: " + cookieStore.toString()); 
        System.out.println("Response里的cookies: " + newCookie.toString());
        List<Cookie> newCookie111 = deepCopy(newCookie);
        newCookie.addAll(cookieStore.get(host)); 
        cookieStore.put(host, newCookie);
        System.out.println("整理后的本地cookies: " + cookieStore.toString()); 
        
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        List<Cookie> cookiesInStore = cookieStore.get(httpUrl.host());
        List<Cookie>  cookies = cookiesInStore != null ? cookiesInStore : new ArrayList<Cookie>();
          System.out.println("我们要访问："+httpUrl);
        System.out.println("host是: " + httpUrl.host()); 
        System.out.println("此时本地的cookies: " + cookieStore.toString()); 
        System.out.println("发出的Req cookies: " + cookies.toString()); 
        return cookies;
    }

    public static <T> List<T> deepCopy(List<T> src)  {  
        null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();  
            ObjectOutputStream out = new ObjectOutputStream(byteOut);  
            out.writeObject(src);  
          
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());  
            ObjectInputStream in = new ObjectInputStream(byteIn); 
        @SuppressWarnings("unchecked")
        List<T> dest = dest = (List<T>) in.readObject();  
        } catch (Exception e) {
            //TODO: handle exception
        }
        return dest;  

    }  
}

