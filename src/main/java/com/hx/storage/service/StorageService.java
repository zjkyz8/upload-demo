package com.hx.storage.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


/**
 * @author landouzi
 */
public interface StorageService {
    String uploadPicture(MultipartFile file) throws IOException;

}
