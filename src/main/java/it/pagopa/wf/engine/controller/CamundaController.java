package it.pagopa.wf.engine.controller;

import it.pagopa.wf.engine.model.VerifyResponse;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

@RestController
@RequestMapping(value = "/camunda")
public class CamundaController {

    @PostMapping(value = "/verify/bpmn", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public VerifyResponse verifyBpmn(@RequestParam("file") MultipartFile file) {
        VerifyResponse response = new VerifyResponse();
        try {
            BpmnModelInstance modelInstance =Bpmn.readModelFromStream(file.getInputStream());

//            Process process = modelInstance.getModelElementsByType(Process.class).iterator().next();
//            boolean isProcessValid = validateProcess(process);
//            response.setIsVerified(isProcessValid);
            response.setIsVerified(Boolean.TRUE);
            response.setMessage("Corretc Bpmn");
            return response;
        } catch (Exception e) {
            response.setIsVerified(Boolean.FALSE);
            response.setMessage(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            return response;
        }
    }

    public static VerifyResponse validateProcess(Process process) {
        VerifyResponse response = new VerifyResponse();
        // Controllo della struttura del processo
        VerifyResponse isStructureValid = validateProcessStructure(process);

        // Controllo dei task
        VerifyResponse areTasksValid = validateTasks(process);

        // Controllo degli eventi
        VerifyResponse areEventsValid = validateEvents(process);

        // Controllo dei gateway
        VerifyResponse areGatewaysValid = validateGateways(process);

        // Ritorna true solo se tutti i controlli sono validi
        response.setIsVerified(isStructureValid.getIsVerified()
                && areTasksValid.getIsVerified()
                && areEventsValid.getIsVerified()
                && areGatewaysValid.getIsVerified());
        response.setMessage(isStructureValid.getMessage()
                .concat(areTasksValid.getMessage())
                .concat(areEventsValid.getMessage())
                .concat(areGatewaysValid.getMessage()));
        return response;
    }

    public static VerifyResponse validateProcessStructure(Process process) {
        VerifyResponse response = new VerifyResponse();
        // Esempio di controllo della struttura del processo
        StartEvent startEvent = process.getChildElementsByType(StartEvent.class).iterator().next();
        EndEvent endEvent = process.getChildElementsByType(EndEvent.class).iterator().next();
        // Verifica se il processo ha un inizio e una fine
        boolean isValid = startEvent != null && endEvent != null;
        response.setIsVerified(isValid);
        if (!isValid) {
            response.setMessage("BPMN Process Structure not valid ");
        }
        return response;
    }

        public static VerifyResponse validateTasks(Process process) {
            VerifyResponse response = new VerifyResponse();
            // Esempio di controllo dei task
            Collection<Task> tasks = process.getChildElementsByType(Task.class);

            boolean isValid = !tasks.isEmpty();
            response.setIsVerified(isValid);
            if (!isValid) {
                response.setMessage("BPMN Tasks not valid ");
            }
            return response;
        }

        public static VerifyResponse validateEvents(Process process) {
            VerifyResponse response = new VerifyResponse();
            // Esempio di controllo degli eventi
            Collection<StartEvent> startEvents = process.getChildElementsByType(StartEvent.class);
            Collection<EndEvent> endEvents = process.getChildElementsByType(EndEvent.class);
            // Verifica se ci sono eventi nel processo
            boolean isValid = !(startEvents.isEmpty() && endEvents.isEmpty());
            response.setIsVerified(isValid);
            if (!isValid) {
                response.setMessage("BPMN Events not valid ");
            }
            return response;
        }

        public static VerifyResponse validateGateways(Process process) {
            VerifyResponse response = new VerifyResponse();
            // Esempio di controllo dei gateway
            Collection<Gateway> gateways = process.getChildElementsByType(Gateway.class);
            // Verifica se ci sono gateway nel processo
            boolean isValid = !gateways.isEmpty();
            response.setIsVerified(isValid);
            if (!isValid) {
                response.setMessage("BPMN Gateways not valid ");
            }
            return response;
        }

}
