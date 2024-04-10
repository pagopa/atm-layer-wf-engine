package it.pagopa.wf.engine.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping(value = "/camunda")
public class CamundaController {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/verify/bpmn")
    public Boolean verifyBpmn(@FormParam("file") File file) {
        try {
            Bpmn.readModelFromFile(file);
        } catch (Exception e) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
