package com.petadopt.controller;

import com.petadopt.common.Result;
import com.petadopt.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public Result<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileService.uploadFile(file);
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", file.getOriginalFilename());
            return Result.success("上传成功", result);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/uploads")
    public Result<List<String>> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> urls = fileService.uploadFiles(files);
            return Result.success("上传成功", urls);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
