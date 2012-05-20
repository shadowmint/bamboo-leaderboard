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

package au.net.iinet.plugins.servlet.actions;

import java.util.Properties;

import com.atlassian.activeobjects.external.ActiveObjects;

import au.net.iinet.plugins.servlet.consts.AppContext;
import au.net.iinet.plugins.servlet.model.UserRepo;
import au.net.iinet.plugins.servlet.model.db.User;
import au.net.iinet.plugins.servlet.viewmodels.home.HomeIndexViewModel;
import au.net.iinet.plugins.servlet.viewmodels.home.HomeUserUpdateModel;
import au.net.iinet.plugins.servlet.viewmodels.home.HomeUserViewModel;
import au.net.iinet.plugins.servlet.viewmodels.home.UserViewModel;

import faux.mvc.Context;
import faux.mvc.Actions;

public class HomeActions extends Actions {
  
  public static final String INSERT = "insert";
  public static final String UPDATE = "update";
  public static final String QUERY = "query";
  public static final String DELETE = "delete";
  
  public HomeActions(Context context) {
    super(context);
  }
  
  public Properties index() {
    HomeIndexViewModel model = new HomeIndexViewModel();
    ActiveObjects ao = (ActiveObjects) _context.get(AppContext.ACTIVE_OBEJCTS);
    UserRepo users = new UserRepo(ao);
    
    String[] tops = {
        "Development - Top cat",
        "Development - Happy cat",
        "Development - Crazy cat"
    };
    
    String[] bottoms = {
        "Development - Lazy cat",
        "Development - Silly cat",
        "Development - Mangy cat",
        "Development - Flea-bitten stray",
        "Development - Goat",
        "Development - Silly cat",
        "Development - Lost cat"
    };
    
    model.top = UserViewModel.asArray(users.get(UserRepo.FILTER_TOP_10_BY_POINTS));
    model.bottom = UserViewModel.asArray(users.get(UserRepo.FILTER_BOTTOM_10_BY_POINTS));
    
    for(int i = 0; i < model.bottom.length; ++i) {
      int itag = ((int) (Math.random() * 100)) % bottoms.length;
      String tag = bottoms[itag];
      model.bottom[i].tag = tag;
    }
    
    for(int i = 0; i < model.top.length; ++i) {
      int itag = ((int) (Math.random() * 100)) % tops.length;
      String tag = tops[itag];
      model.top[i].tag = tag;
    }
    
    return viewModel(model);
  }
  
  public Properties user() {
    HomeUserUpdateModel input = new HomeUserUpdateModel();
    HomeUserViewModel model = new HomeUserViewModel();
    ActiveObjects ao = (ActiveObjects) _context.get(AppContext.ACTIVE_OBEJCTS);
    UserRepo users = new UserRepo(ao);
    
    updateModel(input);
    if (input.action == null) 
      input.action = "default";
    
    model.mode  = "Default";
    
    if (input.action.equals(INSERT)) {
     _context.handler.status().trace("Start with insert");
      User user = users.get(input.email);
      user.setName(input.name);
      user.setIcon(input.icon);
      user.setPoints(input.points);
      users.save(user); 
      model.message = "OK";
     _context.handler.status().trace("Done with insert");
    }
    
    else  if (input.action.equals(QUERY)) {
      if (users.exists(input.email)) {
        User user = users.get(input.email);
        model.mode = "Query";
        model.user = new UserViewModel(user);
      }
      else {
        model.message = "Unable to find that user";
      }
    }
    
    else if (input.action.equals(UPDATE)) {
      if (users.exists(input.email)) {
        User user = users.get(input.email);
        user.setName(input.name);
        user.setIcon(input.icon);
        user.setPoints(input.points);
        users.save(user); 
        model.message = "OK";
      }
      else {
        model.message = "Unable to find that user";
      }
    }
    
    else if (input.action.equals(DELETE)) {
      if (users.exists(input.email)) {
        users.delete(input.email);
        model.message = "OK";
      }
      else {
        model.message = "Unable to find that user";
      }
    }
    
    model.top = UserViewModel.asArray(users.get(UserRepo.FILTER_TOP_10_BY_POINTS));
    model.bottom = UserViewModel.asArray(users.get(UserRepo.FILTER_BOTTOM_10_BY_POINTS));
    
    return viewModel(model);
  }
}
