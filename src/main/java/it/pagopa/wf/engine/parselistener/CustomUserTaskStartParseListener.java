package it.pagopa.wf.engine.parselistener;

import it.pagopa.wf.engine.listener.WaitStateListener;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.application.impl.event.ProcessApplicationEventParseListener;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.camunda.bpm.engine.impl.util.xml.Element;

@Slf4j
public class CustomUserTaskStartParseListener extends ProcessApplicationEventParseListener {


    @Override
    public void parseUserTask(Element userTaskElement, ScopeImpl scope, ActivityImpl activity) {
        super.parseUserTask(userTaskElement, scope, activity);
        UserTaskActivityBehavior activityBehavior = (UserTaskActivityBehavior)activity.getActivityBehavior();
        TaskDefinition taskDefinition = activityBehavior.getTaskDefinition();
        //aggiungere dei log per vedere tutto il bean taskdefinition
        log.info("TaskDefinition = " + taskDefinition.toString() +
                "\n FormKey = " + (taskDefinition.getFormKey()== null? "is null": taskDefinition.getFormKey().getExpressionText()) +
                "\n Priority = " + (taskDefinition.getPriorityExpression()==null? "is null": taskDefinition.getPriorityExpression().getExpressionText()));
        activity.addListener("start", new WaitStateListener());
    }

    @Override
    public void parseEndEvent(Element endEventElement, ScopeImpl scope, ActivityImpl activity) {
        super.parseEndEvent(endEventElement, scope, activity);
        activity.addListener("start", new WaitStateListener());
    }
}
