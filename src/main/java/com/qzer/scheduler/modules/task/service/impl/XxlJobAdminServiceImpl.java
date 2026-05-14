package com.qzer.scheduler.modules.task.service.impl;

import com.qzer.scheduler.modules.task.service.XxlJobAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class XxlJobAdminServiceImpl implements XxlJobAdminService {

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Value("${xxl.job.accessToken}")
    private String accessToken;

    private final RestTemplate restTemplate;

    public XxlJobAdminServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public boolean triggerJob(Long jobId, String executorParam) {
        try {
            String url = adminAddresses + "/api/jobinfo/trigger";

            Map<String, Object> params = new HashMap<>();
            params.put("id", jobId);
            params.put("executorParam", executorParam);
            params.put("addressList", "");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (accessToken != null && !accessToken.isEmpty()) {
                headers.set("XXL-JOB-ACCESS-TOKEN", accessToken);
            }

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            if (response.getBody() != null && response.getBody().get("code") != null) {
                int code = ((Number) response.getBody().get("code")).intValue();
                if (code == 200) {
                    log.info("XXL-JOB trigger success, jobId: {}", jobId);
                    return true;
                } else {
                    String msg = (String) response.getBody().get("msg");
                    log.error("XXL-JOB trigger failed, jobId: {}, msg: {}", jobId, msg);
                }
            }
        } catch (Exception e) {
            log.error("XXL-JOB trigger error, jobId: {}, error: {}", jobId, e.getMessage());
        }
        return false;
    }

    @Override
    public boolean startJob(Long jobId) {
        try {
            String url = adminAddresses + "/api/jobinfo/start";

            Map<String, Object> params = new HashMap<>();
            params.put("id", jobId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (accessToken != null && !accessToken.isEmpty()) {
                headers.set("XXL-JOB-ACCESS-TOKEN", accessToken);
            }

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            if (response.getBody() != null && response.getBody().get("code") != null) {
                int code = ((Number) response.getBody().get("code")).intValue();
                if (code == 200) {
                    log.info("XXL-JOB start success, jobId: {}", jobId);
                    return true;
                } else {
                    String msg = (String) response.getBody().get("msg");
                    log.error("XXL-JOB start failed, jobId: {}, msg: {}", jobId, msg);
                }
            }
        } catch (Exception e) {
            log.error("XXL-JOB start error, jobId: {}, error: {}", jobId, e.getMessage());
        }
        return false;
    }

    @Override
    public boolean stopJob(Long jobId) {
        try {
            String url = adminAddresses + "/api/jobinfo/stop";

            Map<String, Object> params = new HashMap<>();
            params.put("id", jobId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (accessToken != null && !accessToken.isEmpty()) {
                headers.set("XXL-JOB-ACCESS-TOKEN", accessToken);
            }

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            if (response.getBody() != null && response.getBody().get("code") != null) {
                int code = ((Number) response.getBody().get("code")).intValue();
                if (code == 200) {
                    log.info("XXL-JOB stop success, jobId: {}", jobId);
                    return true;
                } else {
                    String msg = (String) response.getBody().get("msg");
                    log.error("XXL-JOB stop failed, jobId: {}, msg: {}", jobId, msg);
                }
            }
        } catch (Exception e) {
            log.error("XXL-JOB stop error, jobId: {}, error: {}", jobId, e.getMessage());
        }
        return false;
    }
}
