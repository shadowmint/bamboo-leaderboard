/*
 * Copyright 2011 iiNet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.net.iinet.plugins.build;

import au.net.iinet.plugins.servlet.model.UserRepo;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bamboo.build.Job;
import com.atlassian.bamboo.chains.StageExecution;
import com.atlassian.bamboo.chains.plugins.PostJobAction;
import com.atlassian.bamboo.resultsummary.BuildResultsSummary;
import com.atlassian.bamboo.resultsummary.tests.FilteredTestResults;
import com.atlassian.bamboo.resultsummary.tests.TestCaseResult;
import com.atlassian.bamboo.resultsummary.tests.TestClassResult;

import static com.google.common.base.Preconditions.*;

import com.atlassian.bamboo.build.BuildLoggerManager;
import com.atlassian.bamboo.build.CustomBuildProcessorServer;
import com.atlassian.bamboo.build.fileserver.BuildDirectoryManager;
import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.process.EnvironmentVariableAccessor;
import com.atlassian.bamboo.process.ProcessService;
import com.atlassian.bamboo.v2.build.BaseConfigurablePlugin;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.variable.CustomVariableContext;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import org.apache.log4j.Logger;
import com.atlassian.bamboo.commit.Commit;
import com.google.common.collect.Multimap;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion.User;

import java.util.Map;
import java.util.List;

public class LeaderboardBuildPostAction implements PostJobAction {

  private final ActiveObjects ao;
  
  private UserRepo users;
  
  public LeaderboardBuildPostAction(ActiveObjects ao) {
    this.ao = checkNotNull(ao);
    users = new UserRepo(ao);
  }
  
  @Override
  public void execute(StageExecution arg0, Job arg1, BuildResultsSummary arg2) {
    System.err.println("------------ HELLO WORLD ------------------");
    System.err.println("ao: " + ao);
    System.err.println("Summary: " + arg2.getTestSummary());
    
    FilteredTestResults<TestClassResult> results = arg2.getFilteredTestResults();
    Multimap<TestClassResult, TestCaseResult> failed = results.getAllFailedTests();
    Multimap<TestClassResult, TestCaseResult> new_failed = results.getNewFailedTests();
    Multimap<TestClassResult, TestCaseResult> exist_failed = results.getExistingFailedTests();
    Multimap<TestClassResult, TestCaseResult> fixed = results.getFixedTests();
    System.err.println("Failed tests: " + failed.size());
    
    int count_failed = failed.size();
    int count_new_failed = new_failed.size();
    int count_exist_failed = exist_failed.size();
    int count_fixed = fixed.size();
    
    /** HARD CODED LOGIC HAX */
    
    /** Copied shamelessly from jenkins CI game:
       -10 points for breaking a build
        0 points for breaking a build that already was broken
        +1 points for doing a build with no failures (unstable builds gives no points)
        -1 points for each new test failures
        +1 points for each new test that passes
     */
    
    /** Calculate the score for whoever just commited all that */
    int new_points = 0;
    if (count_new_failed > 0) {
      // Fucker, you broke the build. 
      new_points -= 10;
      System.err.println("Build broke: -10");
    }
    
    // You broke tests? Lose points
    new_points -= count_new_failed;
    if (count_new_failed > 0) 
      System.err.println("New fails: -" + count_new_failed);
    
    // You fixed some tests tho? Get points for that.
    new_points += count_fixed;
    if (count_fixed > 0) 
      System.err.println("Fixed tests: +" + count_fixed);
    
    // +1 for commit with no fails
    if (count_failed == 0) {
      new_points += 1;
      System.err.println("No fails: +1");
    }
    
    for (Commit c : arg2.getCommits()) {
      System.err.println("Commit " + c.getComment() + " by " + c.getAuthor().getLinkedUserName() + " // " + c.getAuthor().getLinkedUserName() + " - id " + c.getChangeSetId());
      String author_id = c.getAuthor().getName();
      String author_name = c.getAuthor().getName();
      
      // TODO: Icon based on point total here
      
      au.net.iinet.plugins.servlet.model.db.User u = users.get(author_id);
      u.setName(author_name);
     
      int total_points = u.getPoints() + new_points;
      u.setPoints(total_points);
      
      users.save(u);
      
      System.err.println("Total update to user '" + author_id + "': " + new_points);
    }
    System.err.println("------------ HELLO WORLD ------------------");
  }
  
  private void trace(String msg) {
    System.err.println(msg);
  }
}
  
