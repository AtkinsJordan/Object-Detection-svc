package com.example.Object.Detection.controller;

import com.example.Object.Detection.dto.ImageDTO;
import com.example.Object.Detection.postman.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    ImageService imageService;

    /**
     * endpoint to get all images from images db
     * @return
     */
    @GetMapping("")
    public ResponseEntity<List<ImageDTO>> getAllImages() {
        List<ImageDTO> images = imageService.getAllImages();
        return new ResponseEntity<>(images, HttpStatus.OK);
    }

    /**
     * Endpoint to get a single image
     * @param id
     * @return Single image if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ImageDTO> getImage(@PathVariable Integer id) {
        try {
            ImageDTO image = imageService.getImage(id);
            return new ResponseEntity<>(image, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to get all images that contain the detected images in the param
     * @param objects -- can be a multiple objects seperated by ,
     * @return all images that contain the detected images in the param
     */
    @GetMapping("/")
    public ResponseEntity<List<ImageDTO>> getImagesByDetectedObjects(@RequestParam String objects) {
        try {
            List<ImageDTO> images = imageService.getImagesByDetectedObjects(objects);
            return new ResponseEntity<>(images, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * endpoint to create a save a new image
     * @param imageDTO
     * @param imageFile
     * @return image and detected images if enabled
     */
    @PostMapping(value = {"/"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ImageDTO> addImage(@RequestPart("imageDTO") ImageDTO imageDTO,
                                             @RequestPart("imageFile")MultipartFile imageFile) throws IOException {
        ImageDTO savedImage = imageService.saveImage(imageDTO, imageFile);
        return new ResponseEntity<>(savedImage,HttpStatus.OK);
    }

}
