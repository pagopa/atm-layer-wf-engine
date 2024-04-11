package it.pagopa.wf.engine.controller;

import it.pagopa.wf.engine.model.VerifyResponse;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "/camunda")
public class CamundaController {

    @PostMapping(value = "/verify/bpmn", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public VerifyResponse verifyBpmn(@RequestParam("file") MultipartFile file) {
        VerifyResponse response = new VerifyResponse();
        try {
            Bpmn.readModelFromStream(file.getInputStream());
            response.setIsVerified(Boolean.TRUE);
            response.setMessage("Corretc Bpmn");
            return response;
        } catch (Exception e) {
            response.setIsVerified(Boolean.FALSE);
            response.setMessage(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            return response;
        }
    }
}
