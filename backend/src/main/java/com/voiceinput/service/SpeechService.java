package com.voiceinput.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.io.ByteArrayOutputStream;

@Service
public class SpeechService {

    private static final String SILICON_API_URL = "https://api.siliconflow.cn/v1/audio/transcriptions";
    private static final String SILICON_API_KEY = "sk-ffzpunutxfeipjuhabhlqhwfngabcqrcqowgxacnjkknttqm";
    private static final String MODEL = "FunAudioLLM/SenseVoiceSmall";

    private final RestTemplate restTemplate = new RestTemplate();

    public String recognize(byte[] audioBytes, String format) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer " + SILICON_API_KEY);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        HttpHeaders fileHeaders = new HttpHeaders();
        MediaType mediaType = "ogg".equals(format) ? MediaType.parseMediaType("audio/ogg") : MediaType.parseMediaType("audio/wav");
        fileHeaders.setContentType(mediaType);
        HttpEntity<byte[]> fileEntity = new HttpEntity<>(audioBytes, fileHeaders);
        body.add("file", fileEntity);
        body.add("model", MODEL);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
            SILICON_API_URL,
            HttpMethod.POST,
            entity,
            String.class
        );

        return response.getBody();
    }
}