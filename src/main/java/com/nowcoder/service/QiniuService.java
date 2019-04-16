package com.nowcoder.service;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.util.ToutiaoUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class QiniuService {
    private static final Logger logger = LoggerFactory.getLogger(QiniuService.class);
    //...生成上传凭证，然后准备上传
    //设置好账号的ACCESS_KEY和SECRET_KEY
    String accessKey = "S_FdXUJAL7pSpDx-NUo8TaYzj2rJbsEPEAiZ8FrL";
    String secretKey = "HjtyyBQEcjrvpqM0zqTo05vcWrNaKD1LzhQDURhf";
    //上传到指定的存储空间
    String bucket = "nowcode";

    private static String QINIU_IMAGE_DOMAIN = "http://pp5tb1vb6.bkt.clouddn.com/";
    UploadManager uploadManager = new UploadManager();
    //密钥配置
    Auth auth = Auth.create(accessKey, secretKey);

    public String saveImage(MultipartFile file) throws IOException {

    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
    String upToken = auth.uploadToken(bucket);

try {
    int dotPos = file.getOriginalFilename().lastIndexOf(".");
    if (dotPos < 0) {
        return null;
    }
    String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
    if (!ToutiaoUtil.isFileAllowed(fileExt)) {
        return null;
    }
    //文件在云上的存储名
    String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
    //调用put方法上传
    /**
     *  file.getBytes():图片的字节流
     *  fileName：随机生成的图片名
     *  upToken：你指定存储的云空间
     */
    Response response = uploadManager.put(file.getBytes(), fileName, upToken);
    //打印返回的信息
    if (response.isOK() && response.isJson()) {

        return QINIU_IMAGE_DOMAIN + JSONObject.parseObject(response.bodyString()).get("key");
    } else {
        logger.error("七牛异常:" + response.bodyString());
        return null;
    }
    } catch (QiniuException e) {
    // 请求失败时打印的异常的信息
    logger.error("七牛异常:" + e.getMessage());
    return null;
        }
    }


}
