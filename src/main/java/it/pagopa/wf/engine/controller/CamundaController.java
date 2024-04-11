package it.pagopa.wf.engine.controller;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/camunda")
public class CamundaController {


    @PostMapping(value = "/verify/bpmn", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Boolean verifyBpmn(@RequestParam("file") MultipartFile file) {
        try {
            Bpmn.readModelFromStream(file.getInputStream());
        } catch (Exception e) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
