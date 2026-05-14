package com.qzer.scheduler.modules.system.dto;

import lombok.Data;

@Data
public class OAuthClientUpdateRequest {
    private String clientName;
    private String clientSecret;
    private String redirectUri;
    private String scope;
    private Integer status;
}
