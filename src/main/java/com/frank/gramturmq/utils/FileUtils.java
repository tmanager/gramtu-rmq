package com.frank.gramturmq.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件操作相关类.
 *
 * @author 张孝党 2019/09/09.
 * @version V0.0.1.
 * <p>
 * 更新履历： V0.0.1 2019/09/09 张孝党 创建.
 */
@Slf4j
public class FileUtils {

    /**
     * 从网络中下载文件.
     *
     * @param httpUrl  网络地址.
     * @param saveDir  保存到本地的路径.
     * @param fileName 下载文件名.
     * @return 返回, true:下载完成,false:下载异常
     */
    public static boolean downloadFromHttpUrl(String httpUrl, String saveDir, String fileName) {

        long startTime = System.currentTimeMillis();
        log.info("下载文件[{}]开始，开始时间：{}", fileName, DateTimeUtil.getTimeformat());
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置超时时间10秒
            conn.setConnectTimeout(20 * 1000);
            // 防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            // 输入流
            InputStream inputStream = conn.getInputStream();
            // 获取数据
            byte[] bytesData = readInputStream(inputStream);

            // 检测本地文件是否存在
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 保存文件
            File file = new File(saveDir + File.separator + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytesData);

            // 关闭写文件
            if (fos != null) {
                fos.close();
            }

            // 关闭网络流
            if (inputStream != null) {
                inputStream.close();
            }
            // 断开网络连接
            conn.disconnect();

            log.info("下载文件[{}]正常结束,结束时间：{}", fileName, DateTimeUtil.getTimeformat());

            long endTime = System.currentTimeMillis();
            log.info("共耗时{}毫秒!", endTime - startTime);

            return true;
        } catch (Exception ex) {
            log.error("下载文件[{}]异常结束,结束时间为：{}，异常信息为：", fileName, DateTimeUtil.getTimeformat());
            log.error(ex.getMessage());
            return false;
        }
    }

    /**
     * 从输入流中获取字节数组.
     *
     * @param inputStream
     */
    private static byte[] readInputStream(InputStream inputStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        // 关闭
        bos.close();
        // 返回
        return bos.toByteArray();
    }

    /**
     * 从FTP服务器中下载文件.
     */
    public static boolean downloadFromFtp(JSONObject ftpInfo, String ftpPath, String saveDir, String fileName) {

        long startTime = System.currentTimeMillis();
        log.info("FTP下载文件[{}]开始，开始时间：{}", fileName, DateTimeUtil.getTimeformat());

        try {
            // 创建FTP客户端
            FTPClient ftpClient = new FTPClient();
            // 连接服务器
            ftpClient.connect(ftpInfo.getString("ftp_ip"));
            log.info("FTP服务器[{}]连接正常.", ftpInfo.getString("ftp_ip"));

            // 登录
            ftpClient.login(ftpInfo.getString("ftp_uname"), ftpInfo.getString("ftp_passwd"));
            log.info("FTP服务器[{}]登录正常.", ftpInfo.getString("ftp_ip"));

            // 获取服务器返回的状态码
            int reply = ftpClient.getReplyCode();
            log.info("FTP服务器返回状态码为[{}]", reply);

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                log.error("FTP服务器[{}]应答失败!", ftpInfo.getString("ftp_ip"));
                return false;
            }

            // 开始下载文件
            File file = new File(saveDir + File.separator + fileName);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);

            boolean isDown = ftpClient.retrieveFile(ftpPath + File.separator + fileName, fos);
            if (isDown) {
                log.info("FTP下载文件[{}]成功!", fileName);
            } else {
                log.info("FTP下载文件[{}]失败!", fileName);
                return false;
            }

            ftpClient.disconnect();
            fos.flush();
            ;
            fos.close();

            log.info("FTP下载文件[{}]正常结束,结束时间：{}", fileName, DateTimeUtil.getTimeformat());

            long endTime = System.currentTimeMillis();
            log.info("FTP下载文件共耗时{}毫秒!", endTime - startTime);

            return true;
        } catch (Exception ex) {
            log.error("FTP下载文件[{}]异常结束,结束时间为：{}，异常信息为：", fileName, DateTimeUtil.getTimeformat());
            log.error(ex.getMessage());
            return false;
        }
    }

    /**
     * 往FTP服务器中上传件.
     */
    public static boolean uploadToFtp(JSONObject ftpInfo, String ftpPath, String localPath, String fileName) {

        long startTime = System.currentTimeMillis();
        log.info("FTP上传文件[{}]开始，开始时间：{}", fileName, DateTimeUtil.getTimeformat());

        try {
            // 创建FTP客户端
            FTPClient ftpClient = new FTPClient();
            // 连接服务器
            ftpClient.connect(ftpInfo.getString("ftp_ip"));
            log.info("FTP服务器[{}]连接正常.", ftpInfo.getString("ftp_ip"));

            // 登录
            ftpClient.login(ftpInfo.getString("ftp_uname"), ftpInfo.getString("ftp_passwd"));
            log.info("FTP服务器[{}]登录正常.", ftpInfo.getString("ftp_ip"));

            // 获取服务器返回的状态码
            int reply = ftpClient.getReplyCode();
            log.info("FTP服务器返回状态码为[{}]", reply);

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                log.error("FTP服务器[{}]应答失败!", ftpInfo.getString("ftp_ip"));
                return false;
            }

            log.info("FTP服务器[{}]开始上传文件，文件名为[{}].", ftpInfo.getString("ftp_ip"), fileName);
            // 开始上传文件
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            File file = new File(localPath + File.separator + fileName);
            FileInputStream fis = new FileInputStream(file);

            boolean isDown = ftpClient.storeFile(ftpPath + File.separator + fileName, fis);
            if (isDown) {
                log.info("FTP上传文件[{}]成功!", fileName);
            } else {
                log.info("FTP上传文件[{}]失败!", fileName);
                return false;
            }

            // ADD BY zhangxd ON 20191013 START
            // COMMENT ON 增加logout动作
            ftpClient.logout();
            // ADD BY zhangxd ON 20191013 END
            // 断开连接
            ftpClient.disconnect();

            log.info("FTP上传文件[{}]正常结束,结束时间：{}", fileName, DateTimeUtil.getTimeformat());

            long endTime = System.currentTimeMillis();
            log.info("FTP上传文件共耗时{}毫秒!", endTime - startTime);

            return true;
        } catch (Exception ex) {
            log.error("FTP上传文件[{}]异常结束,结束时间为：{}，异常信息为：", fileName, DateTimeUtil.getTimeformat());
            log.error(ex.getMessage());
            return false;
        }
    }

    /**
     * 删除过期的文件.
     */
    public static boolean delFromFtp(JSONObject ftpInfo, String ftpPath, List<String> fileList) {

        log.info("FTP删除文件开始，开始时间：{}", DateTimeUtil.getTimeformat());
        try {
            long startTime = System.currentTimeMillis();

            // 创建FTP客户端
            FTPClient ftpClient = new FTPClient();
            // 连接服务器
            ftpClient.connect(ftpInfo.getString("ftp_ip"));
            log.info("FTP服务器[{}]连接正常.", ftpInfo.getString("ftp_ip"));

            // 登录
            ftpClient.login(ftpInfo.getString("ftp_uname"), ftpInfo.getString("ftp_passwd"));
            log.info("FTP服务器[{}]登录正常.", ftpInfo.getString("ftp_ip"));

            // 获取服务器返回的状态码
            int reply = ftpClient.getReplyCode();
            log.info("FTP服务器返回状态码为[{}]", reply);

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                log.error("FTP服务器[{}]应答失败!", ftpInfo.getString("ftp_ip"));
                return false;
            }

            // 开始删除文件
            for (String fileName : fileList) {
                boolean isDown = ftpClient.deleteFile(ftpPath + File.separator + fileName);
                if (isDown) {
                    log.info("FTP删除文件[{}]成功!", fileName);
                } else {
                    log.info("FTP删除文件[{}]失败!", fileName);
                }
            }

            // ADD BY zhangxd ON 20191013 START
            // COMMENT ON 增加logout动作
            ftpClient.logout();
            // ADD BY zhangxd ON 20191013 END
            // 断开连接
            ftpClient.disconnect();

            log.info("FTP上传文件正常结束,结束时间：{}", DateTimeUtil.getTimeformat());

            long endTime = System.currentTimeMillis();
            log.info("FTP上传文件共耗时{}毫秒!", endTime - startTime);
            return true;
        } catch (Exception ex) {
            log.error("FTP删除文件异常结束,结束时间为：{}，异常信息为：", DateTimeUtil.getTimeformat());
            log.error(ex.getMessage());
            return false;
        }

    }

    /**
     * 压缩文件.
     */
    public static boolean compressZipFile(List<String> fileList, String zipFileName) {

        boolean result = true;
        log.info("压缩文件开始.........................");
        try {
            // 构建压缩文件File
            File zipFile = new File(zipFileName);
            // 初始化ZIP流
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));

            for (String file : fileList) {
                // 获取目标文件
                File inFile = new File(file);
                if (inFile.exists()) {
                    ZipEntry zipEntry = new ZipEntry(inFile.getName());
                    // 赋予ZIP流对象属性
                    zipOutputStream.putNextEntry(zipEntry);

                    int len = 0;
                    byte[] buffer = new byte[1024];
                    // 构建FileInputStream流对象
                    FileInputStream fis = new FileInputStream(inFile);
                    while ((len = fis.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, len);
                        zipOutputStream.flush();
                    }
                    // 关闭
                    fis.close();
                }
            }

            // 关闭
            zipOutputStream.close();

            log.info("压缩文件结束.........................");
            result = true;
        } catch (Exception ex) {
            log.error("压缩文件异常：{}" + ex.getMessage());
            result = false;
        }

        // 返回
        return result;
    }

    public static void main(String[] args) throws Exception {

        JSONObject ftpInfo = new JSONObject();
        ftpInfo.put("ftp_ip", "39.98.190.193");
        ftpInfo.put("ftp_uname", "thesisftp");
        ftpInfo.put("ftp_passwd", "Thesis#Ftp@2019!");

        FileUtils.uploadToFtp(ftpInfo, "//", "F:\\software", "DRW_freeLT_easeus.exe");
    }
}
