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

package au.net.iinet.plugins.servlet.impl;

import java.io.InputStream;

import au.net.iinet.plugins.servlet.LeaderboardServlet;

import faux.mvc.extend.ITemplatePath;
import faux.mvc.Context;

public class TemplateFile implements ITemplatePath {

  @Override
  public InputStream resolvePath(String controllerId, String viewId, String contentType, Context context) {
    LeaderboardServlet s = (LeaderboardServlet) context.servlet;
    String path = resolvePath(controllerId, viewId, contentType);
    InputStream rtn = s.getResource(path);
    if (rtn == null) 
      context.handler.status().trace("Missing template: " + path);
    else 
      context.handler.status().trace("Loaded template: " + path);
    return rtn;
  }
  
  private String resolvePath(String cid, String vid, String type) {
    String rtn = "/" + cid + "/" + vid;
    if (type == "text/html")
      rtn += ".html";
    return rtn; 
  }
}
