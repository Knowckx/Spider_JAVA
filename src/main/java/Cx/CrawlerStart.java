package Cx;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CrawlerStart {
    //主流程0
    void mainLC() {
        System.out.println("WebUtil Start");
        ExcelUtil ExcelFile = new ExcelUtil();
        HashMap<String, String> UserInfo = ExcelFile.GetAllData();
        // UserInfo.clear();
        // UserInfo.put("57205000509167", "01131X");
        try {
            int mspSize = UserInfo.size();
            int Cnt = 1;
            int ThreadPoolCnt = 10;
            ExecutorService fixedThreadPool = Executors.newFixedThreadPool(ThreadPoolCnt);
            for (String key : UserInfo.keySet()) {
                // System.out.printf("Progress: %s/%s \n", Cnt++, mspSize);
                String UserID = key;
                String PWD = UserInfo.get(key);
                // fixedThreadPool.execute(new CrawlerUtil(UserID,PWD));
                fixedThreadPool.execute(new CrawlerUtil(UserID,PWD,Cnt++,mspSize));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("WebUtil end");
    }

    void mainTakeTest() {
        ExcelUtil ExcelFile = new ExcelUtil();
        HashMap<String, String> UserInfo = ExcelFile.GetAllData();
        // UserInfo.clear();
        // UserInfo.put("57205000509167", "01131X");
        ConcurrentHashMap<String, String> UserInfoFail = new ConcurrentHashMap<String, String> ();
        try {
            int mspSize = UserInfo.size();
            int Cnt = 1;
            int ThreadPoolCnt = 10;
            ExecutorService fixedThreadPool = Executors.newFixedThreadPool(ThreadPoolCnt);
            for (String key : UserInfo.keySet()) {
                String UserID = key;
                String PWD = UserInfo.get(key);
                fixedThreadPool.execute(new TakeTestUtil(UserID,PWD,Cnt++, mspSize,UserInfoFail));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("WebUtil end");
    }
}
