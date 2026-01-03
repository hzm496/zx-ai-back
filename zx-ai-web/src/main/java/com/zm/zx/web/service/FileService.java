package com.zm.zx.web.service;

import com.zm.zx.common.response.Response;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
 
    /**
     * 上传文件
     * 
     * @param file 文件
     * @return 文件访问 URL
     */
    Response<?> uploadFile(MultipartFile file);

    /**
     * 上传图片
     * 
     * @param file 图片文件
     * @return 图片访问 URL
     */
    Response<?> uploadImage(MultipartFile file);

    /**
     * 上传视频
     * 
     * @param file 视频文件
     * @return 视频访问 URL
     */
    Response<?> uploadVideo(MultipartFile file);
}