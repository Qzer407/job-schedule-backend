package com.qzer.scheduler.modules.alarm.service;

import com.qzer.scheduler.modules.alarm.entity.AlarmConfig;
import com.qzer.scheduler.modules.alarm.entity.AlarmRecord;

import java.util.List;

public interface AlarmService {

    void sendAlarm(Long taskId, String taskName, String alarmType, String alarmContent);

    void sendAlarmWithRule(Long taskId, String taskName, AlarmRule rule);

    List<AlarmRecord> getRecentAlarms(Long taskId, int minutes);

    void mergeAlarms(Long taskId, String taskName);

    List<AlarmConfig> getEnabledChannels();

    AlarmConfig getConfigByChannelType(String channelType);

    void saveConfig(AlarmConfig config);

    void updateConfig(AlarmConfig config);

    void deleteConfig(Long id);

    List<AlarmConfig> listAllConfigs();

    class AlarmRule {
        private boolean enabled = true;
        private String alarmType;
        private String content;
        private int maxPerHour = 10;
        private int suppressionMinutes = 5;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getAlarmType() { return alarmType; }
        public void setAlarmType(String alarmType) { this.alarmType = alarmType; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public int getMaxPerHour() { return maxPerHour; }
        public void setMaxPerHour(int maxPerHour) { this.maxPerHour = maxPerHour; }
        public int getSuppressionMinutes() { return suppressionMinutes; }
        public void setSuppressionMinutes(int suppressionMinutes) { this.suppressionMinutes = suppressionMinutes; }
    }
}
