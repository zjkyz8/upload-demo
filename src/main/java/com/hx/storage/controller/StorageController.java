package com.hx.storage.controller;


import com.hx.storage.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping(value="/upload",produces ={"application/text;charset=UTF-8"})
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadPicture(@RequestParam("file") MultipartFile file) throws IOException {
        return storageService.uploadPicture(file);
    }

}
