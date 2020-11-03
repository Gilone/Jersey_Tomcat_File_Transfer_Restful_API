package com.toolsof5g.fileutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    private ZipUtils(){
        throw new IllegalStateException("Utility class");
    }

    private static final int  BUFFER_SIZE = 2 * 1024;

    private static void compressSingleFile(File sourceFile, ZipOutputStream zOutputStream, String nameInZip){
        byte[] buf = new byte[BUFFER_SIZE];
        try(FileInputStream inStream = new FileInputStream(sourceFile)){
            zOutputStream.putNextEntry(new ZipEntry(nameInZip));
            int len;
            while ((len = inStream.read(buf)) != -1){
                zOutputStream.write(buf, 0, len);
            }
            zOutputStream.closeEntry();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void compressMultiFile(File sourceFile, ZipOutputStream zOutputStream, String nameInZip)
            throws Exception {
        File[] listFiles = sourceFile.listFiles();
        if(listFiles == null || listFiles.length == 0){
            zOutputStream.putNextEntry(new ZipEntry(nameInZip + "/"));
            zOutputStream.closeEntry();
        }else {
            for (File file : listFiles) {
            compress(file, zOutputStream, nameInZip + "/" + file.getName());
            }
        }
    }

    private static void compress(File sourceFile, ZipOutputStream zOutputStream, String nameInZip) throws Exception{
        if(sourceFile.isFile()){
            compressSingleFile(sourceFile, zOutputStream, nameInZip);
        } else {
            compressMultiFile(sourceFile, zOutputStream, nameInZip);
        }
    }

    public static void toZip(String srcDir, String outPath) throws Exception {
        try (ZipOutputStream zOutputStream = new ZipOutputStream(new FileOutputStream(new File(outPath)))){
            File sourceFile = new File(srcDir);
            compress(sourceFile,zOutputStream,sourceFile.getName());
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
     
}
