package com.voiceinput.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Base64;

@Service
public class SpeechService {

    private static final String API_KEY = "HPKPOKEY0KKJzupj7Nx0N9Di";
    private static final String SECRET_KEY = "hr9XWQfoeA6sB2x5uGkGdxcgsemKL7Xg";

    private String cachedToken = null;
    private long tokenExpireTime = 0;
    private final RestTemplate restTemplate;

    public SpeechService() {
        this.restTemplate = new RestTemplate();
    }

    public String recognize(byte[] audioBytes, String format) throws Exception {
        String token = getAccessToken();
        if (token == null) {
            throw new Exception("无法获取token");
        }

        String url = "https://vop.baidu.com/server_api?token=" + token;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String base64Audio = Base64.getEncoder().encodeToString(audioBytes);

        // 百度支持的格式: pcm(16k/8k), wav(16k/8k), aiff, amr, mmf, ogg, m4a, opu
        // webm/opus格式用opu
        String baiduFormat = "ogg".equals(format) ? "ogg" : "opu";

        String jsonBody = String.format(
            "{\"speech\":\"%s\",\"len\":%d,\"format\":\"%s\",\"rate\":16000,\"channel\":1,\"cuid\":\"vi001\"}",
            base64Audio, audioBytes.length, baiduFormat
        );

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }

    private String getAccessToken() throws Exception {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpireTime - 60000) {
            return cachedToken;
        }

        String tokenUrl = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials"
                + "&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY;

        ResponseEntity<String> response = restTemplate.getForEntity(tokenUrl, String.class);
        if (response.getBody() != null && response.getBody().contains("access_token")) {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(response.getBody());
            cachedToken = node.get("access_token").asText();
            tokenExpireTime = System.currentTimeMillis() + node.get("expires_in").asLong() * 1000;
            return cachedToken;
        }
        return null;
    }
}