package com.example.file.service.zip;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtil {

    private static void write(InputStream inputStream, OutputStream outStream) throws IOException{
        byte[] data=new byte[4096];
        int length=0;
        while((length=inputStream.read(data)) != -1){
            outStream.write(data,0,length);
        }
        outStream.flush();//刷新输出流
        inputStream.close();//关闭输入流
    }


    public static void unzip(String zipFile, String destDir) throws IOException {
        unzip(new File(zipFile), new File(destDir));
    }

    /**
     * 解压文件n
     */
    public static void unzip(File zipFile, File destDir) throws IOException {

        ZipFile zipOutFile = new ZipFile(zipFile);
        Enumeration<? extends ZipEntry> entries = zipOutFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if(entry.isDirectory()){
                File tempFile = new File(destDir.getAbsolutePath()+File.separator+entry.getName());
                if (!tempFile.exists()) {
                    tempFile.mkdirs();
                }
            }else{
                File tempFile=new File(destDir.getAbsolutePath()+File.separator+entry.getName());
                checkParentDir(tempFile);
                FileOutputStream fileOutStream=new FileOutputStream(tempFile);
                BufferedOutputStream bufferOutStream = new BufferedOutputStream(fileOutStream);
                write(zipOutFile.getInputStream(entry),bufferOutStream);
                bufferOutStream.close();
                fileOutStream.close();
            }
        }
        zipOutFile.close();//关闭zip文件
    }

    /**
     * 验证父目录是否存在，否则创建
     */
    private static void checkParentDir(File file){
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
    }

    public static void main(String[] args) throws IOException {
        unzip("D:\\datas\\zip\\20200717-1-001.ZIP","D:\\datas\\zip\\20200717-1-001");
    }
}
