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

package au.net.iinet.plugins.servlet.viewmodels.home;

import java.util.ArrayList;

import au.net.iinet.plugins.servlet.model.db.User;

public class UserViewModel {
  public String email;
  public String icon;
  public String name;
  public String tag;
  public int points;
  
  public UserViewModel(String email, String icon, String name, int points) {
    this.email = email;
    this.name = name;
    this.icon = icon;
    this.points = points;
  }
  
  public String getEmail() {
    return email;
  }
  
  public String getName() {
    return name;
  }
  
  public String getIcon() {
    return icon;
  }
  
  public int getPoints() {
    return points;
  }
  
  public String getTag() {
    return tag;
  }
  
  public UserViewModel(User user) {
    email = user.getEmail();
    icon = user.getIcon();
    points = user.getPoints();
    name = user.getName();
  }
  
  public static UserViewModel[] asArray(ArrayList<User> users) {
    UserViewModel rtn[] = new UserViewModel[users.size()];
    for (int i = 0; i < users.size(); ++i) {
      rtn[i] = new UserViewModel(users.get(i));
    }
    return rtn;
  }
}