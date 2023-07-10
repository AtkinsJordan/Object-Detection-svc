package com.example.Object.Detection.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "detected_images")
@NoArgsConstructor
@Data
public class DetectedImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String label;
    @Column(name = "image_id")
    private Integer imageId;

}
