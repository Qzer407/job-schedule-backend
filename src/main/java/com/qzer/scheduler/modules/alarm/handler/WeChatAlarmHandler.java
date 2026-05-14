package com.qzer.scheduler.modules.alarm.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeChatAlarmHandler implements AlarmChannelHandler {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public String getChannelType() {
        return "WECHAT";
    }

    @Override
    public void send(String title, String content, String config) {
        if (!isEnabled(config)) {
            log.warn("企业微信告警配置为空，跳过发送");
            return;
        }

        try {
            Map<String, String> configMap = objectMapper.readValue(config, Map.class);
            String webhookUrl = configMap.get("webhookUrl");

            if (webhookUrl == null || webhookUrl.isEmpty()) {
                log.warn("企业微信webhook地址为空");
                return;
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("msgtype", "text");

            Map<String, String> textContent = new HashMap<>();
            textContent.put("content", title + "\n" + content);
            requestBody.put("text", textContent);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            restTemplate.postForEntity(webhookUrl, request, String.class);

            log.info("企业微信告警发送成功");
        } catch (Exception e) {
            log.error("企业微信告警发送失败: {}", e.getMessage(), e);
        }
    }
}
