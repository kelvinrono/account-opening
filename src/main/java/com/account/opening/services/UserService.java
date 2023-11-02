package com.account.opening.services;

import com.account.opening.objects.UserObject;

import java.util.HashMap;

public interface UserService {
    HashMap openAccount(HashMap params);
    HashMap getSpecificUserAccount(long id);
    HashMap debitAccount(long id, UserObject userObject);
    HashMap creditAccount(long id, UserObject userObject);
    HashMap validateUser (HashMap params);
    HashMap updateDetails(long id, UserObject userObject);

    HashMap loginUser(HashMap params);

}
