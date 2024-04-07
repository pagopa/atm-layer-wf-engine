package it.pagopa.wf.engine.parselistener;

import it.pagopa.wf.engine.listener.WaitStateListener;
import org.camunda.bpm.application.impl.event.ProcessApplicationEventParseListener;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.util.xml.Element;

public class CustomUserTaskStartParseListener extends ProcessApplicationEventParseListener {

    private final String notificationWaitStatusCallbackBasePath;

    public CustomUserTaskStartParseListener(String notificationWaitStatusCallbackBasePath) {
        this.notificationWaitStatusCallbackBasePath = notificationWaitStatusCallbackBasePath;
    }

    @Override
    public void parseUserTask(Element userTaskElement, ScopeImpl scope, ActivityImpl activity) {
        super.parseUserTask(userTaskElement, scope, activity);
        activity.addListener("start", new WaitStateListener(notificationWaitStatusCallbackBasePath));
    }

    @Override
    public void parseEndEvent(Element endEventElement, ScopeImpl scope, ActivityImpl activity) {
        super.parseEndEvent(endEventElement, scope, activity);
        activity.addListener("start", new WaitStateListener(notificationWaitStatusCallbackBasePath));
    }
}
