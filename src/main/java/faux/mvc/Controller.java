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

import com.google.inject.*;

import java.io.InputStream;

import faux.mvc.extend.ITemplate;
import faux.mvc.extend.ITemplatePath;

/** Base for all controllers */
public abstract class Controller {

  private ITemplate _template;
  
  private ITemplatePath _templatePath;
  
  protected Context _context;
  
  @Inject
  public Controller(Provider provider, Context context) {
    _template = provider.template;
    _templatePath = provider.templatePath;
    _context = context;
  }
  
  private String controllerId() {
    String rtn = this.getClass().getSimpleName();
    rtn = rtn.replace("Controller", "");
    rtn = rtn.toLowerCase();
    return rtn;
  }
  
  private String viewInvoker() {
    StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
    StackTraceElement e = stacktrace[3];
    String methodName = e.getMethodName();
    return methodName;
  }
  
  protected View view() {
    String method = viewInvoker();
    return view(method, null);
  }
  
  protected View view(String vid) {
    return view(vid, null);
  }
  
  protected View view(Object viewModel) {
    String method = viewInvoker();
    return view(method, viewModel);
  }
  
  protected View view(String vid, Object viewModel){
    String cid = controllerId();
    InputStream templateStream = _templatePath.resolvePath(cid, vid, "text/html", _context);
    View rtn = new View(templateStream, viewModel, _template, _context);
    return rtn;
  }
  
  protected View raw(String context, String path, String content_type) {
    InputStream templateStream = _templatePath.resolvePath(context, path, content_type, _context);
    View rtn = new View(templateStream, null, _template, _context);
    rtn.setRaw("image/jpg");
    return rtn;
  }
}
