package com.voiceinput.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class XunfeiService {

    private static final String APP_ID = "7780512";
    private static final String API_KEY = "HPKPOKEY0KKJzupj7Nx0N9Di";
    private static final String API_SECRET = "hr9XWQfoeA6sB2x5uGkGdxcgsemKL7Xg";
    private static final String API_URL = "https://ws.xfyun.cn/v2/recognize";

    private final RestTemplate restTemplate = new RestTemplate();

    public String recognize(byte[] audioData) throws Exception {
        long ts = System.currentTimeMillis() / 1000;
        String signStr = API_KEY + ts;
        String signature = hmacSha1(API_SECRET, signStr);

        String param = Base64.getEncoder().encodeToString(
            "{\"aue\":\"opus\",\"authid\":\"7780512\",\"result_type\":\"complete\",\"version\":\"v2\"}".getBytes(StandardCharsets.UTF_8)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Appid", APP_ID);
        headers.set("X-CurTime", String.valueOf(ts));
        headers.set("X-Param", param);
        headers.set("X-Signature", signature);

        String base64Audio = Base64.getEncoder().encodeToString(audioData);
        String jsonBody = String.format("{\"data\":{\"aue\":\"opus\",\"data\":\"%s\"}}", base64Audio);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }

    private String hmacSha1(String secret, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        mac.init(key);
        byte[] sign = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(sign);
    }
}