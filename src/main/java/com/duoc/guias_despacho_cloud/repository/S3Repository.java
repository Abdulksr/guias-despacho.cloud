package com.duoc.guias_despacho_cloud.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import com.duoc.guias_despacho_cloud.modelo.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class S3Repository {

    @Autowired
    private AmazonS3 s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private static final Logger log = LoggerFactory.getLogger(S3Repository.class);

    public List<Asset> listObjectsInBucket() {
        List<Asset> items = s3Client.listObjectsV2(bucketName).getObjectSummaries().stream()
                .parallel()
                .map(S3ObjectSummary::getKey)
                .map(this::mapS3ToObject)
                .collect(Collectors.toList());

        log.info("Found " + items.size() + " objects in the bucket " + bucketName);

        return items;
    }

    private Asset mapS3ToObject(String key) {
        return Asset.builder()
                .name(s3Client.getObjectMetadata(bucketName, key).getUserMetaDataOf("name"))
                .key(key)
                .url(s3Client.getUrl(bucketName, key))
                .build();
    }

    public S3ObjectInputStream getObject(String fileName) throws IOException {
        if (!s3Client.doesBucketExistV2(bucketName)) {
            log.error("No Bucket Found");
            return null;
        }
        S3Object s3object = s3Client.getObject(bucketName, fileName);
        return s3object.getObjectContent();
    }

    public byte[] downloadFile(String fileName) throws IOException {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void moveObject(String fileKey, String destinationFileKey) {
        CopyObjectRequest copyRequest = new CopyObjectRequest(bucketName, fileKey, bucketName, destinationFileKey);
        s3Client.copyObject(copyRequest);
        deleteObject(fileKey);
    }

    public void deleteObject(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
    }

    public String uploadFile(String fileName, File fileObj) {
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
        fileObj.delete();
        return "File uploaded : " + fileName;
    }
}
