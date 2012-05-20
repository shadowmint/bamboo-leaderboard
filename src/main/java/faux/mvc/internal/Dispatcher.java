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

package faux.mvc.internal;

import java.lang.reflect.Method;
import java.util.HashMap;

import faux.mvc.Context;
import faux.mvc.Controller;
import faux.mvc.Provider;

/** Take a request url and return an action */
public class Dispatcher {

  /** Set of controllers we've created so far */
  private HashMap<String,Controller> _controllers = new HashMap<String, Controller>();
  
  /** Context of this request */
  private Context _context;
  
  public Dispatcher(Context context) {
    _context = context;
  }
  
  public Action handleRequest(String requestUri) {
    String path = qualifiedUri(requestUri);
    Action rtn = null;
    
    // Is the path valid given our namespace? 
    // NB. If this is false the servlet is configured wrong.
    if (path == null) 
      _context.handler.status().error("Invalid request: URI does not match application namespace", Status.ERROR);
    
    // Otherwise, get the controller, look for an action to run and run it.
    else {
      String[] parts = path.split("/");
      String cid = (parts.length == 1) ? "home" : parts[1];
      String aid = (parts.length <= 2) ? "index" : parts[2];
      try {
        Controller controller = _controllers.containsKey(cid) ? _controllers.get(cid) : null;
        if (controller == null) {
          Provider provider = _context.injector.getInstance(Provider.class);
          String namespace = _context.handler.controllerNamespace();
          Class<?> ctype = Class.forName(controllerName(cid, namespace));
          controller = (Controller) ctype.getConstructor(Provider.class, Context.class).newInstance(provider, _context);
          _controllers.put(cid, controller);
        }
        Method action = controller.getClass().getMethod(aid);
        rtn = new Action(controller, action, _context);
        _context.handler.status().trace("Action: " + cid + "::" + aid);
      }
      catch(Exception e) {
        _context.handler.status().error("Unable to find controller action " + cid + "::" + aid + " (hint: are you use the applicationNamespace() is valid?)", e, Status.NOT_FOUND);
      }
    }
    return rtn;
  }
  
  /** Remove the application namespace, if any */
  private String qualifiedUri(String requestUri) {
    
    String appNamespace = _context.handler.applicationNamespace();
    String rtn = requestUri;
    
    // Remove namespace
    if (appNamespace != "") {
      if (requestUri.startsWith(appNamespace)) {
        rtn = requestUri.replaceFirst(appNamespace, "");
      }
    }
    
    // Remove trailing slash
    if (rtn.endsWith("/")) 
      rtn = rtn.substring(0, rtn.length() - 1);
    
    return rtn;
  }
  
  /** 
   * Return a fully qualified controller namespace.
   * @param cid The name of the controller.
   * @param namespace The namespace in the form 'blah.blah'
   */
  private String controllerName(String cid, String namespace) {
    String rtn = namespace + "." + cid.substring(0, 1).toUpperCase() + cid.substring(1).toLowerCase() + "Controller";
    return rtn;
  }
}
