package it.pagopa.wf.engine.model;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ParentSpanContext {
    private String traceId;
    private String spanId;
}
