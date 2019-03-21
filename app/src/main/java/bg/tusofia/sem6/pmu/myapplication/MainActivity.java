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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import bg.tusofia.sem6.pmu.myapplication.Helpers.Auth;
import bg.tusofia.sem6.pmu.myapplication.Helpers.AuthOrigin;
import bg.tusofia.sem6.pmu.myapplication.ServerRequest.Method;
import bg.tusofia.sem6.pmu.myapplication.ServerRequest.Request;
import bg.tusofia.sem6.pmu.myapplication.ServerRequest.RequestBuilder;
import bg.tusofia.sem6.pmu.myapplication.ServerRequest.URL;
import bg.tusofia.sem6.pmu.myapplication.Utils.AlertDialog;
import bg.tusofia.sem6.pmu.myapplication.Utils.Toast;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final int GOOGLE_RESPONCE_CODE = 101;

    private View mainPreloadView;

    private CallbackManager callbackManager;

    private GoogleSignInClient googleSignInClient;

    private Request serverRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginButton fbOriginalButton = findViewById(R.id.fb_login_button);
        Button fbButton = findViewById(R.id.buttonLoginWithFacebook);
        Button googleButton = findViewById(R.id.buttonLoginWithGoogle);
        mainPreloadView = findViewById(R.id.mainPreloadView);

        serverRequest = new Request(this);

        fbOriginalButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        callbackManager = CallbackManager.Factory.create();
        fbOriginalButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mainPreloadView.setVisibility(View.VISIBLE);
                Log.d(TAG, loginResult.getAccessToken().getToken());
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
                new AlertDialog(MainActivity.this).getBuilder().setTitle(getResources().getString(R.string.modal_login_auth_error)).setMessage(getResources().getString(R.string.modal_login_error_fb)).show();
            }
        });


        fbButton.setOnClickListener((v) -> {
            if (!Auth.isLoggedIn(this)) {
                fbOriginalButton.performClick();
            } else {
                onLoggedIn();
            }

        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.google_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleButton.setOnClickListener(v -> {

            if (!Auth.isLoggedIn(this)) {
                Log.d(TAG, "Google: startActivityForResult");
                startActivityForResult(googleSignInClient.getSignInIntent(), GOOGLE_RESPONCE_CODE);
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
        callbackManager.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, requestCode + " " + resultCode + " " + data);
        switch (requestCode) {
            case GOOGLE_RESPONCE_CODE:
                try {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null && Auth.signIn(this, AuthOrigin.GOOGLE, account.getEmail(), account.getDisplayName(), account.getId(), account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null, account.getIdToken())) {
                        sendLoggedUserToServer(AuthOrigin.GOOGLE, account.getEmail(), account.getDisplayName(), account.getId(), account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null, account.getIdToken());
                    } else {
                        Toast.make(this, "Cannot sign to google");
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                    Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                    new AlertDialog(MainActivity.this).getBuilder().setTitle(getResources().getString(R.string.modal_login_auth_error)).setMessage(getResources().getString(R.string.modal_login_error_google)).show();
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        serverRequest.stop();
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

                            if (Auth.signIn(this, AuthOrigin.FACEBOOK, email, first_name + " " + last_name, id, image_url, accessToken.getToken())) {
                                sendLoggedUserToServer(AuthOrigin.FACEBOOK, email, first_name + " " + last_name, id, image_url, accessToken.getToken());
                            } else {
                                Toast.make(this, "Could not verify your credentials.");
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

    private void sendLoggedUserToServer(AuthOrigin origin, String email, String names, String userId, String image, String token) {

        Map<String, String> params = new HashMap<>();
        params.put("origin", String.valueOf(origin));
        params.put("email", email);
        params.put("socialId", userId);
        params.put("names", names);
        params.put("image", image);
        params.put("access_token", token);

        serverRequest.send(
                new RequestBuilder(Method.POST, URL.OAUTH_LOGIN)
                        .setResponseListener(response -> onLoggedIn())
                        .setErrorListener(error -> {
                            Log.d(TAG, new String(error.networkResponse.data));

                                    mainPreloadView.setVisibility(View.GONE);

                                    new AlertDialog(MainActivity.this).getBuilder()
                                            .setTitle(getResources().getString(R.string.modal_server_error_title))
                                            .setMessage(getResources().getString(R.string.modal_server_error_description))
                                            .setNegativeButton(android.R.string.ok, (dialog, which) -> {
                                                dialog.cancel();
                                                Auth.logOut(this);
                                            })
                                            .show();

                                }
                        )
                        .addParams(params)
                        .build(this)
        );
    }
}
