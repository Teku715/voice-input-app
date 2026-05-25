package com.voiceinput.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import java.util.Base64;
import java.util.Map;

@Service
public class SpeechService {

    private static final String API_KEY = "HPKPOKEY0KKJzupj7Nx0N9Di";
    private static final String SECRET_KEY = "hr9XWQfoeA6sB2x5uGkGdxcgsemKL7Xg";

    private String cachedToken = null;
    private long tokenExpireTime = 0;

    private final RestTemplate restTemplate;

    public SpeechService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        this.restTemplate = new RestTemplate(factory);
    }

    public String recognize(byte[] audioBytes) throws Exception {
        String token = getAccessToken();
        if (token == null) {
            throw new Exception("无法获取百度token");
        }

        String url = "https://vop.baidu.com/server_api?token=" + token + "&cuid=voiceinput&dev_pid=15372";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String base64Audio = Base64.getEncoder().encodeToString(audioBytes);
        int speechLen = audioBytes.length;

        String jsonBody = String.format(
            "{\"speech\":\"%s\",\"len\":%d,\"format\":\"wav\",\"rate\":16000,\"channel\":1,\"dev_pid\":15372,\"cuid\":\"voiceinput\"}",
            base64Audio, speechLen
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

        ResponseEntity<Map> response = restTemplate.getForEntity(tokenUrl, Map.class);
        if (response.getBody() != null && response.getBody().containsKey("access_token")) {
            cachedToken = response.getBody().get("access_token").toString();
            int expiresIn = (int) response.getBody().getOrDefault("expires_in", 0);
            tokenExpireTime = System.currentTimeMillis() + expiresIn * 1000;
            return cachedToken;
        }

        return null;
    }
}