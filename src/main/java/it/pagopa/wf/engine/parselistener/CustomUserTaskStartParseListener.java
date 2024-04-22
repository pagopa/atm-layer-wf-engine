package it.pagopa.wf.engine.parselistener;

import it.pagopa.wf.engine.client.RedisClient;
import it.pagopa.wf.engine.listener.EndExecutionListener;
import it.pagopa.wf.engine.listener.StartServiceTaskListener;
import it.pagopa.wf.engine.listener.StartUserTaskListener;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.application.impl.event.ProcessApplicationEventParseListener;
import org.camunda.bpm.engine.impl.bpmn.behavior.ExternalTaskActivityBehavior;
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
        taskDefinition.addTaskListener("create",new StartUserTaskListener(redisClient, taskDefinition) );
    }

    @Override
    public void parseServiceTask(Element serviceTaskElement, ScopeImpl scope, ActivityImpl activity) {
        super.parseServiceTask(serviceTaskElement, scope, activity);
        activity.addListener("start", new StartServiceTaskListener(redisClient));
    }

    @Override
    public void parseEndEvent(Element endEventElement, ScopeImpl scope, ActivityImpl activity) {
        super.parseEndEvent(endEventElement, scope, activity);
        activity.addListener("start", new EndExecutionListener(redisClient));
    }
}
