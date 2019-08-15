// XAidl.aidl
package com.example.xbinderservice;

// Declare any non-default types here with import statements
import com.example.xbinderservice.User;

interface XAidl {

  User login(in User user);

  long register(String  name,String password);

  int resetPwd(String name,String oldPwd,String newPwd);

}
