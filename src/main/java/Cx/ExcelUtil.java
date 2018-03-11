package Cx;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;

public class ExcelUtil {
    String FilePath = "src/main/res/水口账号(1900).xls";
    Sheet sheet = null;

    HashMap<String, String> GetAllData() {
        HashMap<String, String> UserInfo = new HashMap<String, String>();
        sheet = getSheet(FilePath);

        int UserIDClumn1 = 1; //有数据的一共两列
        int UserIDClumn2 = 5;

        LoopGetData(UserIDClumn1, UserInfo);
        LoopGetData(UserIDClumn2, UserInfo);
        System.out.println(UserInfo.size());
        // System.out.println(UserInfo.get("57205000509083"));
        return UserInfo;
    }

    void LoopGetData(int UserIDClumn, Map<String, String> UserInfo) {
        for (int i = 0;; i++) {
            String UserID = getCell(i, UserIDClumn);
            if (UserID.equals("")) {
                break;
            } else if (UserID.equals("用户名")) {
                continue;
            }
            String UserPwd = getCell(i, UserIDClumn + 1);
            UserInfo.put(UserID, UserPwd);
        }
    }

    String getCell(int rowN, int columnN) {
        Row row = sheet.getRow(rowN);
        Cell cell = row.getCell(columnN);
        if (cell == null) {
            // System.err.println("empty cell");
            return "";
        }
        return cell.getStringCellValue();
    }

    Sheet getSheet(String FilePath) {
        Sheet sheet = null;
        try {
            InputStream inputStream = new FileInputStream(FilePath);
            Workbook workbook = WorkbookFactory.create(inputStream);
            sheet = workbook.getSheetAt(0); //默认第一张表
        } catch (Exception e) {
            System.err.println("getSheet");
        }
        return sheet;
    }

}