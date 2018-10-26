package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by 蒙卓明 on 2018/10/24
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private static Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String uploadFileName(MultipartFile multipartFile, String path) {

        String originFileName = multipartFile.getOriginalFilename();
        //扩展名
        String fileExtName = originFileName.substring(originFileName.lastIndexOf(".") + 1);

        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtName;
        logger.info("开始上传文件，上传文件的文件名为{}，上传的路径为{}，新文件名为{}", originFileName, path,
                uploadFileName);

        File directory = new File(path);
        if (!directory.exists()) {
            directory.setWritable(true);
            directory.mkdirs();
        }

        File targetFile = new File(path, uploadFileName);
        try {
            multipartFile.transferTo(targetFile);
            //此时文件已上传成功

            //上传文件至FTP服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));

            //上传完成之后，将upload文件夹下面的文件删除
            targetFile.delete();

        } catch (IOException e) {
            logger.error("上传文件异常", e);
            return null;
        }
        return targetFile.getName();
    }

}


