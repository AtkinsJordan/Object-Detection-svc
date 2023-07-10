package com.example.Object.Detection.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class Result {
    public ArrayList<Tag> tags;
}
