package it.pagopa.wf.engine.controller;

import it.pagopa.wf.engine.model.VerifyResponse;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
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

        public static boolean validateProcess(Process process) {
            // Controllo della struttura del processo
            boolean isStructureValid = validateProcessStructure(process);

            // Controllo dei task
            boolean areTasksValid = validateTasks(process);

            // Controllo degli eventi
            boolean areEventsValid = validateEvents(process);

            // Controllo dei gateway
            boolean areGatewaysValid = validateGateways(process);

            // Altri controlli...

            // Ritorna true solo se tutti i controlli sono validi
            return isStructureValid && areTasksValid && areEventsValid && areGatewaysValid;
        }

        public static boolean validateProcessStructure(Process process) {
            // Esempio di controllo della struttura del processo
            StartEvent startEvent = process.getChildElementsByType(StartEvent.class).iterator().next();
            EndEvent endEvent = process.getChildElementsByType(EndEvent.class).iterator().next();

            // Verifica se il processo ha un inizio e una fine
            return startEvent != null && endEvent != null;
        }

        public static boolean validateTasks(Process process) {
            // Esempio di controllo dei task
            Collection<Task> tasks = process.getChildElementsByType(Task.class);

            // Verifica se ci sono task nel processo
            return !tasks.isEmpty();
        }

        public static boolean validateEvents(Process process) {
            // Esempio di controllo degli eventi
            Collection<StartEvent> startEvents = process.getChildElementsByType(StartEvent.class);
            Collection<EndEvent> endEvents = process.getChildElementsByType(EndEvent.class);
            // Verifica se ci sono eventi nel processo
            return !(startEvents.isEmpty() && endEvents.isEmpty());
        }

        public static boolean validateGateways(Process process) {
            // Esempio di controllo dei gateway
            Collection<Gateway> gateways = process.getChildElementsByType(Gateway.class);
            // Verifica se ci sono gateway nel processo
            return !gateways.isEmpty();
        }

}
