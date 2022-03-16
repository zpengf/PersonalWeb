package com.capture.files.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploaderService {

    /**
     * 使用fastdfs上传文件
     *
     * fileExtName 文件扩展名
     * 返回的路径 前端通过路径展示图片
     */
    public String uploadFdfs(MultipartFile file, String fileExtName) throws Exception;

    /**
     * 使用OSS上传文件
     */
    public String uploadOSS(MultipartFile file,
                            String userId,
                            String fileExtName) throws Exception;

}
