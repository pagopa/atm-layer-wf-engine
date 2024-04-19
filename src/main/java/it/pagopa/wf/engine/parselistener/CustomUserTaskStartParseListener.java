package it.pagopa.wf.engine.parselistener;

import it.pagopa.wf.engine.client.RedisClient;
import it.pagopa.wf.engine.config.RedisProperty;
import it.pagopa.wf.engine.listener.WaitStateListenerEnd;
import it.pagopa.wf.engine.listener.WaitStateListenerStart;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.application.impl.event.ProcessApplicationEventParseListener;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomUserTaskStartParseListener extends ProcessApplicationEventParseListener {

    @Autowired
    RedisClient redisClient;

    @Override
    public void parseUserTask(Element userTaskElement, ScopeImpl scope, ActivityImpl activity) {
        super.parseUserTask(userTaskElement, scope, activity);
        UserTaskActivityBehavior activityBehavior = (UserTaskActivityBehavior) activity.getActivityBehavior();
        TaskDefinition taskDefinition = activityBehavior.getTaskDefinition();
        activity.addListener("start", new WaitStateListenerStart(redisClient, taskDefinition));
    }

    @Override
    public void parseEndEvent(Element endEventElement, ScopeImpl scope, ActivityImpl activity) {
        super.parseEndEvent(endEventElement, scope, activity);
        activity.addListener("end", new WaitStateListenerEnd(redisClient));
    }
}