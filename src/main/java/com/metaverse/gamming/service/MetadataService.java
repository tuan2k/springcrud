package com.metaverse.gamming.service;

import com.amazonaws.services.s3.model.S3Object;
import com.metaverse.gamming.model.FileMeta;
import com.metaverse.gamming.payload.response.ResultApi;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MetadataService {
    public ResultApi upload(MultipartFile[] files, String apiKey, String path) throws IOException;
    public S3Object download(int id);
    public List<FileMeta> list();
}
