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

package au.net.iinet.plugins.servlet;

import javax.servlet.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import au.net.iinet.plugins.servlet.consts.AppContext;

import com.atlassian.activeobjects.external.ActiveObjects;

import java.io.IOException;

import java.io.InputStream;
import java.util.Properties;

import faux.mvc.extend.RequestHandler;

import static com.google.common.base.Preconditions.*;

public final class LeaderboardServlet extends HttpServlet{
  
  private static final long serialVersionUID = 1L;
  
  private RequestHandler r;
  
  private Properties p;
  
  private final ActiveObjects ao;
  
  public LeaderboardServlet(ActiveObjects ao) {
    this.ao = checkNotNull(ao);
  }
  
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    handleRequest(req, resp);
  }
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    handleRequest(req, resp);
  }
  
  private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
  {
    // Make the active objects interface public to controllers.
    p = new Properties();
    p.put(AppContext.ACTIVE_OBEJCTS, ao);
    
    r = new LeaderboardRequestHandler(p);
    // r.debug(); // Debugging is on!
    r.handleRequest(req, resp, this);
  }
  
  public InputStream getResource(String path) {
    // r.status().trace(getServletContext().getRealPath("/"));
    InputStream pdInputStream = this.getServletContext().getResourceAsStream(path);
    return pdInputStream;
  }
}

