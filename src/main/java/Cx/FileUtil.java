package Cx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import net.sourceforge.tess4j.Tesseract;

class FileUtil {
    File DownLoadFile(String FilePath, InputStream in) {
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
        return file;
    }

    File DownLoadHtml(String filePath, InputStream in) {
        File file = null;
        try {
            file = new File(filePath); //路径+文件名
            FileOutputStream fos = new FileOutputStream(file);
            int readSign = 0;
            while ((readSign = in.read()) != -1) {
                fos.write(readSign);
            }
            in.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return file;
    }

    String tess4JOCR(File picfile) {
        String dataPath = "src/main/res/download/tessdata";
        Tesseract instance = new Tesseract();
        instance.setDatapath(dataPath);
        String result = null;
        try {
            result = instance.doOCR(picfile);
        } catch (Exception e) {
            System.err.println("tess4JOCR Fucj");
        }
        return result;
    }

}
