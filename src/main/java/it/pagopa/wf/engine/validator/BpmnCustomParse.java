package it.pagopa.wf.engine.validator;

import org.camunda.bpm.engine.ParseException;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParse;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParser;
import org.camunda.bpm.engine.impl.util.ReflectUtil;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.springframework.stereotype.Component;

public class BpmnCustomParse extends BpmnParse {
    public BpmnCustomParse(BpmnParser parser) {
        super(parser);
        expressionManager = parser.getExpressionManager();
        parseListeners = parser.getParseListeners();
        setSchemaResource(ReflectUtil.getResourceUrlAsString(BpmnParser.BPMN_20_SCHEMA_LOCATION));
    }

    @Override
    public void parseProcessDefinitions() {
        for (Element processElement : rootElement.elements("process")) {
            boolean isExecutable = !deployment.isNew();
            String isExecutableStr = processElement.attribute("isExecutable");
            if (isExecutableStr != null) {
                isExecutable = Boolean.parseBoolean(isExecutableStr);
                if (!isExecutable) {
                    throw new ParseException("non-executable process. Set the attribute isExecutable=true to deploy this process.", null, null, null);
                }
            } else {
                throw new ParseException("non-executable process. Set the attribute isExecutable=true to deploy this process.", null, null, null);
            }
            // Only process executable processes
            if (isExecutable) {
                processDefinitions.add(parseProcess(processElement));
            }
        }
    }
}
