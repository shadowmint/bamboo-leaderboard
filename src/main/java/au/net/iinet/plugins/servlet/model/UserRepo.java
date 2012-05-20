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

package au.net.iinet.plugins.servlet.model;

import java.util.ArrayList;

import net.java.ao.Query;

import au.net.iinet.plugins.servlet.model.db.User;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.sal.api.transaction.TransactionCallback;

/** Handles fetching and creating users */
public class UserRepo {
  
  public static final int FILTER_ALL = 0;
  
  public static final int FILTER_TOP_10_BY_POINTS = 1;
  
  public static final int FILTER_BOTTOM_10_BY_POINTS = 2;
  
  private ActiveObjects _ao;
  
  public UserRepo(ActiveObjects ao) {
    _ao = ao;
  }
  
  /** Email cleaner */
  private String cleanEmail(String email) {
    return email.toLowerCase();
  }
  
  /** Finds multiple user objects using a filter type. */
  public ArrayList<User> get(final int filterType) {
    final ArrayList<User> rtn = new ArrayList<User>();
    final ActiveObjects ao = _ao;
    ao.executeInTransaction(new TransactionCallback<Void>() {
      @Override
      public Void doInTransaction() {
        User[] results = null;
        switch(filterType) {
        
          // All records
          case FILTER_ALL:
            results = ao.find(User.class);
            break;
            
          // Top ten (ie. most points; good guys)
          case FILTER_TOP_10_BY_POINTS:
            results = ao.find(User.class, Query.select().limit(10).order("points desc"));
            break;
            
          // Bottom ten (ie. least points, or -ve. Losers)
          case FILTER_BOTTOM_10_BY_POINTS:
            results = ao.find(User.class, Query.select().limit(10).order("points asc"));
            break;
        }
        for (User u : results) {
          rtn.add(u);
        }
        return null;
      }
    });
    return rtn;
  }
  
  /** 
   * Finds a single user object and returns it by email.
   * Returns a new object if one wasn't found.
   */
  public void delete(String id) {
    
    final String email = cleanEmail(id);
    final ActiveObjects ao = _ao;
    
    // Found a record! Return that~
    if (exists(email)) {
      final User user = get(id);
      ao.executeInTransaction(new TransactionCallback<Void>() {
        @Override
        public Void doInTransaction() {
          ao.delete(user);
          return null;
        }
      });
    }
  }
  
  /** 
   * Finds a single user object and returns it by email.
   * Returns a new object if one wasn't found.
   */
  public User get(String id) {
    
    User rtn = null;
    final String email = cleanEmail(id);
    final ActiveObjects ao = _ao;
    
    // Found a record! Return that~
    if (exists(email)) {
      rtn = ao.executeInTransaction(new TransactionCallback<User>() {
        @Override
        public User doInTransaction() {
          User[] users = ao.find(User.class, Query.select().where("email = ?", email));
          return users[0];
        }
      });
    }
    
    // No matching record, create one.
    else {
      rtn = ao.executeInTransaction(new TransactionCallback<User>() {
        @Override
        public User doInTransaction() {
          User user = ao.create(User.class);
          user.setEmail(email);
          
          // We cannot allow an absence of kittens
          if (user.getIcon() == null) 
            user.setIcon("http://placekitten.com/g/40/40");
          
          user.save();
          return user;
        }
      });
    }
    
    return rtn;
  }
  
  /** Checks if a user object exists */
  public boolean exists(String id) {
    
    final String email = cleanEmail(id);
    final ActiveObjects ao = _ao;
    
    Boolean rtn = ao.executeInTransaction(new TransactionCallback<Boolean>() {
      @Override
      public Boolean doInTransaction() {
        int count = ao.count(User.class, Query.select().limit(1).where("email = ?", email));
        return count > 0;
      }
    });
    
    return rtn;
  }
  
  /** Persists a user object */
  public void save(final User user) {
    final ActiveObjects ao = _ao;
    ao.executeInTransaction(new TransactionCallback<Void>() {
      @Override
      public Void doInTransaction() {
        user.setEmail(cleanEmail(user.getEmail()));
        user.save();
        return null;
      }
    });
  }
}
