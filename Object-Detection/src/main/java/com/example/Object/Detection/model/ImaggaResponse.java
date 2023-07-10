package com.example.Object.Detection.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImaggaResponse {
    public Result result;
    public Status status;
}
