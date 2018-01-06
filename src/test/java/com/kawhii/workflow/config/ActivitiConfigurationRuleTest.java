
package com.kawhii.workflow.config;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.ActivitiTestCase;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Carl
 * @date 2018/1/4
 * @since 1.0.0
 */
public class ActivitiConfigurationRuleTest extends ActivitiTestCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivitiConfigurationRuleTest.class);

  /*
    //可以直接 activitiRule.getRuntimeService();
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();
  */

    @Test
    @Deployment(resources = "processes/onboarding.bpmn20.xml")
    public void startProcessInstanceByKey() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("employeeName", "Kermit");
        variables.put("numberOfDays", new Integer(4));
        variables.put("vacationMotivation", "I'm really tired!");

        runtimeService.startProcessInstanceByKey("vacationRequest", variables);
        assertEquals(1, runtimeService.createProcessInstanceQuery().count());
    }

    @Test
    @Deployment(resources = "processes/onboarding.bpmn20.xml")
    public void completingTasks() {
        startProcessInstanceByKey();
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("management").list();
        for (Task task : tasks) {
            LOGGER.info("Task available: " + task.getName());
        }
        assertTrue(tasks.size() > 0);

        //完成任务
        Task task = tasks.get(0);

        Map<String, Object> taskVariables = new HashMap<>();
        taskVariables.put("vacationApproved", "false");
        taskVariables.put("managerMotivation", "We have a tight deadline!");
        taskService.complete(task.getId(), taskVariables);
    }
}