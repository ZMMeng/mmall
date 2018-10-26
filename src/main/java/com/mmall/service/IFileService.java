package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by 蒙卓明 on 2018/10/24
 */
public interface IFileService {

    String uploadFileName(MultipartFile multipartFile, String path);
}
