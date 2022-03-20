package com.capture.files.controller;

import com.capture.api.BaseController;
import com.capture.api.controller.files.FileUploaderControllerApi;
import com.capture.exception.GraceException;
import com.capture.files.resource.FileResource;
import com.capture.files.service.UploaderService;
import com.capture.grace.result.GraceJSONResult;
import com.capture.grace.result.ResponseStatusEnum;
import com.capture.pojo.bo.NewAdminBO;
import com.capture.utils.FileUtils;
import com.capture.utils.extend.AliImageReviewUtils;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.mongodb.gridfs.GridFS;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FileUploaderController extends BaseController implements FileUploaderControllerApi {

    final static Logger logger = LoggerFactory.getLogger(FileUploaderController.class);

    @Autowired
    private UploaderService uploaderService;

    @Autowired
    private FileResource fileResource;

    @Autowired
    private AliImageReviewUtils aliImageReviewUtils;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Value("${adminFace.temp_path}")
    public String adminFacePath;

    @Override
    public GraceJSONResult uploadFace(String userId,
                                      MultipartFile file) throws Exception {

        String path = "";
        if (file != null) {
            // 获得文件上传的名称
            String fileName = file.getOriginalFilename();

            // 判断文件名不能为空
            if (StringUtils.isNotBlank(fileName)) {
                String fileNameArr[] = fileName.split("\\.");
                // 获得后缀
                String suffix = fileNameArr[fileNameArr.length - 1];
                // 判断后缀符合我们的预定义规范
                if (!suffix.equalsIgnoreCase("png") &&
                        !suffix.equalsIgnoreCase("jpg") &&
                        !suffix.equalsIgnoreCase("jpeg")
                ) {
                    return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_FORMATTER_FAILD);
                }

                // 执行上传
                path = uploaderService.uploadFdfs(file, suffix);
//                path = uploaderService.uploadOSS(file, userId, suffix);

            } else {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
            }
        } else {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        logger.info("path = " + path);

        String finalPath = "";
        if (StringUtils.isNotBlank(path)) {
            finalPath = fileResource.getHost() + path;
//            finalPath = fileResource.getOssHost() + path;
        } else {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

//        return GraceJSONResult.ok(doAliImageReview(finalPath));
        return GraceJSONResult.ok(finalPath);
    }

    @Override
    public GraceJSONResult uploadSomeFiles(String userId,
                                           MultipartFile[] files)
            throws Exception {


        // 声明list，用于存放多个图片的地址路径，返回到前端
        List<String> imageUrlList = new ArrayList<>();
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                String path = "";
                if (file != null) {
                    // 获得文件上传的名称
                    String fileName = file.getOriginalFilename();

                    // 判断文件名不能为空
                    if (StringUtils.isNotBlank(fileName)) {
                        String fileNameArr[] = fileName.split("\\.");
                        // 获得后缀
                        String suffix = fileNameArr[fileNameArr.length - 1];
                        // 判断后缀符合我们的预定义规范
                        if (!suffix.equalsIgnoreCase("png") &&
                                !suffix.equalsIgnoreCase("jpg") &&
                                !suffix.equalsIgnoreCase("jpeg")
                        ) {
                            //某个文件出现错误 跳过继续上传
                            continue;
                        }

                        // 执行上传
                        path = uploaderService.uploadFdfs(file, suffix);
                        //path = uploaderService.uploadOSS(file, userId, suffix);

                    } else {
                        //某个文件出现错误 跳过继续上传
                        continue;
                    }
                } else {
                    //某个文件出现错误 跳过继续上传
                    continue;
                }

                String finalPath = "";
                if (StringUtils.isNotBlank(path)) {
                    finalPath = fileResource.getHost() + path;
                    //finalPath = fileResource.getOssHost() + path;
                    imageUrlList.add(finalPath);
                } else {
                    continue;
                }
            }
        }

        return GraceJSONResult.ok(imageUrlList);
    }

    public static final String FAILED_IMAGE_URL = "https://imooc-news.oss-cn-shanghai.aliyuncs.com/image/faild.jpeg";
    private String doAliImageReview(String pendingImageUrl) {

        /**
         * fastdfs 默认存在于内网，无法被阿里云内容管理服务检查到
         * 需要配置到公网才行：
         * 1. 内网穿透，natppp/花生壳/ngrok
         * 2. 路由配置端口映射
         * 3. fdfs 发布到云服务器
         */
        boolean result = false;
        try {
            result = aliImageReviewUtils.reviewImage(pendingImageUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!result) {
            return FAILED_IMAGE_URL;
        }

        return pendingImageUrl;
    }

    @Override
    public GraceJSONResult uploadToGridFS(NewAdminBO newAdminBO)
            throws Exception {

        // 获得图片的base64字符串
        String file64 = newAdminBO.getImg64();

        // 将base64字符串转换为byte数组
        byte[] bytes = new BASE64Decoder().decodeBuffer(file64.trim());

        // 转换为输入流
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        // 上传到gridfs中  fileId文件的主键
        ObjectId fileId = gridFSBucket.uploadFromStream(newAdminBO.getUsername() + ".png", inputStream);

        // 获得文件在gridfs中的主键id
        String fileIdStr = fileId.toString();

        //将id 回传前端 前端拿到执行保存
        return GraceJSONResult.ok(fileIdStr);
    }

    @Override
    public void readInGridFS(String faceId,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {

        // 0. 判断参数
        if (StringUtils.isBlank(faceId) || faceId.equalsIgnoreCase("null")) {
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }

        // 1. 从gridfs中读取
        File adminFace = readGridFSByFaceId(faceId);

        // 2. 把人脸图片输出到浏览器
        FileUtils.downloadFileByStream(response, adminFace);
    }

    private File readGridFSByFaceId(String faceId) throws Exception {

        GridFSFindIterable gridFSFiles
                = gridFSBucket.find(Filters.eq("_id", new ObjectId(faceId)));

        GridFSFile gridFS = gridFSFiles.first();

        if (gridFS == null) {
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }

        String fileName = gridFS.getFilename();
        System.out.println(fileName);

        // 获取文件流，保存文件到本地或者服务器的临时目录
        File fileTemp = new File(adminFacePath);
        if (!fileTemp.exists()) {
            fileTemp.mkdirs();
        }

        File myFile = new File(adminFacePath + fileName);

        // 创建文件输出流
        OutputStream os = new FileOutputStream(myFile);
        // 下载到服务器或者本地
        gridFSBucket.downloadToStream(new ObjectId(faceId), os);

        return myFile;
    }

    @Override
    public GraceJSONResult readFace64InGridFS(String faceId,
                                              HttpServletRequest request,
                                              HttpServletResponse response)
            throws Exception {

        // 0. 获得gridfs中人脸文件
        File myface = readGridFSByFaceId(faceId);

        // 1. 转换人脸为base64
        String base64Face = FileUtils.fileToBase64(myface);

        return GraceJSONResult.ok(base64Face);
    }
}
