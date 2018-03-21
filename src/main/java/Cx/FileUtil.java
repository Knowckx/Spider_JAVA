package Cx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import net.sourceforge.tess4j.Tesseract;

class FileUtil {
    File DownLoad(String FilePath, InputStream in) {
        File file = null;
        try {
            file = new File(FilePath); //路径+文件名
            FileOutputStream fos = new FileOutputStream(file);
            ReadableByteChannel rbc = Channels.newChannel(in);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        System.out.printf("文件下载成功,%s\n", FilePath);
        return file;
    }

    String tess4JOCR(File picfile,String dataPath) {
        Tesseract instance = new Tesseract();
        instance.setDatapath(dataPath);
        String result = "";
        try {
            result = instance.doOCR(picfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
