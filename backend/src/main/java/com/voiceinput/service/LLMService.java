package com.voiceinput.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;
import java.util.*;

@Service
public class LLMService {

    @Value("${silicon.api.key}")
    private String apiKey;

    @Value("${silicon.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String improveText(String text) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "Qwen/Qwen2.5-7B-Instruct");
            
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", "你是一个文字优化助手。用户输入的是语音识别结果，可能有错字、缺少标点。请直接返回优化后的文字，只需要返回优化后的内容，不要解释。");
            
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", text);
            
            messages.add(systemMsg);
            messages.add(userMsg);
            requestBody.put("messages", messages);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            if (response.getBody() != null && response.getBody().containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) choice.get("message");
                    return (String) message.get("content");
                }
            }
            
            return text;
        } catch (Exception e) {
            return text + " [AI优化失败，原文保留]";
        }
    }
}