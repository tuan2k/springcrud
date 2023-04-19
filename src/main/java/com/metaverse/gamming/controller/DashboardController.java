package com.metaverse.gamming.controller;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.metaverse.gamming.model.User;
import com.metaverse.gamming.payload.response.ResultApi;
import com.metaverse.gamming.repository.UserRepository;
import com.metaverse.gamming.service.MetadataService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
public class DashboardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);

    private MetadataService metadataService;

    private UserRepository userRepository;

    @GetMapping("dashboard")
    public String dashboard(Model model) {

        var files = metadataService.list();
        model.addAttribute("files", files);
        return "dashboard";
    }

    @GetMapping("/getUser")
    public List<User> getUser(){
        return userRepository.findAll();
    }

    @PostMapping("upload")
    public ResultApi upload(
            @RequestParam("file") MultipartFile[] files,@RequestParam("api-key") String apiKey,@RequestParam("path") String
            path) throws IOException {
        ResultApi result = metadataService.upload(files,apiKey,path);
        return result;
    }

    @GetMapping("download/{id}")
    @ResponseBody
    public HttpEntity<ByteArrayResource> download(Model model, @PathVariable int id, HttpServletResponse response) throws
            IOException {

        S3Object s3Object = metadataService.download(id);
        final String keyName = "demo";
        final S3ObjectInputStream stream = s3Object.getObjectContent();
        try {
            var content = IOUtils.toByteArray(stream);
            LOGGER.info("File downloaded successfully.");
            final byte[] data = content;
            final ByteArrayResource resource = new ByteArrayResource(data);
            s3Object.close();
            return ResponseEntity
                    .ok()
                    .contentLength(data.length)
                    .header("Content-type", "application/octet-stream")
                    .header("Content-disposition", "attachment; filename=\"" + keyName + "\"")
                    .body(resource);
        } catch (final IOException ex) {
            LOGGER.info("IO Error Message= " + ex.getMessage());
        }
        return null;
    }

//        String contentType = s3Object.getObjectMetadata().getContentType();
//        var bytes = s3Object.getObjectContent().readAllBytes();
//        System.out.println(bytes);
//        HttpHeaders header = new HttpHeaders();
//        header.setContentType(MediaType.valueOf(contentType));
//        header.setContentLength(bytes.length);
//
//        return new HttpEntity<byte[]>(bytes, header);
}
