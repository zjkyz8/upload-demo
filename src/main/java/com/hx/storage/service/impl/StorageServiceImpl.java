package com.hx.storage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hx.storage.service.StorageService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * @author jinlong
 */
@Slf4j
@Service
public class StorageServiceImpl implements StorageService {
    private final String regionName = "ap-beijing";
    private final String privateBucket = "huixinshen-1300989839";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String uploadPicture(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Authorization", "分配的token");
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<JSONObject> response = restTemplate.exchange("获取临时cred的接口地址", HttpMethod.GET, entity, JSONObject.class);

        String key = generateKey(fileName);

        COSCredentials cred = new BasicCOSCredentials(response.getBody().getJSONObject("credentials").getString("tmpSecretId"), response.getBody().getJSONObject("credentials").getString("tmpSecretKey"));
        ClientConfig clientConfig = new ClientConfig(new Region(regionName));
        COSClient cosclient = new COSClient(cred, clientConfig);

        // 设置 x-cos-security-token header 字段
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setSecurityToken(response.getBody().getJSONObject("credentials").getString("sessionToken"));
        PutObjectRequest putObjectRequest = new PutObjectRequest(privateBucket, key, file.getInputStream(), objectMetadata);

        try {
            PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
            // 成功：putobjectResult 会返回文件的 etag
            String etag = putObjectResult.getETag();
        } catch (CosServiceException e) {
            //失败，抛出 CosServiceException
            e.printStackTrace();
        } catch (CosClientException e) {
            //失败，抛出 CosClientException
            e.printStackTrace();
        }

        // 关闭客户端
        cosclient.shutdown();

        return key;
    }

    private String generateKey(String fileName){
        String pattern = "yyyyMMddHHmmssSSS";
        String timeKey = generateTimeKey(pattern);
        //测试环境必须以此开头，
        return "联系huixinshe确认" +  timeKey + fileName;
        //生产环境必须以此开头
//        return "联系huixinshe确认" +  timeKey + fileName;

    }

    private String generateTimeKey(String pattern){
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(dateTimeFormatter);
    }
}
