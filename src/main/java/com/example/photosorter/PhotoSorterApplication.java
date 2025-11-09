package com.example.photosorter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;

import lombok.Setter;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

@SpringBootApplication
@Component
@Setter
public class PhotoSorterApplication implements CommandLineRunner {

    @Value("${photo-sorter.source-folder}")
    private String sourceFolderPath;

    @Value("${photo-sorter.destination-folder}")
    private String destinationFolderPath;
    private File destinationRoot;

    public static void main(String[] args) {
        SpringApplication.run(PhotoSorterApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        long startTime = System.currentTimeMillis();
        System.out.println("開始執行照片分類作業...");
        System.out.println("來源資料夾: " + sourceFolderPath);
        System.out.println("目標資料夾: " + destinationFolderPath);

        File sourceFolder = new File(sourceFolderPath);
        destinationRoot = new File(destinationFolderPath);
        if (!destinationRoot.exists()) {
            destinationRoot.mkdirs();
        }
        if (!sourceFolder.exists() || !sourceFolder.isDirectory()) {
            System.out.println("來源資料夾 " + sourceFolderPath + " 不存在或不是一個有效的資料夾。");

            System.out.println("指定的資料夾不存在或不是一個有效的資料夾。");
            return;
        }
        sortPhotosByCreationDate(sourceFolder);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        long hours = duration / 3600000;
        long minutes = (duration % 3600000) / 60000;
        long seconds = (duration % 60000) / 1000;
        long millis = duration % 1000;

        System.out.println("照片分類作業完成！");
        System.out.println("總耗時: " + hours + " 時 " + minutes + " 分 " + seconds + " 秒 " + millis + " 毫秒");
    }

    private void sortPhotosByCreationDate(File sourceFolder) throws IOException {
        File[] files = sourceFolder.listFiles();
        if (files != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
            int processedCount = 0;
            TreeSet<String> monthFolders = new TreeSet<>();
            for (File file : files) {
                if (file.isFile()) {
                    Date photoDate = null;
                    try {
                        Metadata metadata = ImageMetadataReader.readMetadata(file);
                        ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                        if (directory != null) {
                            photoDate = directory.getDateOriginal();
                        }
                    } catch (Exception e) {
                        // 讀取 EXIF 失敗時忽略，改用檔案建立日期
                    }
                    if (photoDate == null) {
                        BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                        photoDate = new Date(attributes.creationTime().toMillis());
                    }
                    String folderName = dateFormat.format(photoDate);
                    monthFolders.add(folderName);
                    File destinationFolder = new File(destinationRoot, folderName);
                    if (!destinationFolder.exists()) {
                        destinationFolder.mkdirs();
                    }
                    File destinationFile = new File(destinationFolder, file.getName());
                    // 确保目标目录存在
                    Files.createDirectories(destinationFile.toPath().getParent());
                    Files.copy(file.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    processedCount++;
                }
            }
            System.out.println("總共處理了 " + processedCount + " 個檔案");
            if (!monthFolders.isEmpty()) {
                System.out.println("處理月份範圍: " + monthFolders.first() + " ~ " + monthFolders.last() + " (共 "
                        + monthFolders.size() + " 個月份)");
            }
        }
    }
}