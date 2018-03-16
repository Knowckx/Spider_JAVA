package Cx;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class WebUtil {
    //主流程0
    void mainLC() {
        System.out.println("WebUtil Start");
        try {
            ExcelUtil ExcelFile = new ExcelUtil();
            HashMap<String, String> UserInfo = ExcelFile.GetAllData();
            // UserInfo.clear();
            // UserInfo.put("57205000509167", "01131X");
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

                //     public void run() {
                //         try {
                //             // System.out.println("第" +1 + "个线程" +Thread.currentThread().getName());
                //             System.out.printf("Progress: %s/%s \n", Cnt++, mspSize);
                //             System.out.printf("UserID,PWD = %s,%s \n", UserID, PWD);
                //             CrawlerUtil crawler = new CrawCrawlerUtil(UserID, PWD);
                //             crawler.Start();
                //         } catch (InterruptedException e) {
                //             e.printStackTrace();
                //         }
                //     }
                // });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("WebUtil end");
    }
}
