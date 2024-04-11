package it.pagopa.wf.engine.model;

import lombok.Data;

@Data
public class VerifyResponse {
    private Boolean isVerified;
    private String message;
}
