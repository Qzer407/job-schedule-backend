
package com.qzer.scheduler.modules.monitor.controller;

import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.modules.monitor.service.HealthCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
public class HealthCheckController {

    private final HealthCheckService healthCheckService;

    @GetMapping
    public ApiResponse&lt;Map&lt;String, Object&gt;&gt; checkHealth() {
        return ApiResponse.success(healthCheckService.checkHealth());
    }
}

