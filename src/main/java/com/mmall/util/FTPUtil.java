package com.mmall.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * FTP上传工具类
 *
 * Created by 蒙卓明 on 2018/10/24
 */
public class FTPUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip", "192.168.116.134");
    private static String ftpUsername = PropertiesUtil.getProperty("ftp.username", "ftpuser");
    private static String ftpPassword = PropertiesUtil.getProperty("ftp.password", "123456");

    private String ip;
    private int port;
    private String username;
    private String password;
    private FTPClient ftpClient;

    public FTPUtil(String ip, int port, String username, String password) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp, 21, ftpUsername, ftpPassword);
        LOGGER.info("开始连接FTP服务器");
        boolean result = ftpUtil.uploadFile("img", fileList);
        LOGGER.info("上传结束，上传结果为{}", result);
        return result;
    }

    private boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;
        if (!connectFTPServer(this.ip, this.port, this.username, this.password)) {
            uploaded = false;
            return uploaded;
        }

        try {
            ftpClient.changeWorkingDirectory(remotePath);
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("UTF-8");
            //设置成二进制文件，以防乱码
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            //被动模式
            ftpClient.enterLocalActiveMode();
            //开始上传FTP服务器
            for (File file : fileList) {
                fis = new FileInputStream(file);
                ftpClient.storeFile(file.getName(), fis);
            }
        } catch (IOException e) {
            LOGGER.error("上传文件异常", e);
            uploaded = false;
        } finally {
            if (fis != null) {
                fis.close();
            }
            ftpClient.disconnect();
        }

        return uploaded;
    }

    /**
     * 连接并登陆FTP服务器
     * @param ip IP
     * @param port 端口
     * @param username FTP用户名
     * @param password 密码
     * @return
     */
    private boolean connectFTPServer(String ip, int port, String username, String password) {
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip, port);
            isSuccess = ftpClient.login(username, password);
        } catch (IOException e) {
            LOGGER.error("连接FTP服务器异常", e);
        }
        return isSuccess;
    }

}
