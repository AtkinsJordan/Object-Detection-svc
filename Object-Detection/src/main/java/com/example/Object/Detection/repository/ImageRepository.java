package com.example.Object.Detection.repository;

import com.example.Object.Detection.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository  extends JpaRepository<ImageEntity, Integer> {
}
