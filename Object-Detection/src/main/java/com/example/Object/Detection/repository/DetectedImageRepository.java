package com.example.Object.Detection.repository;

import com.example.Object.Detection.entity.DetectedImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetectedImageRepository extends JpaRepository<DetectedImageEntity, Integer> {

    List<DetectedImageEntity> findDetectedImageEntitiesByLabel(String label);

}
