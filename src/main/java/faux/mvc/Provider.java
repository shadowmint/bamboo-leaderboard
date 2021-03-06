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

import faux.mvc.extend.ITemplate;
import faux.mvc.extend.ITemplatePath;

import com.google.inject.*;

/** Service provider for controller to use */
public class Provider {
  
  public ITemplate template = null;
  
  public ITemplatePath templatePath = null;
  
  @Inject
  public Provider(ITemplate template, ITemplatePath templatePath) {
    this.template = template;
    this.templatePath = templatePath;
  }
}
