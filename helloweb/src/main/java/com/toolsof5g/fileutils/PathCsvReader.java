package com.toolsof5g.fileutils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import com.csvreader.CsvReader;

public class PathCsvReader {
    private PathCsvReader() {
        throw new IllegalStateException("Utility class");
    }

    private static Map<String, String> file2Path = new HashMap<>();

    private static void readFile2PathFromStringList(List<String[]> content) {
        for (int row = 0; row < content.size(); row++) {
            String fileName = content.get(row)[0];
            String filePath = content.get(row)[1];
            file2Path.put(fileName, filePath);
        }
    }

    private static List<String[]> readCSV2StringList(String csvFilePath) throws IOException {
        CsvReader csvReader = new CsvReader(csvFilePath, ',', StandardCharsets.UTF_8);
        List<String[]> content = new ArrayList<>();
        while (csvReader.readRecord()) {
            String line = csvReader.getRawRecord();
            System.out.println(line);
            content.add(csvReader.getValues());
        }
        csvReader.close();
        return content;
    }

    public static Map<String, String> readCsvFile(String csvFilePath) { 
        try {
            List<String[]> content = readCSV2StringList(csvFilePath);
            readFile2PathFromStringList(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file2Path;
    }

}