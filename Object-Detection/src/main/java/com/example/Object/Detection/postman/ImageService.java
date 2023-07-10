package com.example.Object.Detection.postman;

import com.example.Object.Detection.model.Tag;
import com.example.Object.Detection.repository.DetectedImageRepository;
import com.example.Object.Detection.repository.ImageRepository;
import com.example.Object.Detection.entity.DetectedImageEntity;
import com.example.Object.Detection.dto.ImageDTO;
import com.example.Object.Detection.entity.ImageEntity;
import com.example.Object.Detection.util.ImageDetectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private DetectedImageRepository detectedImageRepository;

    private Logger logger = LoggerFactory.getLogger(ImageService.class);

    private final ImageDetectionUtil imageDetectionUtil = new ImageDetectionUtil();


    /**
     * This method will get all images and their data from the images db
     * @return List of ImageDTO
     */
    public List<ImageDTO> getAllImages() {
        List<ImageDTO> imageDTOS = new ArrayList<>();
        List<ImageEntity> imageEntities = imageRepository.findAll();

        //converts to imageDTO
        for(ImageEntity ie : imageEntities) {
            ImageDTO imageDTO = contvertToImageDTO(ie);
            imageDTOS.add(imageDTO);
        }
        return imageDTOS;
    }

    /**
     * This method will get a single image from the images DB
     * @param id  -- primary key of the image
     * @return ImageDTO
     */
    public ImageDTO getImage(Integer id) {
        ImageEntity imageEntity = imageRepository.findById(id).get();
        return contvertToImageDTO(imageEntity);

    }

    /**
     * This image will search the detected_images db by the label
     *   from a comma seperated string.  It will the collect the image_ids
     *   and search for the images DB for matching images
     *
     * @param objects -- can be multiple objects seperated by ,
     * @return List of ImageDTO
     */
    public List<ImageDTO> getImagesByDetectedObjects(String objects) {

        // formatting query parameter
        objects = objects.replace("\"", "");
        String[] objectsToSearchFor = objects.split(",");

        List<Integer> imageIds = new ArrayList<>();
        List<ImageDTO> matchingImages = new ArrayList<>();
        List<DetectedImageEntity> detectedImageEntities = new ArrayList<>();

        // get list of detectedImageEntities that match label fom detected_images db
        for(String object: objectsToSearchFor) {
            List<DetectedImageEntity> DetectedImageEntityForLabel =
                    detectedImageRepository.findDetectedImageEntitiesByLabel(object);
            detectedImageEntities.addAll(DetectedImageEntityForLabel);

            // Get the image ids from the list
            for(DetectedImageEntity detectedImageEntity: detectedImageEntities){
                imageIds.add(detectedImageEntity.getImageId());
            }
            // remove duplicate entries
            imageIds = imageIds.stream()
                    .distinct()
                    .collect(Collectors.toList());
        }

        if(imageIds.isEmpty()){
            throw new NoSuchElementException("No detected images match query parameter");
        }

        // Get the image with matching id and add to list
        for(Integer id: imageIds) {
            ImageDTO imageDTO = getImage(id);
            matchingImages.add(imageDTO);
        }
        return matchingImages;
    }

    /**
     * This method is used to create a new image in the images DB. If object detection is
     * enabled the image will be scanned for objects. If objects are found they will be saved
     * in detected_images db
     *
     * @param imageDTO  the new Image to be saved
     * @param imageFile
     * @return ImageDTO with images detected and metadata
     */
    public ImageDTO saveImage (ImageDTO imageDTO, MultipartFile imageFile) throws IOException {
        try {
            imageDTO = imageDTO.combineDTOAndFileData(imageDTO, imageFile);
        } catch (IOException e) {
            logger.error("Unable to convert image bytes");
        }

        ImageEntity imageEntity;
        List<String> detectedImageLabel;
        List<Tag> detectedImages = new ArrayList<>();

        // if no label was provided create one
        if(imageDTO.getLabel() == null){
            imageDTO.setLabel(imageDTO.generateLabel());
        }

        // Check if object detection is enabled and scan the image if enabled
        if(imageDTO.getObjectDetectionEnabled()) {
            logger.info("object detection enabled scanning image");

            //In order to send the file to imagga I had to save the MultipartFile to the project
            Path savedLocalImage = createFile(imageFile);
            System.out.println("Saved path for image: " + savedLocalImage);
            String imaggaResponse = imageDetectionUtil.postImaggaByPath(savedLocalImage);
            detectedImages = imageDetectionUtil.getTagsFromImaggaResponse(imaggaResponse);

            // Here were are only accepting detected images with a confidence of 75 or higher
            detectedImages = imageDetectionUtil.getHighConfidenceTags(detectedImages);

            // Delete file locally after imagga has been called
            // I was not able to get the delete working correctly
            //Files.deleteIfExists(savedLocalImage);

            // detection is enabled but no images are found
            if(detectedImages.isEmpty()){
                logger.info("No Images were detected with high confidence");
                imageEntity = convertToImageEntity(imageDTO);
                imageRepository.save(imageEntity);
            } else {
                // images were detected saving image entity to get the imageId created
                imageEntity = convertToImageEntity(imageDTO);
                ImageEntity savedImageEntity = imageRepository.save(imageEntity);
                Integer imageId = savedImageEntity.getId();

                // saving detected images to detected_images db
                for(Tag t: detectedImages) {
                    DetectedImageEntity detectedImageEntity = new DetectedImageEntity();
                    detectedImageEntity.setLabel(t.getTag().getEn());
                    detectedImageEntity.setImageId(imageId);
                    detectedImageRepository.save(detectedImageEntity);
                }
            }
        } else {
            // object detection not enabled saving object to db
            logger.info("object detection not enabled");
            imageEntity = convertToImageEntity(imageDTO);
            imageRepository.save(imageEntity);
        }
        // convert image entity to DTO and set detected images
        imageDTO = contvertToImageDTO(imageEntity);
        detectedImageLabel = imageDetectionUtil.convertToStringLabel(detectedImages);
        imageDTO.setDetectedImages(detectedImageLabel);

        return imageDTO;
    }


    /**
     * Maps an ImageEntity to an ImageDTO
     * @param imageEntity
     * @return ImageDTO
     */
    private ImageDTO contvertToImageDTO(ImageEntity imageEntity){
        ImageDTO imageDTO = new ImageDTO();

        imageDTO.setImageId(imageEntity.getId());
        imageDTO.setLabel(imageEntity.getLabel());
        imageDTO.setObjectDetectionEnabled(imageEntity.getObjectDetectionEnabled());
        //imageDTO.setFileData(imageEntity.getFileData());
        imageDTO.setFileName(imageEntity.getFileName());
        imageDTO.setType(imageEntity.getType());

        List<DetectedImageEntity> detectedImageList = imageEntity.getDetectedImages();

        // avoids NPE when converting entity that does not have detect images enabled
        if (detectedImageList != null){
            List<String> detectedImageLabelList = convertToListOfString(detectedImageList);
            imageDTO.setDetectedImages(detectedImageLabelList);
        }
        return imageDTO;
    }

    /**
     * Maps an ImageDTO to an ImageEntity in order to save image
     * @param imageDTO
     * @return ImageEntity
     */
    private ImageEntity convertToImageEntity(ImageDTO imageDTO) {
        ImageEntity imageEntity = new ImageEntity();

        imageEntity.setId(imageDTO.getImageId());
        imageEntity.setLabel(imageDTO.getLabel());
        imageEntity.setObjectDetectionEnabled(imageDTO.getObjectDetectionEnabled());
        imageEntity.setFileName(imageDTO.getFileName());
        imageEntity.setType(imageDTO.getType());
        //imageEntity.setFileData(imageDTO.getFileData());
        return imageEntity;
    }

    /**
     * Creates a list of strings from the labels of DetectedImageEntity
     * @param detectedImageList
     * @return
     */
    private List<String> convertToListOfString(List<DetectedImageEntity> detectedImageList) {
        List<String> detectedImageLabelList = new ArrayList<>();
        for(DetectedImageEntity detectedImageEntity : detectedImageList) {
            String label = detectedImageEntity.getLabel();
            detectedImageLabelList.add(label);
        }
        return detectedImageLabelList;
    }

    private Path createFile(MultipartFile multipartFile) throws IOException {
        try {

            String localPath = "Object-Detection\\src\\main\\resources\\images\\";
            byte[] data = multipartFile.getBytes();
            Path path = Paths.get(localPath + multipartFile.getOriginalFilename());
             return Files.write(path, data);

        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }
}

