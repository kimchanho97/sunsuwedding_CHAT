package com.sunsuwedding.chat.client.internal;

import com.sunsuwedding.chat.common.exception.ChatErrorCode;
import com.sunsuwedding.chat.common.exception.CustomException;
import com.sunsuwedding.chat.dto.message.S3UploadResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ChatImageUploadClient {

    private final RestTemplate restTemplate;

    @Value("${backend.api.base-url}")
    private String baseUrl;

    private static final String CHAT_IMAGE_UPLOAD_PATH = "/internal/chat/image-upload";

    /**
     * 백엔드 API 서버로 multipart 이미지 업로드 요청
     *
     * @param image 업로드할 이미지
     * @return 업로드 결과 (fileName, fileUrl)
     */
    public S3UploadResultDto uploadImage(MultipartFile image) {
        String url = baseUrl + CHAT_IMAGE_UPLOAD_PATH;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Multipart 요청 구성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", convertToResource(image));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<S3UploadResultDto> response = restTemplate.postForEntity(
                    url,
                    requestEntity,
                    S3UploadResultDto.class
            );

            return Objects.requireNonNull(response.getBody());
        } catch (RestClientException e) {
            throw new CustomException(ChatErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    /**
     * MultipartFile → Resource 변환 유틸
     */
    private MultipartInputStreamFileResource convertToResource(MultipartFile file) {
        try {
            return new MultipartInputStreamFileResource(file);
        } catch (IOException e) {
            throw new CustomException(ChatErrorCode.IMAGE_CONVERT_FAILED);
        }
    }

    public static class MultipartInputStreamFileResource extends InputStreamResource {
        private final String filename;

        public MultipartInputStreamFileResource(MultipartFile multipartFile) throws IOException {
            super(multipartFile.getInputStream());
            this.filename = multipartFile.getOriginalFilename();
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() {
            return -1; // 자동 계산 안 되므로 설정 안 함
        }
    }
}
