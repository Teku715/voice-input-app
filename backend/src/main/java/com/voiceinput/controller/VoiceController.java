package com.voiceinput.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.voiceinput.service.LLMService;
import com.voiceinput.service.SpeechService;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class VoiceController {

    @Autowired
    private LLMService llmService;

    @Autowired
    private SpeechService speechService;

    @PostMapping("/improve")
    public Map<String, String> improve(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("result", "请输入文字");
            return error;
        }

        String improved = llmService.improveText(text);

        Map<String, String> result = new HashMap<>();
        result.put("result", improved);
        return result;
    }

    @PostMapping("/recognize")
    public Map<String, String> recognize(@RequestBody Map<String, String> request) {
        String audioBase64 = request.get("audio");
        String format = request.getOrDefault("format", "wav");
        if (audioBase64 == null || audioBase64.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("result", "请录音");
            return error;
        }

        try {
            byte[] audioBytes = Base64.getDecoder().decode(audioBase64);
            String response = speechService.recognize(audioBytes, format);
            // 解析百度返回结果
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map map = mapper.readValue(response, Map.class);
            int errNo = (int) map.getOrDefault("err_no", -1);
            if (errNo == 0) {
                Map resultMap = (Map) map.get("result");
                String text = (String) resultMap.get("recognition_text");
                Map<String, String> result = new HashMap<>();
                result.put("result", text);
                return result;
            } else {
                String errMsg = map.getOrDefault("err_msg", "识别失败").toString();
                Map<String, String> error = new HashMap<>();
                error.put("result", "识别失败: " + errMsg);
                return error;
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("result", "识别失败: " + e.getMessage());
            return error;
        }
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "ok");
        return result;
    }
}