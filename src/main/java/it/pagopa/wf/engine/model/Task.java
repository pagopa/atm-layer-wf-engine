package it.pagopa.wf.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private String id;
    private Map<String, Object> variables;
    private String form;
    private int priority;
    private boolean isExternal;
}
