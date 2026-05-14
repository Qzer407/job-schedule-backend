package com.qzer.scheduler.modules.alarm.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailAlarmHandler implements AlarmChannelHandler {

    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;

    @Override
    public String getChannelType() {
        return "EMAIL";
    }

    @Override
    public void send(String title, String content, String config) {
        if (!isEnabled(config)) {
            log.warn("邮件告警配置为空，跳过发送");
            return;
        }

        try {
            Map<String, String> configMap = objectMapper.readValue(config, Map.class);
            String to = configMap.get("to");
            String from = configMap.getOrDefault("from", "noreply@scheduler.com");

            if (to == null || to.isEmpty()) {
                log.warn("邮件告警收件人为空");
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to.split(","));
            message.setSubject(title);
            message.setText(content);

            mailSender.send(message);
            log.info("邮件告警发送成功: {}", to);
        } catch (Exception e) {
            log.error("邮件告警发送失败: {}", e.getMessage(), e);
        }
    }
}
