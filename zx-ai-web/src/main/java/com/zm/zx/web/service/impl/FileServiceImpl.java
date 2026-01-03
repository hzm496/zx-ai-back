package com.zm.zx.web.service.impl;

import com.zm.zx.common.config.MinioProperties;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.service.FileService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Resource
    private MinioProperties minioProperties;
    @Resource
    private MinioClient minioClient;
    
    public static final String BUCKET_NAME = "zx-ai";
    
    /**
     * 初始化 Minio Bucket
     */
    @PostConstruct
    public void initBucket() {
        try {
            // 检查 bucket 是否存在
            boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(BUCKET_NAME).build()
            );
            
            if (!bucketExists) {
                log.info("==> Bucket '{}' 不存在，开始创建...", BUCKET_NAME);
                
                // 创建 bucket
                minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(BUCKET_NAME).build()
                );
                
                // 设置 bucket 为公开读权限
                String policy = "{\n" +
                    "  \"Version\": \"2012-10-17\",\n" +
                    "  \"Statement\": [\n" +
                    "    {\n" +
                    "      \"Effect\": \"Allow\",\n" +
                    "      \"Principal\": {\"AWS\": [\"*\"]},\n" +
                    "      \"Action\": [\"s3:GetObject\"],\n" +
                    "      \"Resource\": [\"arn:aws:s3:::" + BUCKET_NAME + "/*\"]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
                
                minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                        .bucket(BUCKET_NAME)
                        .config(policy)
                        .build()
                );
                
                log.info("==> Bucket '{}' 创建成功，并已设置为公开读权限", BUCKET_NAME);
            } else {
                log.info("==> Bucket '{}' 已存在", BUCKET_NAME);
            }
        } catch (Exception e) {
            log.error("==> 初始化 Minio Bucket 失败", e);
        }
    }
    
    // 图片允许的格式
    private static final List<String> IMAGE_CONTENT_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp", "image/bmp"
    );
    
    // 图片允许的扩展名
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(
        ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp"
    );
    
    // 视频允许的格式
    private static final List<String> VIDEO_CONTENT_TYPES = Arrays.asList(
        "video/mp4", "video/avi", "video/mov", "video/wmv", "video/flv", "video/webm"
    );
    
    // 视频允许的扩展名
    private static final List<String> VIDEO_EXTENSIONS = Arrays.asList(
        ".mp4", ".avi", ".mov", ".wmv", ".flv", ".webm", ".m4v"
    );
    
    // 文件大小限制（字节）
    private static final long IMAGE_MAX_SIZE = 10 * 1024 * 1024; // 10MB
    private static final long VIDEO_MAX_SIZE = 500 * 1024 * 1024; // 500MB
 
    @Override
    public Response<?> uploadFile(MultipartFile file) {
        try {
            String url = minioUploadFile(file, BUCKET_NAME, "files");
            return Response.success(url);
        } catch (Exception e) {
            log.error("==> 文件上传失败", e);
            throw new BizException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public Response<?> uploadImage(MultipartFile file) {
        try {
            // 验证文件类型
            validateFileType(file, IMAGE_CONTENT_TYPES, IMAGE_EXTENSIONS, "图片");
            
            // 验证文件大小
            validateFileSize(file, IMAGE_MAX_SIZE, "图片");
            
            // 上传到 images 目录
            String url = minioUploadFile(file, BUCKET_NAME, "images");
            return Response.success(url);
        } catch (Exception e) {
            log.error("==> 图片上传失败", e);
            throw new BizException("图片上传失败: " + e.getMessage());
        }
    }

    @Override
    public Response<?> uploadVideo(MultipartFile file) {
        try {
            // 验证文件类型
            validateFileType(file, VIDEO_CONTENT_TYPES, VIDEO_EXTENSIONS, "视频");
            
            // 验证文件大小
            validateFileSize(file, VIDEO_MAX_SIZE, "视频");
            
            // 上传到 videos 目录
            String url = minioUploadFile(file, BUCKET_NAME, "videos");
            return Response.success(url);
        } catch (Exception e) {
            log.error("==> 视频上传失败", e);
            throw new BizException("视频上传失败: " + e.getMessage());
        }
    }

    /**
     * 验证文件类型（支持 Content-Type 和文件扩展名双重验证）
     */
    private void validateFileType(MultipartFile file, List<String> allowedTypes, List<String> allowedExtensions, String fileTypeDesc) {
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        log.info("==> 文件类型检查 - 文件名: {}, ContentType: {}", originalFilename, contentType);
        
        // 获取文件扩展名
        String fileExtension = "";
        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }
        
        boolean isValid = false;
        
        // 方式1：通过 Content-Type 验证（但排除 application/octet-stream）
        if (StringUtils.hasText(contentType) && !contentType.equals("application/octet-stream")) {
            String mainContentType = contentType.toLowerCase().split(";")[0].trim();
            for (String allowedType : allowedTypes) {
                if (mainContentType.equals(allowedType.toLowerCase())) {
                    isValid = true;
                    log.info("==> 文件类型验证通过（Content-Type）: {}", mainContentType);
                    break;
                }
            }
        }
        
        // 方式2：通过文件扩展名验证（作为备选或主要方式）
        if (!isValid && StringUtils.hasText(fileExtension)) {
            for (String allowedExt : allowedExtensions) {
                if (fileExtension.equals(allowedExt.toLowerCase())) {
                    isValid = true;
                    log.info("==> 文件类型验证通过（扩展名）: {}", fileExtension);
                    break;
                }
            }
        }
        
        if (!isValid) {
            String errorMsg = fileTypeDesc + "格式不支持，当前文件: " + originalFilename + 
                             "，支持的扩展名: " + String.join(", ", allowedExtensions);
            log.error("==> {}", errorMsg);
            throw new BizException(errorMsg);
        }
    }

    /**
     * 验证文件大小
     */
    private void validateFileSize(MultipartFile file, long maxSize, String fileTypeDesc) {
        if (file.getSize() > maxSize) {
            throw new BizException(fileTypeDesc + "大小不能超过 " + (maxSize / 1024 / 1024) + "MB");
        }
    }
 
    /**
     * 上传文件到 Minio
     * 
     * @param file 文件
     * @param bucketName 桶名称
     * @param folder 文件夹名称（如 images、videos、files）
     * @return 文件访问 URL
     */
    private String minioUploadFile(MultipartFile file, String bucketName, String folder) {
        try {
            log.info("## 上传文件至 Minio ...");

            // 判断文件是否为空
            if (file == null || file.getSize() == 0) {
                log.error("==> 上传文件异常：文件大小为空");
                throw new BizException("文件大小不能为空");
            }

            // 文件的原始名称
            String originalFileName = file.getOriginalFilename();
            if (!StringUtils.hasText(originalFileName)) {
                throw new BizException("文件名不能为空");
            }
            
            // 文件的 Content-Type
            String contentType = file.getContentType();

            // 生成存储对象的名称（将 UUID 字符串中的 - 替换成空字符串）
            String key = UUID.randomUUID().toString().replace("-", "");
            
            // 获取文件的后缀，如 .jpg
            String suffix = "";
            if (originalFileName.contains(".")) {
                suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            // 按日期组织文件：folder/2025/01/13/uuid.jpg
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String objectName = String.format("%s/%s/%s%s", folder, datePath, key, suffix);

            log.info("==> 开始上传文件至 Minio, ObjectName: {}", objectName);

            // 上传文件至 Minio
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(contentType)
                    .build());

            // 返回文件的访问链接
            String url = String.format("%s/%s/%s", minioProperties.getEndpoint(), bucketName, objectName);
            log.info("==> 上传文件至 Minio 成功，访问路径: {}", url);
            return url;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("==> 上传文件至 Minio 失败", e);
            throw new BizException("文件上传失败: " + e.getMessage());
        }
    }
 
 
}