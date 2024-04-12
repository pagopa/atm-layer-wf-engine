package it.pagopa.wf.engine.listener;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Notification {
    String currentActivityName;
    String currentActivityId;
    String requestId;
    String processInstanceId;
    String processDefinitionId;
}
