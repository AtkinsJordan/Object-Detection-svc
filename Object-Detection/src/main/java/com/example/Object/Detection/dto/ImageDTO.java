package com.example.Object.Detection.dto;

import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
@Data
public class ImageDTO {

    private Integer imageId;
    private String label;
    private Boolean objectDetectionEnabled;
    private List<String> detectedImages;
    private String fileName;
    private String type;
    private Long fileSize;
    //private byte [] fileData;


    public String generateLabel() {
       String label = RandomStringUtils.randomAlphanumeric(8);
       return label;
    }

    public ImageDTO combineDTOAndFileData(ImageDTO imageDTO, MultipartFile imageFile) throws IOException {
        imageDTO.setFileName(imageFile.getOriginalFilename());
        imageDTO.setType(imageFile.getContentType());
        //imageDTO.setFileData(imageFile.getBytes());
        imageDTO.setFileSize(imageFile.getSize());
        return imageDTO;
    }
}
