package com.ezeia.politicalparty.view.Fragment;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.ezeia.politicalparty.R;
import com.ezeia.politicalparty.chat.UserDetails;
import com.ezeia.politicalparty.model.Error;
import com.ezeia.politicalparty.model.login.LoginResponse;
import com.ezeia.politicalparty.pref.Preferences;
import com.ezeia.politicalparty.presenter.LoginPresenter;
import com.ezeia.politicalparty.utils.CustomToast;
import com.ezeia.politicalparty.utils.Database;
import com.ezeia.politicalparty.utils.Utils;
import com.ezeia.politicalparty.view.Activity.TabsActivity;
import com.ezeia.politicalparty.view.viewsInterface.LoginView;
import com.firebase.client.Firebase;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

public class Login_Fragment extends Fragment implements OnClickListener,LoginView {

    private static View view;
    private static EditText emailid, password;
    private static Button loginButton;
    private static TextView forgotPassword, signUp;
    private static CheckBox show_hide_password;
    private static LinearLayout loginLayout;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;
    private boolean isLoading;
    private View errorView;
    private LoginPresenter loginPresenter;
    private String district = "", role = "", scheme = "";

    public Login_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login_layout, container, false);

        Fabric.with(getActivity(), new Crashlytics());

        initViews();
        setListeners();
        return view;
    }

    // Initiate Views
    private void initViews() {

        if(getActivity().getSupportFragmentManager() != null)
        {
            fragmentManager = getActivity().getSupportFragmentManager();
        }
        emailid = (EditText) view.findViewById(R.id.login_emailid);
        password = (EditText) view.findViewById(R.id.login_password);
        loginButton = (Button) view.findViewById(R.id.loginBtn);
        forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
        signUp = (TextView) view.findViewById(R.id.createAccount);
        show_hide_password = (CheckBox) view.findViewById(R.id.show_hide_password);
        loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);
        errorView = (View) view.findViewById(R.id.view_error);

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        // Setting text selector over textviews
        @SuppressLint("ResourceType") XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(), xrp);

            forgotPassword.setTextColor(csl);
            show_hide_password.setTextColor(csl);
            signUp.setTextColor(csl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.callFirebaseToSaveData(getActivity());
        //Utils.callFirebaseToSaveGroups(getActivity());
    }

    // Set Listeners
    private void setListeners() {
        loginButton.setOnClickListener(this);
        // Set check listener over checkbox for showing and hiding password
        show_hide_password
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {
                        // If it is checkec then show password else hide
                        // password
                        if (isChecked) {

                            show_hide_password.setText("Hide password");// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT);
                            password.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                        } else {
                            show_hide_password.setText("Show password");// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            password.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password

                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                checkValidation();
                break;
        }
    }

    // Check Validation before login
    private void checkValidation() {
        // Get email id and password
        String getEmailId = emailid.getText().toString();
        String getPassword = password.getText().toString();

        if (getEmailId.equals("") || getEmailId.length() == 0
                || getPassword.equals("") || getPassword.length() == 0) {
            loginLayout.startAnimation(shakeAnimation);
            new CustomToast().Show_Toast(getActivity(), view,
                    "Enter both credentials.");
        }
        else {
           // Toast.makeText(getActivity(), "Do Login.", Toast.LENGTH_SHORT).show();
            //loginPresenter = new LoginPresenter(this);

           /* LinkedHashMap<String,String> hmap = new LinkedHashMap<>();
            hmap.put("username",getEmailId);
            hmap.put("password",getPassword);
            hmap.put("device_type","android");

            if(Preferences.getDeviceToken(getActivity()) != null)
                hmap.put("device_id",Preferences.getDeviceToken(getActivity()));

            isLoading = true;
            loginPresenter.sendLogin(hmap);*/

            Database database = new Database(getActivity());
            String hmapDetails = database.checkIfLoginCorrect(getEmailId,getPassword);
            if(hmapDetails != null && !hmapDetails.equals("NA"))
            {
                district = hmapDetails.split(Pattern.quote("^"))[2];
                scheme = hmapDetails.split(Pattern.quote("^"))[3];
                role = hmapDetails.split(Pattern.quote("^"))[4];

                Toast.makeText(getActivity().getApplicationContext(),"Login Succesful",Toast.LENGTH_SHORT).show();
                callFirebase();
            }else {
                new CustomToast().Show_Toast(getActivity(), view,
                        "Invalid Username or password.");
            }
        }
    }

    void createVolleyRequest()
    {
        RequestQueue requestQueue;
        try {
            if(getActivity() != null) {
                requestQueue = Volley.newRequestQueue(getActivity());
                String URL = "http://itooltips.com/demo/api/v1/login";
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("username", Preferences.getUsername(getActivity()));
                jsonBody.put("password", Preferences.getPassword(getActivity()));
                jsonBody.put("device_type", "android");
                jsonBody.put("device_id", Preferences.getDeviceToken(getActivity()));
                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("200")) {
                            Intent intent = new Intent(getActivity(), TabsActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", error.toString());
                        Toast.makeText(getActivity().getApplicationContext(), "Sorry,some error occured.Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return requestBody == null ? null : requestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        String responseString = "";
                        if (response != null) {
                            responseString = String.valueOf(response.statusCode);
                            // can get more details such as response.headers
                        }
                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                    }
                };

                requestQueue.add(stringRequest);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void onLoginSuccess(LoginResponse data) {
       /* String msg;

        if(data.getStatus() == 1)
        {
            msg = data.getMessage();
            Toast.makeText(getActivity().getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
            callFirebase();
        }else{
            msg = data.getMessage();
            Toast.makeText(getActivity().getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onLoginFailed(Error error) {

    }

    void callFirebase()
    {
        String user = "", pass = "";
        user = emailid.getText().toString();
        pass = password.getText().toString();

        String url = "https://politicalparty-adadb.firebaseio.com/users.json";
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.show();

        final String finalUser = user;
        final String finalPass = pass;
        final StringBuilder firebaseUser = new StringBuilder();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {

                Firebase reference = new Firebase("https://politicalparty-adadb.firebaseio.com/users");

                if(s.equals("null")) {

                    if(role.equals("District_Admin"))
                    {
                        firebaseUser.append(finalUser).append("-").append(district).append("-").append(scheme);
                    }else{
                        firebaseUser.append(finalUser);
                    }
                    reference.child(firebaseUser.toString()).child("password").setValue(finalPass);
                    Toast.makeText(getActivity(), "login successful", Toast.LENGTH_LONG).show();

                    Preferences.setUsername(getActivity(),firebaseUser.toString());
                    Preferences.setDistrict(getActivity(),district);
                    Preferences.setScheme(getActivity(),scheme);
                    Preferences.setRole(getActivity(),role);

                    createVolleyRequest();
                }
                else{
                    try {
                        JSONObject obj = new JSONObject(s);
                        if(role.equals("District_Admin"))
                        {
                            firebaseUser.append(finalUser).append("-").append(district).append("-").append(scheme);
                        }else{
                            firebaseUser.append(finalUser);
                        }
                        if(!obj.has(firebaseUser.toString())){
                            reference.child(firebaseUser.toString()).child("password").setValue(finalPass);
                        }
                        else if(obj.getJSONObject(firebaseUser.toString()).getString("password").equals(finalPass)){
                            UserDetails.username = firebaseUser.toString();
                            UserDetails.password = finalPass;
                           // startActivity(new Intent(getActivity(), Users.class));
                            Preferences.setUsername(getActivity(),firebaseUser.toString());
                            Preferences.setDistrict(getActivity(),district);
                            Preferences.setScheme(getActivity(),scheme);
                            Preferences.setRole(getActivity(),role);

                           createVolleyRequest();
                        }
                        else {
                            Toast.makeText(getActivity(), "incorrect password", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                pd.dismiss();
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
                pd.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
        rQueue.add(request);
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public void onShowLoadingView() {
        isLoading = false;
    }

    @Override
    public void onHideLoadingView() {
        isLoading = false;
    }

    @Override
    public void onShowEmptyView() {
        isLoading = false;
    }

    @Override
    public void onHideEmptyView() {
        isLoading = false;
    }

    @Override
    public void onShowErrorView(com.ezeia.politicalparty.model.Error error) {
        isLoading = false;
       /* if (error != null) {
            errorView.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public void onHideErrorView() {
        isLoading = false;
       // errorView.setVisibility(View.GONE);
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

}
