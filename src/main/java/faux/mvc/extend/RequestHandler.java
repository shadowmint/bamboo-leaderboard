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

package faux.mvc.extend;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import faux.mvc.Context;
import faux.mvc.internal.Action;
import faux.mvc.internal.Dispatcher;
import faux.mvc.internal.Status;

/** Handles incoming servlet requests; extend this in the servlet */
public abstract class RequestHandler {
  
  private Injector _injector = null;
  
  /** Public global logging api */
  private Status _status = null;
  
  /** Properties attached from the top level context */
  private Properties _properties = null;
  
  /** If we're in debug mode. */
  private boolean _debug = false;
  
  public RequestHandler(Properties properties) {
    _injector = Guice.createInjector(configure());
    _properties = properties;
  }
  
  public void handleRequest(HttpServletRequest request, HttpServletResponse response, HttpServlet servlet) {
    
    // Reset status on new request.
    _status = null; 
    
    // If the top level context specified properties, pass them to the context
    Context c = new Context(request, response, servlet, this, _injector, _properties);
    
    // Process request
    Dispatcher d = new Dispatcher(c);
    Action a = d.handleRequest(request.getRequestURI());
    Status s = status();
    
    // An error?
    if (s.failed()) 
      error(response, s);
      
    // No error... probably
    else {
      try {
        byte[] result = a.invoke();
        
        // Actuall that is an error
        if (result == null) 
          error(response, s);
        else
          response.getOutputStream().write(result);
        
        // No debugging for raw types
        if ((!a.binary()) && _debug) {
          String debug = status().debug();
          response.getOutputStream().write(debug.getBytes());
        }
      }
      catch(IOException e) {
      }
    }
  }
  
  private void error(HttpServletResponse response, Status status) {
    try {
      if (_debug) {
        response.setContentType("text/html");
        response.getWriter().write("Error code " + status.code());
        response.getWriter().write(status.debug());
      }
      else
        response.sendError(status.code());
    }
    catch(IOException e) {
    }
  }
  
  /** Logging api */
  public Status status() {
    if (_status == null) 
      _status = _injector.getInstance(Status.class);
    return _status;
  }
  
  /** Enable debugging */
  public void debug() {
    _debug = true;
  }
  
  /** Configure any services that must be resolved here. */
  public abstract AbstractModule configure();
  
  /** Return the namespace for controllers here */
  public abstract String controllerNamespace();
  
  /** 
   * Return the global leader space for the app
   * <p>
   * ie. If this servlet lives under /home/myservlet/ and the path
   * /home/myservlet/home/blah should map to the action blah() on
   * the HomeController class, then pass "/home/myservlet" as the
   * response to this. Otherwise return "".
   */
  public abstract String applicationNamespace();
}
