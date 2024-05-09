package it.pagopa.wf.engine.service;

import it.pagopa.wf.engine.model.VerifyResponse;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParse;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParser;
import org.camunda.bpm.engine.impl.cfg.BpmnParseFactory;
import org.camunda.bpm.engine.impl.cfg.DefaultBpmnParseFactory;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.el.JuelExpressionManager;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentEntity;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.camunda.bpm.engine.impl.bpmn.parser.BpmnParse.CAMUNDA_BPMN_EXTENSIONS_NS;

@Service
public class CamundaService {
    @Autowired
    ProcessEngineConfigurationImpl processEngineConfiguration;

    public VerifyResponse validateFile(MultipartFile file) throws IOException {
        VerifyResponse response = new VerifyResponse();
        try (InputStream inputStream = file.getInputStream()) {
            final ExpressionManager testExpressionManager = new JuelExpressionManager();
            Context.setProcessEngineConfiguration(processEngineConfiguration);
            BpmnParseFactory bpmnParseFactory = new DefaultBpmnParseFactory();
            BpmnParser bpmnParser = new BpmnParser(testExpressionManager, bpmnParseFactory);
            BpmnParse bpmnParse = bpmnParser.createParse()
                    .sourceInputStream(inputStream)
                    .deployment(new DeploymentEntity())
                    .name(file.getName());

            bpmnParse.execute();
            checkExecutable(bpmnParse.getRootElement());
            response.setIsVerified(Boolean.TRUE);
            response.setMessage("Correct Bpmn");
            return response;
        } catch (Exception exception) {
            response.setIsVerified(Boolean.FALSE);
            response.setMessage(exception.getCause() == null ? exception.getMessage() : exception.getCause().getMessage());
            return response;
        }
    }

    private void checkExecutable(Element rootElement) {
        for (Element processElement : rootElement.elements("process")) {
            String isExecutableStr = processElement.attribute("isExecutable");
            if (isExecutableStr != null) {
                boolean isExecutable = Boolean.parseBoolean(isExecutableStr);
                if (!isExecutable) {
                    throw new UnsupportedOperationException("non-executable process. Set the attribute isExecutable=true to deploy this process.");
                }
            } else {
                throw new UnsupportedOperationException("non-executable process. Set the attribute isExecutable=true to deploy this process.");
            }
            String historyTimeToLiveString = processElement.attributeNS(CAMUNDA_BPMN_EXTENSIONS_NS, "historyTimeToLive");
            if (historyTimeToLiveString == null || historyTimeToLiveString.isEmpty()){
                throw new UnsupportedOperationException("non-executable process. History Time To Live cannot be null.");
            }
        }
    }

}
