package it.pagopa.wf.engine.parselistener;

import it.pagopa.wf.engine.listener.WaitStateListener;
import org.camunda.bpm.application.impl.event.ProcessApplicationEventParseListener;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.springframework.stereotype.Component;

@Component
public class CustomUserTaskStartParseListener extends ProcessApplicationEventParseListener {

    public void parseUserTask(Element userTaskElement, ScopeImpl scope, ActivityImpl activity) {
        super.parseUserTask(userTaskElement, scope, activity);
        activity.addListener("start", new WaitStateListener());
    }
}
