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

package au.net.iinet.plugins.servlet.controllers;

import java.util.Properties;

import au.net.iinet.plugins.servlet.actions.HomeActions;

import faux.mvc.Controller;
import faux.mvc.View;
import faux.mvc.Provider;
import faux.mvc.Context;

public class HomeController extends Controller {

  private HomeActions _actions;
  
  public HomeController(Provider p, Context c) {
    super(p, c);
    _actions = new HomeActions(c);
  }
  
  public View index() {
    Properties model = _actions.index();
    return view(model);
  }
  
  public View user() {
    Properties model = _actions.user();
    return view(model);
  }
  
  public View img() {
    try {
      String imageId = _context.request.getParameter("id").toLowerCase();
      if  (imageId.endsWith("jpg"))
        return raw("home/img", imageId, "image/jpg");
      else if (imageId.endsWith("png"))
        return raw("home/img", imageId, "image/png");
    }
    catch(Exception e) {
    }
    return raw(null, null, null);
  }
}
