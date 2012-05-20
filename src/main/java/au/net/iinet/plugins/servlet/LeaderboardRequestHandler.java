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

package au.net.iinet.plugins.servlet;

import java.util.Properties;

import com.google.inject.AbstractModule;

import faux.mvc.extend.*;

import au.net.iinet.plugins.servlet.impl.*;

public class LeaderboardRequestHandler extends RequestHandler {

  public LeaderboardRequestHandler(Properties properties) {
    super(properties);
  }

  @Override
  public AbstractModule configure() {
    return new AbstractModule() {
      @Override
      public void configure() {
        bind(ITemplate.class).to(Template.class);
        bind(ITemplatePath.class).to(TemplateFile.class);
        bind(ILog.class).to(Logging.class);
      }
    };
  }

  @Override
  public String controllerNamespace() {
    return "au.net.iinet.plugins.servlet.controllers";
  }

  @Override
  public String applicationNamespace() {
    return "/bamboo/plugins/servlet/board";
  }

}
