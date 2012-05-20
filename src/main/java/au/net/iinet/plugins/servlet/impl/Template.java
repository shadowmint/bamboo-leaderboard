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
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import faux.mvc.Context;
import faux.mvc.extend.ITemplate;
import faux.mvc.internal.Status;

public class Template implements ITemplate {

  private static VelocityEngine _ve = null;
  
  @Override
  public String render(Properties templateProperties, InputStream templateStream, Context context) {
    
    // Create contexts
    context.handler.status().trace("Model: " + templateProperties);
    
    // TemplateContext tcontext = new TemplateContext(templateProperties);
    VelocityContext tcontext = new VelocityContext(templateProperties);
    Reader treader = new InputStreamReader(templateStream);   
    StringWriter writer = new StringWriter();
  
    // Render velocity template
    try {
      ve(context).evaluate(tcontext, writer, "", treader);
    }
    catch(Exception e) {
      context.handler.status().error("Failed to render template: " + e.toString(), Status.ERROR);
    }
    
    return writer.toString();
  }
  
  private VelocityEngine ve(Context c) {
    if (_ve == null) {
      _ve = new VelocityEngine();
      try {
        _ve.init();
      } 
      catch (Exception e) {
        c.handler.status().error("Unable to init velocity engine", e, Status.ERROR); 
      }
    }
    return _ve;
  }
}
