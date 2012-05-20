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

import faux.mvc.extend.ILog;

import com.google.inject.*;

/** Request status tracking class */
public class Status {
  
  public static int SUCCESS = 200;
  
  public static int ERROR = 500;
  
  public static int NOT_FOUND = 404;
  
  private ILog _log;
  
  private boolean _failed;
  
  private int _code;
  
  @Inject
  public Status(ILog log) {
    _log = log;
    _failed = false;
    _code = SUCCESS;
  }
  
  /** Report an error occurred and stack trace */
  public void error(String message, Throwable e, int code) {
    error(message, code);
    _log.trace(e.toString());
    for (StackTraceElement s : e.getStackTrace()) {
      _log.trace(s.toString());
    }
  }
  
  /** Report an error occurred */
  public void error(String message, int code) {
    _log.trace("Request failed: CODE " + code);
    _log.trace(message);
    _code = code;
    if (_code != SUCCESS) {
      _failed = true;
    }
  }
  
  /** General debugging log message */
  public void trace(String message) {
    _log.trace(message);
  }
  
  /** If the request failed */
  public boolean failed() {
    return _failed;
  }
  
  /** Return log debug code */
  public String debug() {
    return _log.debug();
  }
  
  /** Status code */
  public int code() {
    return _code;
  }
}
