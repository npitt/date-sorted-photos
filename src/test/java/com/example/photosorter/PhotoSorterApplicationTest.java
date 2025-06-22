package com.example.photosorter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PhotoSorterApplicationTest {

    @Autowired
    private PhotoSorterApplication photoSorterApplication;

    @TempDir
    File tempDir;

    @Test
    void testSortPhotosByCreationDate() throws Exception {
        // 創建測試用臨時文件
        File sourceFolder = new File(tempDir, "source");
        sourceFolder.mkdirs();
        File testFile = new File(sourceFolder, "test.jpg");
        Files.createFile(testFile.toPath());
        // 設置文件創建時間為2024-01-15
        Instant creationTime = Instant.parse("2024-01-15T12:00:00Z");
        // 原错误代码（示例）
        Files.setAttribute(testFile.toPath(), "creationTime", FileTime.from(creationTime));

        // 修正后代码
        FileTime fileTime = FileTime.from(creationTime); // 将 Instant 转换为 FileTime
        Files.setAttribute(testFile.toPath(), "creationTime", fileTime);
        // 設置配置參數（臨時目錄）
        System.setProperty("photo-sorter.source-folder", sourceFolder.getAbsolutePath());
        File destinationFolder = new File(tempDir, "destination");
        System.setProperty("photo-sorter.destination-folder", destinationFolder.getAbsolutePath());

        // 執行排序邏輯
        photoSorterApplication.run();

        // 驗證目標文件夾是否存在
        File expectedFolder = new File(destinationFolder, "2024-01");
        assertTrue(expectedFolder.exists(), "目標月份文件夾未創建");

        // 驗證文件是否複製成功
        File copiedFile = new File(expectedFolder, "test.jpg");
        assertTrue(copiedFile.exists(), "文件未成功複製到目標文件夾");
    }
}