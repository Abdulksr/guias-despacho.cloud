package com.duoc.guias_despacho_cloud.controller;

import com.duoc.guias_despacho_cloud.modelo.Asset;
import com.duoc.guias_despacho_cloud.service.AwsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/s3")
public class AwsController {

    private AwsService awsService;

    @Autowired
    public AwsController(AwsService awsService) {
        this.awsService = awsService;
    }

    @GetMapping("/getS3FileContent")
    public ResponseEntity<String> getS3FileContent(
            @RequestParam("fileName") String fileName) throws IOException {
        return new ResponseEntity<>(awsService.getS3FileContent(fileName), HttpStatus.OK);
    }

    @GetMapping("/listS3Files")
    public ResponseEntity<List<Asset>> getS3Files() throws IOException {
        List<Asset> list = new ArrayList<>();
        HttpStatus status = HttpStatus.OK;
        try {
            list = awsService.getS3Files();
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(list, status);
    }

    @GetMapping("/downloadS3File")
    public ResponseEntity<ByteArrayResource> downloadS3File(
            @RequestParam("filePath") String filePath,
            @RequestParam("fileName") String fileName) throws IOException {
        byte[] data = awsService.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @DeleteMapping("/deleteObject")
    public ResponseEntity<String> deleteObject(
            @RequestParam("fileName") String fileName) throws IOException {
        awsService.deleteObject(fileName);
        return new ResponseEntity<>("File deleted", HttpStatus.OK);
    }

    @GetMapping("/moveFile")
    public ResponseEntity<String> moveFile(
            @RequestParam("fileName") String fileKey,
            @RequestParam("fileNameDest") String fileKeyDest) throws IOException {
        awsService.moveObject(fileKey, fileKeyDest);
        return new ResponseEntity<>("File moved", HttpStatus.OK);
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<String> uploadFile(
            @RequestParam("filePath") String filePath,
            @RequestParam("file") MultipartFile file) throws IOException {
        return new ResponseEntity<>(awsService.uploadFile(filePath, file), HttpStatus.OK);
    }

}
