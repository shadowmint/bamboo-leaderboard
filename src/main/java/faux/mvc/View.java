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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import faux.mvc.extend.ITemplate;

public class View {

  private ITemplate _template;
  
  private Object _viewModel;
  
  private InputStream _templateStream;
  
  private Context _context;
  
  private boolean _raw = false;
  
  private String _contentType = "text/html";
  
  public View(InputStream templateStream, Object viewModel, ITemplate template, Context context) {
    _template = template;
    _viewModel = viewModel;
    _templateStream = templateStream;
    _context = context;
  }
  
  /** If raw, no template is run, the raw data is returned. eg. for images */
  public void setRaw(String contentType) {
    _contentType = contentType;
    _raw = true;
  }
  
  /** Return true if this view is marked as raw */
  public boolean raw() {
    return _raw;
  }
  
  /** Return the content type for this view */
  public String contentType() {
    return _contentType;
  }
  
  public byte[] render() {
    byte[] rtn = null;
    if (_templateStream != null) {
      
      // Raw content
      if (raw()) {
        try {
          int size = _templateStream.available();
          rtn = new byte[size];
          _templateStream.read(rtn);
        }
        catch(IOException e) {
          rtn = null;
        }
      }
      
      // Templated content
      else {
        Properties props = new Properties();
        if (_viewModel != null)
          props.put("Model", _viewModel);
        else
          props.put("Model", false);
        String output = _template.render(props, _templateStream, _context);
        rtn = output.getBytes();
      }
    }
    return rtn;
  }
}
