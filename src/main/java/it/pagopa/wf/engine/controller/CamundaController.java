package it.pagopa.wf.engine.controller;

import it.pagopa.wf.engine.model.VerifyResponse;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ParseException;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParse;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParser;
import org.camunda.bpm.engine.impl.cfg.BpmnParseFactory;
import org.camunda.bpm.engine.impl.cfg.DefaultBpmnParseFactory;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.el.Expression;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.el.JuelExpressionManager;
import org.camunda.bpm.engine.impl.form.type.BooleanFormType;
import org.camunda.bpm.engine.impl.form.type.FormTypes;
import org.camunda.bpm.engine.impl.form.type.LongFormType;
import org.camunda.bpm.engine.impl.form.type.StringFormType;
import org.camunda.bpm.engine.impl.interceptor.CommandInterceptor;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentEntity;
import org.camunda.bpm.engine.impl.scripting.ScriptFactory;
import org.camunda.bpm.engine.spring.SpringExpressionManager;
import org.camunda.bpm.engine.test.mock.MockExpressionManager;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaExpressionImpl;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaExpression;
import org.camunda.bpm.spring.boot.starter.spin.SpringBootSpinProcessEnginePlugin;
import org.camunda.bpm.spring.boot.starter.util.SpringBootProcessEnginePlugin;
import org.camunda.spin.SpinFactory;
import org.camunda.spin.impl.SpinFactoryImpl;
import org.camunda.spin.plugin.impl.SpinBpmPlatformPlugin;
import org.camunda.spin.plugin.impl.SpinConfiguration;
import org.camunda.spin.plugin.impl.SpinFallbackSerializerFactory;
import org.camunda.spin.plugin.impl.SpinObjectValueSerializer;
import org.camunda.spin.plugin.impl.SpinProcessEnginePlugin;
import org.camunda.spin.plugin.impl.SpinScriptEnvResolver;
import org.camunda.spin.scripting.SpinScriptEnv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/camunda")
public class CamundaController {

    @PostMapping(value = "/verify/bpmn", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public VerifyResponse verifyBpmn(@RequestParam("file") MultipartFile file) throws IOException {
        return validateFile(file);
    }

    private VerifyResponse validateFile(MultipartFile file) throws IOException {
        VerifyResponse response = new VerifyResponse();

        try (InputStream inputStream = file.getInputStream()) {
            final ExpressionManager testExpressionManager = new JuelExpressionManager();
            ProcessEngineConfigurationImpl processEngineConfiguration = new ProcessEngineConfigurationImpl() {
                @Override
                protected Collection<? extends CommandInterceptor> getDefaultCommandInterceptorsTxRequired() {
                    return null;
                }

                @Override
                protected Collection<? extends CommandInterceptor> getDefaultCommandInterceptorsTxRequiresNew() {
                    return null;
                }

                @Override
                public ExpressionManager getExpressionManager() {
                    return testExpressionManager;
                }

                @Override
                public FormTypes getFormTypes() {
                    final FormTypes formTypes = new FormTypes();
                    formTypes.addFormType(new BooleanFormType());
                    formTypes.addFormType(new StringFormType());
                    formTypes.addFormType(new LongFormType());
                    return formTypes;
                }
            };

            log.info("TEMPORARY -- Setting new ScriptFactory");
            processEngineConfiguration.setScriptFactory(new ScriptFactory());

            List<ProcessEnginePlugin> processEnginePlugins = new ArrayList<>();
            processEnginePlugins.add(new SpinConfiguration());
            processEnginePlugins.add(new SpinProcessEnginePlugin());
            processEnginePlugins.add(new SpringBootSpinProcessEnginePlugin());
            processEngineConfiguration.setProcessEnginePlugins(processEnginePlugins);
            log.info("Set custom plugins: {}",processEngineConfiguration.getProcessEnginePlugins());

            Context.setProcessEngineConfiguration(processEngineConfiguration);

            BpmnParseFactory bpmnParseFactory = new DefaultBpmnParseFactory();
            BpmnParser bpmnParser = new BpmnParser(testExpressionManager, bpmnParseFactory);
            log.info("TEMPORARY -- Creating Parse");
            BpmnParse bpmnParse = bpmnParser.createParse()
                    .sourceInputStream(inputStream)
                    .deployment(new DeploymentEntity())
                    .name(file.getName());
            log.info("TEMPORARY -- Running execute");
            bpmnParse.execute();

            response.setIsVerified(Boolean.TRUE);
            response.setMessage("Corretc Bpmn");
            return response;
        }
        catch (final ParseException exception) {
            response.setIsVerified(Boolean.FALSE);
            response.setMessage(exception.getCause() == null ? exception.getMessage() : exception.getCause().getMessage());
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

