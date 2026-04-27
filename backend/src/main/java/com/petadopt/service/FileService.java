package com.petadopt.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload.path}")
    private String uploadPath;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFilename = LocalDateTime.now().format(FORMATTER) + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;

        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path targetPath = uploadDir.resolve(newFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + newFilename;
    }

    public List<String> uploadFiles(MultipartFile[] files) throws IOException {
        List<String> urls = new ArrayList<>();
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    urls.add(uploadFile(file));
                }
            }
        }
        return urls;
    }
}
