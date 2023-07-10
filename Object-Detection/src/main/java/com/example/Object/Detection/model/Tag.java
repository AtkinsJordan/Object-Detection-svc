package com.example.Object.Detection.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Tag {
    public double confidence;
    public Tag2 tag;
}
