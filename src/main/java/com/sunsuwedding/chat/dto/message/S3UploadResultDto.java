package com.sunsuwedding.chat.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class S3UploadResultDto {

    private String fileName;
    private String fileUrl;
}