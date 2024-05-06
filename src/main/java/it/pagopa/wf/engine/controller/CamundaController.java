package it.pagopa.wf.engine.controller;

import it.pagopa.wf.engine.model.VerifyResponse;
import it.pagopa.wf.engine.service.CamundaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "/camunda")
public class CamundaController {
    @Autowired
    CamundaService camundaService;

    @PostMapping(value = "/verify/bpmn", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public VerifyResponse verifyBpmn(@RequestParam("file") MultipartFile file) throws IOException {
        return camundaService.validateFile(file);
    }
}

