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

import java.util.ArrayList;

import faux.mvc.extend.ILog;

public class Logging implements ILog {

  ArrayList<String> _messages = new ArrayList<String>();
  
  @Override
  public void trace(String message) {
    _messages.add(message);
    System.err.println(message);
  }

  @Override
  public String debug() {
    StringBuilder rtn = new StringBuilder();
    rtn.append("<div style='margin: 10px; border: 1px solid #999; border-radius: 5px; padding: 10px;'>");
    for (String s : _messages) {
      rtn.append("<div style='padding: 3px; border: 1px solid #ffdfdf;'>");
      rtn.append(s);
      rtn.append("</div>");
    }
    rtn.append("</div>");
    return rtn.toString();
  }

}
