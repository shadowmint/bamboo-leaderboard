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

package faux.mvc;

import java.util.Properties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Injector;

import faux.mvc.extend.RequestHandler;
import faux.mvc.internal.Status;

public class Context {
  
  public HttpServletRequest request;
  
  public HttpServletResponse response;
  
  public RequestHandler handler;
  
  public HttpServlet servlet;
  
  public Injector injector;
  
  private Properties _properties;
  
  public Context(HttpServletRequest request, HttpServletResponse response, HttpServlet servlet, RequestHandler handler, Injector injector, Properties props) {
    this.request = request;
    this.response = response;
    this.servlet = servlet;
    this.handler = handler;
    this.injector = injector;
    this._properties = props;
  }
  
  @SuppressWarnings("unchecked")
  public <T> T get(String key) {
    T rtn = null;
    if (_properties == null) 
      handler.status().error("Invalid access to context properties for key '" + key + "': No properties defined", Status.ERROR);
    else {
      try { 
        rtn = (T) _properties.get(key);
      }
      catch(Exception e) {
        handler.status().error("Invalid access to context properties for key '" + key + "'", e, Status.ERROR);
        debugProps(_properties);
      }
    }
    return rtn;
  }
  
  private void debugProps(Properties p) {
    if (p == null) 
      handler.status().trace("Property record was NULL: No properties");
    else {
      String delim = ", ";
      StringBuilder sb = new StringBuilder();
      int count = 0;
      for (Object o : _properties.keySet()) {
          ++count;
          String i = o.toString(); 
          sb.append(i);
          if (count != _properties.keySet().size())
            sb.append(delim);
      }
      handler.status().trace("Found " + count + " property keys.");
      handler.status().trace("Known property keys were: " + sb.toString());
    }
  }
}
