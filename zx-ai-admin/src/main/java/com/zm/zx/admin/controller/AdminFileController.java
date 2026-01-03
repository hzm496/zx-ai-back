package com.zm.zx.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@SaCheckRole("admin")
@Slf4j
@RestController
@RequestMapping("/admin/file")
@RequiredArgsConstructor
public class AdminFileController {
    private final FileService fileService;

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
