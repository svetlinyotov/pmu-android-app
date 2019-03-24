package com.snsdevelop.tusofia.sem6.pmu.Helpers;

import android.content.Context;
import android.content.Intent;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.snsdevelop.tusofia.sem6.pmu.MainActivity;
import com.snsdevelop.tusofia.sem6.pmu.R;
import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;

public class Auth {

    public static boolean signIn(final Context context, AuthOrigin origin, String email, String names, String userId, String image, String token) {
        try {
            StoredData.saveString(context, StoredData.LOGGED_USER_ORIGIN, String.valueOf(origin));
            StoredData.saveString(context, StoredData.LOGGED_USER_EMAIL, email);
            StoredData.saveString(context, StoredData.LOGGED_USER_ID, userId);
            StoredData.saveString(context, StoredData.LOGGED_USER_NAME, names);
            StoredData.saveString(context, StoredData.LOGGED_USER_IMAGE, image);
            StoredData.saveString(context, StoredData.LOGGED_USER_TOKEN, token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean logOut(final Context context) {
        try {
            if (AuthOrigin.valueOf(StoredData.getString(context, StoredData.LOGGED_USER_ORIGIN)).equals(AuthOrigin.FACEBOOK)) {
                LoginManager.getInstance().logOut();
            } else {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestServerAuthCode(context.getResources().getString(R.string.google_client_id))
                        .requestEmail()
                        .build();
                GoogleSignIn.getClient(context, gso).signOut();
            }
            StoredData.saveString(context, StoredData.LOGGED_USER_ORIGIN, null);
            StoredData.saveString(context, StoredData.LOGGED_USER_EMAIL, null);
            StoredData.saveString(context, StoredData.LOGGED_USER_ID, null);
            StoredData.saveString(context, StoredData.LOGGED_USER_NAME, null);
            StoredData.saveString(context, StoredData.LOGGED_USER_IMAGE, null);
            StoredData.saveString(context, StoredData.LOGGED_USER_TOKEN, null);

            context.startActivity(new Intent(context, MainActivity.class));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isLoggedIn(final Context context) {
        return StoredData.getString(context, StoredData.LOGGED_USER_TOKEN) != null;
    }

    public static String getAccessToken(final Context context) {
        return StoredData.getString(context, StoredData.LOGGED_USER_TOKEN);
    }

    public static String getUserId(final Context context) {
        return StoredData.getString(context, StoredData.LOGGED_USER_ID);
    }

    public static String getOrigin(final Context context) {
        return StoredData.getString(context, StoredData.LOGGED_USER_ORIGIN);
    }
}
