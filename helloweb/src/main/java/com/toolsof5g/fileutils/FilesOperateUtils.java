package com.toolsof5g.fileutils;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;


class FilesOperateMetaClass{
    public Path stringToPath(String path){
        return Paths.get(path);
    }

    protected static Path getDestPath(Path sourceFolder, Path destDir) throws IOException {
        if (!Files.exists(destDir)) {
            Files.createDirectories(destDir);
        }
        return destDir.resolve(sourceFolder.getFileName());
    }

    protected static void deleteIfExists(Path dir) throws IOException {
        try {
            Files.deleteIfExists(dir);
        } catch (DirectoryNotEmptyException e) {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return super.postVisitDirectory(dir, exc);
                }
            });
        }
    }
    
    protected static void clearDirBasedOnReplaceOption(Path dest, CopyOption... options) throws IOException {
        boolean clear=true;     
        for(CopyOption option:options)
            if(StandardCopyOption.REPLACE_EXISTING==option){
                clear=false;
                break;
            }
        if(clear)deleteIfExists(dest);
    }
}


class FileUtils extends FilesOperateMetaClass {
    private FileUtils() {
    }

    private static void operateSingleFile(boolean isMove, Path sourceFile, Path destDir, CopyOption... options) throws IOException{
        if(sourceFile==null||Files.isDirectory(sourceFile)){
            throw new IllegalArgumentException("Source folder should not be directory");
        }

        Path destFile = getDestPath(sourceFile, destDir);

        if(Files.exists(destFile)&&Files.isSameFile(sourceFile, destFile))return;

        clearDirBasedOnReplaceOption(destFile, options);
        
        if(isMove){
            Files.move(sourceFile, destFile, options);
        } else{
            Files.copy(sourceFile, destFile, options);
        }
    }
    
    public static void deleteSingleFile(Path file) throws IOException {
        deleteIfExists(file);
    }

    public static void copySingleFile(Path sourceFile, Path destDir, CopyOption... options) throws IOException{
        operateSingleFile(false, sourceFile, destDir, options);
    }
    
    public static void moveSingleFile(Path sourceFile, Path destDir, CopyOption... options) throws IOException{
        operateSingleFile(true, sourceFile, destDir, options);
    }

}


class FolderUtils extends FilesOperateMetaClass{
    private FolderUtils() {
    }

    private static boolean sameOrSub(Path sub, Path parent) throws IOException{
        if(parent==null)
            throw new NullPointerException("parent is null");
        if(!Files.exists(parent)||!Files.isDirectory(parent))
            throw new IllegalArgumentException(String.format("the parent not exist or not directory %s",parent));
        while(sub!=null) {
            if(Files.exists(sub)&&Files.isSameFile(parent, sub))
                return true;
            sub=sub.getParent();
        }
        return false;
    }

    private static boolean isSub(Path sub, Path parent) throws IOException{
        if (sub==null){
            return false;
        }else{
            return sameOrSub(sub.getParent(),parent);
        }
    }

    private static void operateFolder(final boolean isMove, final Path sourceFolder,
        final Path destDir,final CopyOption... options) throws IOException{

        if(sourceFolder==null||!Files.isDirectory(sourceFolder)){
            throw new IllegalArgumentException("Source folder should be directory");
        }
        final Path destFolder = getDestPath(sourceFolder, destDir);

        if(Files.exists(destFolder)&&Files.isSameFile(sourceFolder, destFolder))return;
        if(isSub(destFolder, sourceFolder))
            throw new IllegalArgumentException("Dest folder should not be sub directory of source folder");

        clearDirBasedOnReplaceOption(destFolder, options);

        Files.walkFileTree(sourceFolder, 
                new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        Path subDir = 0==dir.compareTo(sourceFolder)?destFolder:destFolder.resolve(dir.subpath(sourceFolder.getNameCount(), dir.getNameCount()));
                        Files.createDirectories(subDir);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if(isMove)
                            Files.move(file, destFolder.resolve(file.subpath(sourceFolder.getNameCount(), file.getNameCount())),options);
                        else
                            Files.copy(file, destFolder.resolve(file.subpath(sourceFolder.getNameCount(), file.getNameCount())),options);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        if(isMove)
                            Files.delete(dir);
                        return super.postVisitDirectory(dir, exc);
                    }
                });
    }

    public static void deleteFolder(Path dir) throws IOException {
        deleteIfExists(dir);
    }

    public static void copyFolder(Path sourceFolder, Path destDir, CopyOption... options) throws IOException{
        operateFolder(false, sourceFolder, destDir, options);
    }
    
    public static void moveFolder(Path sourceFolder, Path destDir, CopyOption... options) throws IOException{
        operateFolder(true, sourceFolder, destDir, options);
    }
}

public class FilesOperateUtils{
    private FilesOperateUtils() {
    }

    public static String zipAndRenameIfDirecory(String fileTempPath) throws Exception {
        Path filePath = Paths.get(fileTempPath);
        if (Files.isDirectory(filePath)){
            String zipFileName = fileTempPath+".zip";
            ZipUtils.toZip(fileTempPath, zipFileName);
            FolderUtils.deleteFolder(filePath);
            return zipFileName;
        } else{
            return fileTempPath;
        }
    }

    private static Path string2Path(String str){
        return Paths.get(str);
    }

    public static String combinStringWithFileSeparator(String stringA, String stringB) {
        return stringA + File.separator + stringB;
    }

    public static void deleteFile(String destString) throws IOException {
        Path destPath = string2Path(destString);
        if (Files.isDirectory(destPath)){
            FolderUtils.deleteFolder(destPath);
        } else{
            FileUtils.deleteSingleFile(destPath);
        }
    }

    public static void moveFile(String sourceString, String destDirString) throws IOException {
        Path sourcePath = string2Path(sourceString);
        Path destDirPath = string2Path(destDirString);

        if (Files.isDirectory(sourcePath)){
            FolderUtils.moveFolder(sourcePath,destDirPath);
        } else{
            FileUtils.moveSingleFile(sourcePath,destDirPath);
        }
    }

    public static void copyFile(String sourceString, String destDirString) throws IOException {
        Path sourcePath = string2Path(sourceString);
        Path destDirPath = string2Path(destDirString);

        if (Files.isDirectory(sourcePath)){
            FolderUtils.copyFolder(sourcePath,destDirPath);
        } else{
            FileUtils.copySingleFile(sourcePath,destDirPath);
        }
    }

}

