package com.example.Object.Detection.entity;

import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "images")
@NoArgsConstructor
@Data
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "label")
    private String Label;

    @Column(name ="object_detection_enabled")
    private Boolean objectDetectionEnabled;

    @Column(name= "file_name")
    private String fileName;

    private String type;

    @Column(name = "file_size")
    private Long fileSize;

// The data is to large and makes response hard to read
//    @Lob
//    @Column(name = "file_data")
//    private byte [] fileData;

    @OneToMany(targetEntity = DetectedImageEntity.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private List<DetectedImageEntity> detectedImages;

}
