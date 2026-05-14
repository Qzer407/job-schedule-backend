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
public class DingTalkAlarmHandler implements AlarmChannelHandler {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public String getChannelType() {
        return "DINGTALK";
    }

    @Override
    public void send(String title, String content, String config) {
        if (!isEnabled(config)) {
            log.warn("钉钉告警配置为空，跳过发送");
            return;
        }

        try {
            Map<String, String> configMap = objectMapper.readValue(config, Map.class);
            String webhookUrl = configMap.get("webhookUrl");
            String secret = configMap.get("secret");

            if (webhookUrl == null || webhookUrl.isEmpty()) {
                log.warn("钉钉webhook地址为空");
                return;
            }

            if (secret != null && !secret.isEmpty()) {
                webhookUrl = addSign(webhookUrl, secret);
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

            log.info("钉钉告警发送成功");
        } catch (Exception e) {
            log.error("钉钉告警发送失败: {}", e.getMessage(), e);
        }
    }

    private String addSign(String webhookUrl, String secret) {
        try {
            long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + secret;
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signBytes = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = java.util.Base64.getEncoder().encodeToString(signBytes);
            String signEncoded = java.net.URLEncoder.encode(sign, "UTF-8");
            return webhookUrl + "&timestamp=" + timestamp + "&sign=" + signEncoded;
        } catch (Exception e) {
            log.error("钉钉签名生成失败: {}", e.getMessage());
            return webhookUrl;
        }
    }
}
