package bg.tusofia.sem6.pmu.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.util.Arrays;

import bg.tusofia.sem6.pmu.myapplication.Helpers.Auth;
import bg.tusofia.sem6.pmu.myapplication.Helpers.AuthOrigin;
import bg.tusofia.sem6.pmu.myapplication.Utils.AlertDialog;
import bg.tusofia.sem6.pmu.myapplication.Utils.Toast;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private View mainPreloadView;

    private CallbackManager callbackManager;

    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginButton fbOriginalButton = findViewById(R.id.fb_login_button);
        Button fbButton = findViewById(R.id.buttonLoginWithFacebook);
        Button googleButton = findViewById(R.id.buttonLoginWithGoogle);
        mainPreloadView = findViewById(R.id.mainPreloadView);

        // FB Login //TODO: extract in separate method or better be static class
        fbOriginalButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        callbackManager = CallbackManager.Factory.create();
        fbOriginalButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mainPreloadView.setVisibility(View.VISIBLE);
                if (loginResult.getAccessToken() != null) {
                    getFBData(loginResult.getAccessToken());
                }
            }

            @Override
            public void onCancel() {
                Toast.make(MainActivity.this, getResources().getString(R.string.modal_login_on_cancel));
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, exception.getMessage());
                new AlertDialog(MainActivity.this).getBuilder().setTitle("Auth error").setMessage(getResources().getString(R.string.modal_login_error_fb)).show();
            }
        });


        fbButton.setOnClickListener((v) -> {
            if (!Auth.isLoggedIn(this)) {
                fbOriginalButton.performClick();
            } else {
                onLoggedIn();
            }

        });

        // Google login //TODO: extract in separate method
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getResources().getString(R.string.google_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleButton.setOnClickListener(v -> {

            if (!Auth.isLoggedIn(this)) {
                Log.d(TAG, "Google: startActivityForResult");
                startActivityForResult(googleSignInClient.getSignInIntent(), 101);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Auth.isLoggedIn(this)) {
            onLoggedIn();
        } else {
            Log.d(TAG, "Not logged in");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, requestCode + " " + resultCode + " " + data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 101:
                    try {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        if (account != null && Auth.signIn(this, AuthOrigin.GOOGLE, account.getEmail(), account.getDisplayName(), account.getId(), account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null, account.getIdToken())) {
                            //TODO: send request to server backend
                            onLoggedIn();
                        } else {
                            Toast.make(this, "Cannot sign to google");
                        }
                    } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                    }
                    break;
            }
    }

    private void onLoggedIn() {
        startActivity(new Intent(this, LocationsActivity.class));
        finish();
    }

    private void getFBData(final AccessToken accessToken) {
        new Thread(() -> {
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken, (object, response) -> {
                        if (response.getError() != null) {
                            AlertDialog.showError(this, "FB Login error", response.getError().getErrorMessage());
                            Auth.logOut(this);
                            mainPreloadView.setVisibility(View.GONE);
                            return;
                        }
                        if (object == null) {
                            AlertDialog.showError(this, "FB Login error", "Cannot get data from FB");
                            Auth.logOut(this);
                            mainPreloadView.setVisibility(View.GONE);
                            return;
                        }
                        Log.d("TAG", object.toString());
                        try {
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String email = object.getString("email");
                            String id = object.getString("id");
                            String image_url = "https://graph.facebook.com/" + id + "/picture?type=large";

                            if (Auth.signIn(this, AuthOrigin.FACEBOOK, id, email, first_name + " " + last_name, image_url, accessToken.getToken())) {
                                //TODO: send request to server backend
                                onLoggedIn();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Auth.logOut(this);
                        }

                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "first_name,last_name,email,id");
            request.setParameters(parameters);
            request.executeAndWait();

        }).start();
    }
}
