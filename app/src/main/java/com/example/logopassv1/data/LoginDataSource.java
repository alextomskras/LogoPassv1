package com.example.logopassv1.data;

import android.util.Log;

import com.example.logopassv1.data.model.LoggedInUser;

import java.io.IOException;
import java.util.Objects;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    final String MYPASSWORD = "123321";
    final String MYLOGIN = "aaa";

    public Result<LoggedInUser> login(String username, String password) {

        try {

            LoggedInUser fakeUser =
                    new LoggedInUser(username,
//                            java.util.UUID.randomUUID().toString(),
                            " ALEX ZAIN");
            if (Objects.equals(username, MYLOGIN) && Objects.equals(password, MYPASSWORD)) {
                Log.d("TAG","Username___"+username);
                return new Result.Success<>(fakeUser);
            }else {
                return new Result.Error(new IOException("Error logging in"));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
    

}