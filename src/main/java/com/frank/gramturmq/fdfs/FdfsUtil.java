package com.frank.gramturmq.fdfs;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.fdfs.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * FastDFS操作类.
 *
 * @author 张孝党 2019/12/17.
 * @version V1.00.
 * <p>
 * 更新履历： V1.00 2019/12/17 张孝党 创建.
 */
@Slf4j
@Component
public class FdfsUtil {

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private ThumbImageConfig thumbImageConfig;

    /**
     * 上传图片文件.
     */
    public List<String> uploadImage(MultipartFile multipartFile) throws Exception {
        log.info("FastDFS开始上传图片.........................");

        String originalFileName = multipartFile.getOriginalFilename().
                substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
        log.info("图片的后缀为：[{}]", originalFileName);

        StorePath storePath = this.storageClient.uploadImageAndCrtThumbImage(
                multipartFile.getInputStream(),
                multipartFile.getSize(),
                originalFileName, null);
        log.info("上传后的路径为：[{}]", storePath.getFullPath());
        log.info("FastDFS结束上传图片.........................");
        // 返回路径
        return this.convertImagePath(storePath);
    }

    /**
     * 上传文件.
     */
    public String uploadFile(MultipartFile multipartFile) throws Exception {
        log.info("FastDFS开始上传文档.........................");

        String originalFileName = multipartFile.getOriginalFilename().
                substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
        log.info("文档的后缀为：[{}]", originalFileName);

        StorePath storePath = this.storageClient.uploadFile(multipartFile.getInputStream(),
                multipartFile.getSize(),
                originalFileName, null);
        log.info("上传文档后的路径为：[{}]", storePath.getFullPath());
        log.info("FastDFS结束上传文档.........................");

        // 返回路径
        return this.convertFilePath(storePath);
    }

    /**
     * 上传文件.
     */
    public String uploadLocalFile(File localFile) throws Exception {
        log.info("FastDFS开始上传文档.........................");

        FileInputStream inputStream = new FileInputStream(localFile);

        String originalFileName = localFile.getName().
                substring(localFile.getName().lastIndexOf(".") + 1);
        log.info("文档的后缀为：[{}]", originalFileName);

        StorePath storePath = this.storageClient.uploadFile(inputStream,
                localFile.length(),
                originalFileName, null);
        log.info("上传文档后的路径为：[{}]", storePath.getFullPath());
        log.info("FastDFS结束上传文档.........................");

        // 返回路径
        return this.convertFilePath(storePath);
    }

    /**
     * 删除文件.
     */
    public boolean deleteFile(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) {
            log.info("文件路径为空!");
            return false;
        }

        return true;
    }

    /**
     * 转换图片路径.
     */
    private List<String> convertImagePath(StorePath storePath) {

        List<String> pathList = new ArrayList<>();

        // URL
        String url = "https://www.gramtu.com/";

        // 全路径
        pathList.add(url + storePath.getFullPath());
        // 缩略图
        pathList.add(url + thumbImageConfig.getThumbImagePath(storePath.getFullPath()));

        // 返回
        return pathList;
    }

    /**
     * 转换文件路径.
     */
    private String convertFilePath(StorePath storePath) {

        // URL
        String url = "https://www.gramtu.com/";

        String fullPath = url + storePath.getFullPath();

        // 返回
        return fullPath;
    }
}
