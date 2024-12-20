package it.pagopa.wf.engine.model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskTest {
    @Test
    void testDataAnnotation() {
        Map<String, Object> variables = new HashMap<>();
        Task testTask1 = new Task("id", variables, "form", 1, true);
        Task testTask2 = new Task();
        testTask2.setId("id");
        testTask2.setVariables(variables);
        testTask2.setForm("form");
        testTask2.setPriority(1);
        testTask2.setExternal(true);
        Task testTask3 = Task.builder().id("id").form("form").priority(3).isExternal(false).build();
        assertEquals(testTask1.getId(), testTask2.getId());
        assertEquals(testTask1.getVariables(), testTask2.getVariables());
        assertEquals(testTask1.getForm(), testTask2.getForm());
        assertEquals(testTask1.getPriority(), testTask2.getPriority());
        assertEquals(testTask1.isExternal(),testTask2.isExternal());
        assertEquals(testTask1, testTask2);
        assertTrue(testTask1.canEqual(testTask2));
        assertNotEquals(testTask3.hashCode(), testTask2.hashCode());
        assertEquals("Task(id=id, variables=null, form=form, priority=3, isExternal=false)", testTask3.toString());
    }
}
