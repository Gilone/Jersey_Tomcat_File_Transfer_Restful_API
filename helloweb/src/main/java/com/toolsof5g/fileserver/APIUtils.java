package com.toolsof5g.fileserver;

import com.toolsof5g.fileutils.*;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Map;


class APIUtils {
    private APIUtils(){
    }

    private static String decodeAuthString(String authString){
        // Header is in the format "Basic 5tyc0uiDat4"
        // We need to extract data before decoding it back to original string
        String[] authParts = authString.split("\\s+");
        String authInfo = authParts[1];
        byte[] bytes = null;
        bytes = Base64.getDecoder().decode(authInfo);
        return new String(bytes);
    }

    private static boolean copyFileReturnBoolean(String filePath, String destDir) throws IOException {
        try{
            FilesOperateUtils.copyFile(filePath, destDir);
            return true;
        }catch (IOException e) {
            return false;
            }
        }

    public static final String WRITE_CSV_FILE_PATH = FilesOperateUtils
        .combinStringWithFileSeparator(System.getProperty("user.dir"), "filepathinfo.csv");

    public static String getFileTempDirectory(){
        String  fileTempDirectory = FilesOperateUtils.combinStringWithFileSeparator(System.getProperty("user.dir"), "tempfile");
        File fileTempDirectoryFile = new File(fileTempDirectory);
        if (!fileTempDirectoryFile.exists()){
            fileTempDirectoryFile.mkdirs();
        }
        return fileTempDirectory;
    }

    public static boolean isUserAuthenticated(String authString) {
        if (authString == null){
            return false;
        }
        else{
            String decodedAuth = decodeAuthString(authString);
            String passString = "usr:123";
            return decodedAuth.equals(passString);
        }
    }

    public static boolean copyFile2TempDirectory(String fileName, String fileTempDirectory) throws IOException {
        Map<String, String> file2Path = PathCsvReader.readCsvFile(WRITE_CSV_FILE_PATH);
        if (file2Path.containsKey(fileName)){
            String fileTruePath = file2Path.get(fileName);
            return copyFileReturnBoolean(fileTruePath, fileTempDirectory);
        }
        else{
            return false;
        }
    }

    public static Map<String, String> getFilePathMap(){
        return PathCsvReader.readCsvFile(WRITE_CSV_FILE_PATH);
    }

    public static File getFileFromTempDirectory(String fileTempDirectory, String fileName) throws Exception {
        String fileTempPath = FilesOperateUtils.combinStringWithFileSeparator(fileTempDirectory, fileName);
        fileTempPath = FilesOperateUtils.zipAndRenameIfDirecory(fileTempPath);
        return new File(fileTempPath);
    }

    public static String decodeFileName2URL(String fileName){
        String uRLFileName = "";
        try {
            uRLFileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return uRLFileName;
        }
}
