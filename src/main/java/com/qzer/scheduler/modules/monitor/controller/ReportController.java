
package com.qzer.scheduler.modules.monitor.controller;

import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.modules.monitor.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/execution")
    public ApiResponse&lt;Map&lt;String, Object&gt;&gt; getExecutionReport() {
        return ApiResponse.success(reportService.getExecutionReport());
    }
}

