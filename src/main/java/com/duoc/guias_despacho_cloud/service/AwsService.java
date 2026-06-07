package com.duoc.guias_despacho_cloud.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.util.StringUtils;
import com.duoc.guias_despacho_cloud.modelo.Asset;
import com.duoc.guias_despacho_cloud.repository.S3Repository;

@Service
public class AwsService {

    private static final Logger log = LoggerFactory.getLogger(AwsService.class);

    @Autowired
    private S3Repository s3Repository;

    public String getS3FileContent(String fileName) throws IOException {
        return getAsString(s3Repository.getObject(fileName));
    }

    public List<Asset> getS3Files() {
        return s3Repository.listObjectsInBucket();
    }

    public byte[] downloadFile(String fileName) throws IOException {
        return s3Repository.downloadFile(fileName);
    }

    public void moveObject(String fileKey, String destinationFileKey) {
        s3Repository.moveObject(fileKey, destinationFileKey);
    }

    public void deleteObject(String fileName) {
        s3Repository.deleteObject(fileName);
    }

    public String uploadFile(String filePath, MultipartFile file) {
        File fileObj = covertMultiPartFileToFile(file);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        return s3Repository.uploadFile(filePath + fileName, fileObj);
    }

    private static String getAsString(InputStream is) throws IOException {
        if (is == null)
            return "";
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StringUtils.UTF8));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            is.close();
        }
        return sb.toString();
    }

    private File covertMultiPartFileToFile(MultipartFile file) {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e.getMessage());
        }
        return convFile;
    }
}
