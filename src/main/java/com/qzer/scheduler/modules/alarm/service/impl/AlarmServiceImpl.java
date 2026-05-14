package com.qzer.scheduler.modules.alarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qzer.scheduler.common.exception.BusinessException;
import com.qzer.scheduler.modules.alarm.entity.AlarmConfig;
import com.qzer.scheduler.modules.alarm.entity.AlarmRecord;
import com.qzer.scheduler.modules.alarm.handler.AlarmChannelHandler;
import com.qzer.scheduler.modules.alarm.mapper.AlarmConfigMapper;
import com.qzer.scheduler.modules.alarm.mapper.AlarmRecordMapper;
import com.qzer.scheduler.modules.alarm.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {

    private final AlarmConfigMapper alarmConfigMapper;
    private final AlarmRecordMapper alarmRecordMapper;
    private final Map<String, AlarmChannelHandler> alarmHandlers;

    private final Map<String, AlarmSuppression> suppressionMap = new ConcurrentHashMap<>();

    private static final int DEFAULT_SUPPRESSION_MINUTES = 5;
    private static final int MAX_ALARM_CONTENT_LENGTH = 1000;

    @Override
    @Transactional
    public void sendAlarm(Long taskId, String taskName, String alarmType, String alarmContent) {
        String suppressionKey = buildSuppressionKey(taskId, alarmType);

        if (isAlarmSuppressed(suppressionKey)) {
            log.info("告警被抑制: taskId={}, alarmType={}", taskId, alarmType);
            return;
        }

        String title = buildTitle(taskName, alarmType);
        String content = buildContent(taskId, taskName, alarmType, alarmContent);

        List<AlarmConfig> enabledChannels = getEnabledChannels();

        for (AlarmConfig channel : enabledChannels) {
            AlarmChannelHandler handler = alarmHandlers.get(channel.getChannelType());
            if (handler != null) {
                try {
                    handler.send(title, content, channel.getChannelConfig());
                    saveAlarmRecord(taskId, taskName, alarmType, content, channel.getChannelType(), 1, null);
                    log.info("告警发送成功: channel={}, taskId={}", channel.getChannelType(), taskId);
                } catch (Exception e) {
                    log.error("告警发送失败: channel={}, error={}", channel.getChannelType(), e.getMessage());
                    saveAlarmRecord(taskId, taskName, alarmType, content, channel.getChannelType(), 0, e.getMessage());
                }
            }
        }

        updateSuppression(suppressionKey);
    }

    @Override
    public void sendAlarmWithRule(Long taskId, String taskName, AlarmRule rule) {
        if (rule == null || !rule.isEnabled()) {
            return;
        }

        if (rule.getMaxPerHour() > 0) {
            int recentCount = countRecentAlarms(taskId, rule.getAlarmType(), 1);
            if (recentCount >= rule.getMaxPerHour()) {
                log.info("告警频率超限: taskId={}, alarmType={}, maxPerHour={}", taskId, rule.getAlarmType(), rule.getMaxPerHour());
                return;
            }
        }

        sendAlarm(taskId, taskName, rule.getAlarmType(), rule.getContent());
    }

    @Override
    public List<AlarmRecord> getRecentAlarms(Long taskId, int minutes) {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(minutes);
        LambdaQueryWrapper<AlarmRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(taskId != null, AlarmRecord::getTaskId, taskId)
               .ge(AlarmRecord::getCreateTime, startTime)
               .orderByDesc(AlarmRecord::getCreateTime);
        return alarmRecordMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void mergeAlarms(Long taskId, String taskName) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(DEFAULT_SUPPRESSION_MINUTES);
        LambdaQueryWrapper<AlarmRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmRecord::getTaskId, taskId)
               .eq(AlarmRecord::getSendStatus, 0)
               .lt(AlarmRecord::getCreateTime, threshold);

        List<AlarmRecord> pendingAlarms = alarmRecordMapper.selectList(wrapper);

        if (pendingAlarms.size() > 1) {
            log.info("合并告警: taskId={}, count={}", taskId, pendingAlarms.size());

            AlarmRecord merged = new AlarmRecord();
            merged.setTaskId(taskId);
            merged.setTaskName(taskName);
            merged.setAlarmType("MERGED");
            merged.setAlarmLevel("WARN");
            merged.setAlarmTitle("[合并告警] " + taskName);
            merged.setAlarmContent(String.format("合并了 %d 条告警记录", pendingAlarms.size()));
            merged.setSendStatus(1);
            merged.setAlarmTime(LocalDateTime.now());
            merged.setCreateTime(LocalDateTime.now());

            alarmRecordMapper.insert(merged);

            for (AlarmRecord record : pendingAlarms) {
                alarmRecordMapper.deleteById(record.getId());
            }
        }
    }

    @Override
    public List<AlarmConfig> getEnabledChannels() {
        LambdaQueryWrapper<AlarmConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmConfig::getEnabled, true);
        return alarmConfigMapper.selectList(wrapper);
    }

    @Override
    public AlarmConfig getConfigByChannelType(String channelType) {
        LambdaQueryWrapper<AlarmConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmConfig::getChannelType, channelType);
        return alarmConfigMapper.selectOne(wrapper);
    }

    @Override
    @Transactional
    public void saveConfig(AlarmConfig config) {
        if (config.getChannelType() == null || config.getChannelType().isEmpty()) {
            throw new BusinessException("渠道类型不能为空");
        }

        AlarmConfig existing = getConfigByChannelType(config.getChannelType());
        if (existing != null) {
            throw new BusinessException("渠道类型已存在: " + config.getChannelType());
        }

        config.setCreateTime(LocalDateTime.now());
        config.setUpdateTime(LocalDateTime.now());
        if (config.getEnabled() == null) {
            config.setEnabled(true);
        }
        alarmConfigMapper.insert(config);
        log.info("告警配置保存成功: {}", config.getChannelType());
    }

    @Override
    @Transactional
    public void updateConfig(AlarmConfig config) {
        config.setUpdateTime(LocalDateTime.now());
        alarmConfigMapper.updateById(config);
        log.info("告警配置更新成功: {}", config.getId());
    }

    @Override
    @Transactional
    public void deleteConfig(Long id) {
        alarmConfigMapper.deleteById(id);
        log.info("告警配置删除成功: {}", id);
    }

    @Override
    public List<AlarmConfig> listAllConfigs() {
        return alarmConfigMapper.selectList(new LambdaQueryWrapper<AlarmConfig>()
                .orderByDesc(AlarmConfig::getCreateTime));
    }

    private void saveAlarmRecord(Long taskId, String taskName, String alarmType,
                                 String content, String channel, Integer status, String errorMsg) {
        AlarmRecord record = new AlarmRecord();
        record.setTaskId(taskId);
        record.setTaskName(taskName);
        record.setAlarmType(alarmType);
        record.setAlarmLevel("ERROR");
        record.setAlarmTitle(buildTitle(taskName, alarmType));
        record.setAlarmContent(content);
        record.setAlarmChannel(channel);
        record.setAlarmTime(LocalDateTime.now());
        record.setSendStatus(status);
        if (errorMsg != null && errorMsg.length() > MAX_ALARM_CONTENT_LENGTH) {
            errorMsg = errorMsg.substring(0, MAX_ALARM_CONTENT_LENGTH);
        }
        record.setErrorMsg(errorMsg);
        record.setCreateTime(LocalDateTime.now());
        alarmRecordMapper.insert(record);
    }

    private String buildTitle(String taskName, String alarmType) {
        return String.format("[%s] 任务告警 - %s", alarmType, taskName);
    }

    private String buildContent(Long taskId, String taskName, String alarmType, String alarmContent) {
        StringBuilder sb = new StringBuilder();
        sb.append("告警类型: ").append(alarmType).append("\n");
        sb.append("任务名称: ").append(taskName).append("\n");
        sb.append("任务ID: ").append(taskId).append("\n");
        sb.append("告警时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        if (alarmContent != null && !alarmContent.isEmpty()) {
            sb.append("告警详情: ").append(alarmContent);
        }
        return sb.toString();
    }

    private String buildSuppressionKey(Long taskId, String alarmType) {
        return String.format("%d:%s", taskId, alarmType);
    }

    private boolean isAlarmSuppressed(String key) {
        AlarmSuppression suppression = suppressionMap.get(key);
        if (suppression == null) {
            return false;
        }
        return !suppression.isExpired();
    }

    private void updateSuppression(String key) {
        suppressionMap.put(key, new AlarmSuppression());
    }

    private int countRecentAlarms(Long taskId, String alarmType, int hours) {
        LocalDateTime startTime = LocalDateTime.now().minusHours(hours);
        LambdaQueryWrapper<AlarmRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmRecord::getTaskId, taskId)
               .eq(alarmType != null, AlarmRecord::getAlarmType, alarmType)
               .ge(AlarmRecord::getCreateTime, startTime);
        return Math.toIntExact(alarmRecordMapper.selectCount(wrapper));
    }

    private static class AlarmSuppression {
        private final LocalDateTime createTime;
        private final int suppressionMinutes;

        AlarmSuppression() {
            this(DEFAULT_SUPPRESSION_MINUTES);
        }

        AlarmSuppression(int suppressionMinutes) {
            this.createTime = LocalDateTime.now();
            this.suppressionMinutes = suppressionMinutes;
        }

        boolean isExpired() {
            return Duration.between(createTime, LocalDateTime.now()).toMinutes() >= suppressionMinutes;
        }
    }
}
