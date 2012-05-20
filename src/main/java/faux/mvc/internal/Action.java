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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import faux.mvc.Context;
import faux.mvc.Controller;
import faux.mvc.View;

/** When invoked, runs a controller instance, renders the result and returns it. */
public class Action {
  
  private Controller _controller;
  
  private Method _method;
  
  private Context _context;
  
  private View _view;
  
  public Action(Controller controller, Method method, Context context) {
    _controller = controller;
    _method = method;
    _context = context;
  }
  
  public String contentType() {
    if (_view != null)
      return _view.contentType();
    else
      return "text/html";
  }
  
  public boolean binary() {
    if (_view != null) 
      return _view.raw();
    else
      return false;
  }
  
  public byte[] invoke() {
    byte[] rtn = null;
    try {
      _view = null;
      try {
        _view = (View) _method.invoke(_controller);
      }
      catch (InvocationTargetException x) {
        Throwable e = x.getCause();
        _context.handler.status().error("Error invoking action", e, Status.ERROR);
      }
      catch(Exception e) {
        _context.handler.status().error("Unable to invoke view action", e, Status.ERROR);
      }
      if (_view != null) 
        rtn = _view.render();
      else 
        _context.handler.status().error("Action did not return a view", Status.ERROR);
    }
    catch(Exception e) {
      _context.handler.status().error("Error rendering action", e, Status.ERROR);
    }
    return rtn;
  }
}
