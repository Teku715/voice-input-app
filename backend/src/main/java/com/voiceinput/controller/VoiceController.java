package com.voiceinput.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.voiceinput.service.LLMService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class VoiceController {

    @Autowired
    private LLMService llmService;

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

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "ok");
        return result;
    }
}