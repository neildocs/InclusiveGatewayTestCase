/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.unittest;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;

import org.junit.Rule;
import org.junit.Test;

/**
 * @author Dario De Sanctis
 */
public class InclusiveGatewayTestCase {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  @Test
  @Deployment(resources = {"testProcess.bpmn"})
  public void shouldExecuteProcess() {
    // Given we create a new process instance
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("testProcess");
    // Then it should be active
    assertThat(processInstance).isActive();
    // And it should be the only instance
    assertThat(processInstanceQuery().count()).isEqualTo(1);
    // When we complete that task
    complete(task("UserTask_TASK1"));
    complete(task("UserTask_TASK2"));
    complete(task("UserTask_TASK3"), withVariables("type", "2", "var1", "ABC"));
    //that works
    //complete(task("UserTask_TASK3"), withVariables("type", "1", "var1", "ABC", "var2", "ABC"));
    // Then the process instance should be at Task 4
    assertThat(processInstance).isWaitingAt("UserTask_TASK4");
    complete(task("UserTask_TASK4"));
    // Then the process instance should be ended
    assertThat(processInstance).isEnded();
  }
}
