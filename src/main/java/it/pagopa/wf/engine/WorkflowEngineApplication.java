package it.pagopa.wf.engine;


import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication
public class WorkflowEngineApplication {
    public static void main(String... args) {
        SpringApplication.run(WorkflowEngineApplication.class, args);
    }
}