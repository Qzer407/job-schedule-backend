package com.qzer.scheduler.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qzer.scheduler.entity.AlarmConfig;
import com.qzer.scheduler.entity.AlarmRecord;
import com.qzer.scheduler.handler.alarm.AlarmChannelHandler;
import com.qzer.scheduler.mapper.AlarmConfigMapper;
import com.qzer.scheduler.mapper.AlarmRecordMapper;
import com.qzer.scheduler.service.impl.AlarmServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {

    @Mock
    private AlarmConfigMapper alarmConfigMapper;

    @Mock
    private AlarmRecordMapper alarmRecordMapper;

    @InjectMocks
    private AlarmServiceImpl alarmService;

    private AlarmConfig emailConfig;

    @BeforeEach
    void setUp() {
        emailConfig = new AlarmConfig();
        emailConfig.setId(1L);
        emailConfig.setChannelType("EMAIL");
        emailConfig.setChannelName("邮件告警");
        emailConfig.setChannelConfig("{\"to\":\"test@example.com\"}");
        emailConfig.setEnabled(true);
        emailConfig.setCreateTime(LocalDateTime.now());
        emailConfig.setUpdateTime(LocalDateTime.now());
    }

    @Test
    void testGetEnabledChannels() {
        List<AlarmConfig> configs = new ArrayList<>();
        configs.add(emailConfig);
        
        when(alarmConfigMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(configs);
        
        List<AlarmConfig> result = alarmService.getEnabledChannels();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("EMAIL", result.get(0).getChannelType());
    }

    @Test
    void testSaveConfig() {
        when(alarmConfigMapper.insert(any(AlarmConfig.class))).thenReturn(1);
        
        AlarmConfig config = new AlarmConfig();
        config.setChannelType("WECHAT");
        config.setChannelName("企业微信");
        config.setChannelConfig("{\"webhookUrl\":\"https://test\"}");
        
        alarmService.saveConfig(config);
        
        verify(alarmConfigMapper, times(1)).insert(any(AlarmConfig.class));
        assertTrue(config.getEnabled());
    }

    @Test
    void testUpdateConfig() {
        when(alarmConfigMapper.updateById(any(AlarmConfig.class))).thenReturn(1);
        
        emailConfig.setChannelName("更新后的邮件告警");
        alarmService.updateConfig(emailConfig);
        
        verify(alarmConfigMapper, times(1)).updateById(any(AlarmConfig.class));
    }

    @Test
    void testDeleteConfig() {
        when(alarmConfigMapper.deleteById(1L)).thenReturn(1);
        
        alarmService.deleteConfig(1L);
        
        verify(alarmConfigMapper, times(1)).deleteById(1L);
    }

    @Test
    void testGetConfigByChannelType() {
        when(alarmConfigMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(emailConfig);
        
        AlarmConfig result = alarmService.getConfigByChannelType("EMAIL");
        
        assertNotNull(result);
        assertEquals("EMAIL", result.getChannelType());
    }

    @Test
    void testListAllConfigs() {
        List<AlarmConfig> configs = new ArrayList<>();
        configs.add(emailConfig);
        
        when(alarmConfigMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(configs);
        
        List<AlarmConfig> result = alarmService.listAllConfigs();
        
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
