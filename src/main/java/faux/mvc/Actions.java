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

import java.lang.reflect.Field;
import java.util.Properties;

/** Base for controller implementations */
public abstract class Actions {
  
  protected Context _context;
  
  public Actions(Context context) {
    _context = context;
  }
  
  /** Extracts top level public properties of the object and creates a property set */
  protected Properties viewModel(Object model) {
    Properties rtn = new Properties();
    Class<?> ctype = model.getClass();
    Field fields[] = ctype.getFields();
    for (Field f : fields) {
      String name = f.getName();
      try {
        Object value = f.get(model);
        if (value == null) 
          value = "";
        rtn.put(name, value);
      }
      catch(IllegalAccessException e) {
      } // Ignore private fields.
    }
    return rtn;
  }
  
  /** Extracts parameters and binds them to an update model */
  protected void updateModel(Object model) {
    Class<?> ctype = model.getClass();
    Field fields[] = ctype.getFields();
    for (Field f : fields) {
      String name = f.getName();
      try {
        String svalue = _context.request.getParameter(name);
        Class<?> t = f.getType();
        
        // string
        if (t == String.class) {
          f.set(model, svalue);
        }
        else {
          if (t.isPrimitive()) {
            
            // int
            if (t == int.class) {
              int ivalue = 0;
              try {
                ivalue = Integer.parseInt(svalue);
              }
              catch(Exception e) {
                _context.handler.status().trace("Invalid int value: " + svalue);
              }
              f.set(model, ivalue);
            }
            
            // bool is from 0 or 1
            else if (t == int.class) {
              int ivalue = 0;
              try {
                ivalue = Integer.parseInt(svalue);
              }
              catch(Exception e) {
                _context.handler.status().trace("Invalid bool value: " + svalue);
              }
              boolean bvalue = ivalue == 1 ? true : false;
              f.set(model, bvalue);
            }
          } 
        }
      }
      catch(IllegalAccessException e) {
      } // Ignore private fields.
    }
  }
  
  /** Returns true if this is a post request */
  public boolean isPost() {
    return _context.request.getMethod() == "POST";
  }
}
