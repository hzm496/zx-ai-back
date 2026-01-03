package com.zm.zx.web.controller;

import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.service.FileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/web/file")
public class FileController {

    @Resource
    private FileService fileService;

    /**
     * 上传图片
     */
    @ApiOperationLog(description = "上传图片")
    @PostMapping("/upload/image")
    public Response<?> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("==> 接收到图片上传请求，文件名: {}, 大小: {} bytes", 
                 file.getOriginalFilename(), file.getSize());
        return fileService.uploadImage(file);
    }

    /**
     * 上传视频
     */
    @ApiOperationLog(description = "上传视频")
    @PostMapping("/upload/video")
    public Response<?> uploadVideo(@RequestParam("file") MultipartFile file) {
        log.info("==> 接收到视频上传请求，文件名: {}, 大小: {} bytes", 
                 file.getOriginalFilename(), file.getSize());
        return fileService.uploadVideo(file);
    }

    /**
     * 上传通用文件
     */
    @ApiOperationLog(description = "上传文件")
    @PostMapping("/upload")
    public Response<?> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("==> 接收到文件上传请求，文件名: {}, 大小: {} bytes", 
                 file.getOriginalFilename(), file.getSize());
        return fileService.uploadFile(file);
    }
}

