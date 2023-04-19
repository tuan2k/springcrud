package com.metaverse.gamming.service;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.metaverse.gamming.controller.DashboardController;
import com.metaverse.gamming.model.FileMeta;
import com.metaverse.gamming.payload.response.ResultApi;
import com.metaverse.gamming.repository.FileMetaRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class MetadataServiceImpl implements MetadataService {

    @Autowired
    private AmazonS3Service amazonS3Service;

    @Autowired
    private FileMetaRepository fileMetaRepository;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${secret.key}")
    private String key;

    @Value("${aws.s3.url}")
    private String url;

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataServiceImpl.class);

    @Override
    public ResultApi upload(MultipartFile[] files, String apiKey, String path) throws IOException {
        ResultApi resp = new ResultApi();
        if (files.length == 0) {
            resp.setStatusCode(400);
            resp.setMessage("Cannot upload empty file!");
            return resp;
        }
        if (!apiKey.equals(key)) {
            resp.setStatusCode(403);
            resp.setMessage("You have not permission to do it!");
            return resp;
        }
        for (MultipartFile file : files) {
            StringBuffer _path = new StringBuffer();
            if (!path.equals("")) {
                _path.append(bucketName).append("/").append(path);
            } else {
                _path.append(bucketName);
            }
            Map<String, String> metadata = new HashMap<>();
            metadata.put("Content-Type", file.getContentType());
            metadata.put("Content-Length", String.valueOf(file.getSize()));
            LOGGER.info("log info upload file");
            String fileName = String.format("%s", file.getOriginalFilename());
            // Uploading file to s3
            PutObjectResult putObjectResult = amazonS3Service.upload(
                    _path.toString(), fileName, Optional.of(metadata), file.getInputStream());
            LOGGER.info(String.valueOf(putObjectResult));
            StringBuffer sp = new StringBuffer();
            sp.append(url).append("/").append(_path).append("/").append(fileName);
            // Saving metadata to db
            LOGGER.info("save to DB");
            fileMetaRepository.save(new FileMeta(fileName,sp.toString() , putObjectResult.getMetadata().getVersionId()));
        }
        resp.setStatusCode(200);
        resp.setMessage("Upload file successfully!!!");
        return resp;
    }

    @Override
    public S3Object download(int id) {
        FileMeta fileMeta = fileMetaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        return amazonS3Service.download(fileMeta.getFilePath(),fileMeta.getFileName());
    }

    @Override
    public List<FileMeta> list() {
        List<FileMeta> metas = new ArrayList<>();
        fileMetaRepository.findAll().forEach(metas::add);
        return metas;
    }
}
