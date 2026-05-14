package com.qzer.scheduler.modules.system.dto;

import lombok.Data;

@Data
public class OAuthClientCreateRequest {
    private String clientId;
    private String clientName;
    private String provider;
    private String clientSecret;
    private String redirectUri;
    private String scope;
    private Integer status;
}
