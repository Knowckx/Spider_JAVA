package Cx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;



public class cookieUtil implements CookieJar {
    private HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    @Override
    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> repsCookies1) {
        System.out.println("---------我们收到Response，url是" + httpUrl);
        String host = httpUrl.host();
        // System.out.println("host是: " + httpUrl.host());
        System.out.println("Response里的cookies: " + repsCookies1.toString());
        List<Cookie> localhostCookie = null;
        //坑：传入的repsCookies1你是不能用的。必须新建一个。
        List<Cookie> repsCookies = new ArrayList<>(repsCookies1);
        try {
            // List<Cookie> localhostCookie = deepCopy(cookieStore.get(host));
            localhostCookie = cookieStore.get(host);
            if (localhostCookie == null || localhostCookie.isEmpty()) {
                localhostCookie = repsCookies;
            } else {
                //拿到复制之后，就是两个list求无重复并集的问题了。
                updataCookies(localhostCookie, repsCookies);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("此时无重复并集: " + localhostCookie.toString());
        cookieStore.put(host, localhostCookie);
        // System.out.println("整理后的本地cookies: " + cookieStore.toString());
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        List<Cookie> cookiesInStore = cookieStore.get(httpUrl.host());
        List<Cookie> cookies = cookiesInStore != null ? cookiesInStore : new ArrayList<Cookie>();
        System.out.println("我们要访问：" + httpUrl);
        System.out.println("host是: " + httpUrl.host());
        System.out.println("此时本地的cookies: " + cookieStore.toString());
        System.out.println("发出的Req cookies: " + cookies.toString());
        return cookies;

    }

    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    void updataCookies(List<Cookie> localhostCookie, List<Cookie> repsCookies) {
        Iterator<Cookie> localIte = localhostCookie.iterator();

        for (Cookie repsC : repsCookies) {
            while (localIte.hasNext()) {
                Cookie localC = localIte.next();
                if (repsC.name() == localC.name()) {
                    localIte.remove();
                    break;
                }
            }
            // localhostCookie.add(repsC);
        }
        localhostCookie.addAll(repsCookies);
        // System.out.println("repsCookies: " + repsCookies.toString());
        System.out.println("localhostCookie: " + localhostCookie.toString());
    }
}
