package com.ezeia.politicalparty.view.Fragment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.ezeia.politicalparty.model.Error;
import com.ezeia.politicalparty.model.signUp.SignUpResponse;
import com.ezeia.politicalparty.presenter.SignUpPresenter;
import com.ezeia.politicalparty.view.Activity.LoginActivity;
import com.ezeia.politicalparty.R;
import com.ezeia.politicalparty.utils.CustomToast;
import com.ezeia.politicalparty.utils.Utils;
import com.ezeia.politicalparty.view.viewsInterface.SignUpView;
import com.firebase.client.Firebase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

public class SignUp_Fragment extends Fragment implements OnClickListener,SignUpView {
    private static View view;
    private static EditText fullName, emailId,
            password,mobileNo,userRole;
    private AutoCompleteTextView district,state;
    private static TextView login;
    private static Button signUpButton;
    private static CheckBox terms_conditions;
    private SignUpPresenter signUpPresenter;
    private boolean isLoading;
    private View errorView;

    public SignUp_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signup_layout, container, false);

        Fabric.with(getActivity(), new Crashlytics());

        initViews();
        setListeners();
        return view;
    }

    // Initialize all views
    private void initViews() {
        ArrayList<String> jsonArray = new ArrayList<>();
        fullName = (EditText) view.findViewById(R.id.fullName);
        emailId = (EditText) view.findViewById(R.id.userEmailId);
        district = (AutoCompleteTextView) view.findViewById(R.id.district);
        district.setEnabled(false);
        state = (AutoCompleteTextView) view.findViewById(R.id.state);
        password = (EditText) view.findViewById(R.id.password);
        mobileNo = view.findViewById(R.id.mobileNo);
        userRole = (EditText) view.findViewById(R.id.userRole);
        signUpButton = (Button) view.findViewById(R.id.signUpBtn);
        login = (TextView) view.findViewById(R.id.already_user);
        terms_conditions = (CheckBox) view.findViewById(R.id.terms_conditions);
        errorView = (View) view.findViewById(R.id.view_error);

        signUpPresenter = new SignUpPresenter(this);

        // Setting text selector over textviews
        @SuppressLint("ResourceType") XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(), xrp);

            login.setTextColor(csl);
            terms_conditions.setTextColor(csl);
        } catch (Exception e) {
        }

        writeToFile(Utils.jsonObject);

        String jsonData = readFromFile();
        final HashMap<String,String[]> map = new Gson().fromJson(jsonData, new TypeToken<HashMap<String, String[]>>(){}.getType());
        for(Map.Entry<String,String[]> entry: map.entrySet())
        {
            jsonArray.add(entry.getKey());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, jsonArray);
        state.setThreshold(1);
        state.setAdapter(adapter);

        state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Object item = parent.getItemAtPosition(position);
                String text = item.toString();
                if(!text.equals("District")){
                    district.setEnabled(true);
                    district.setHint("District");
                    String[] listDistricts = map.get(text);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listDistricts);
                    district.setThreshold(1);
                    district.setAdapter(adapter);
                }
                else{
                    district.setEnabled(false);
                }
            }
        });

        state.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals(""))
                {
                    district.setText("");
                    district.setEnabled(false);
                }
                else{
                    district.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    // Set Listeners
    private void setListeners() {
        signUpButton.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpBtn:
                // Call checkValidation method
                checkValidation();
                break;

            case R.id.already_user:

                // Replace login fragment
                new LoginActivity().replaceLoginFragment();
                break;
        }

    }

    // Check Validation Method
    private void checkValidation() {

        // Get all edittext texts
        String getFullName = fullName.getText().toString();
        String getEmailId = emailId.getText().toString();
        String getDistrict = district.getText().toString();
        String getState = state.getText().toString();
        String getPassword = password.getText().toString();
        String getMobileNo = mobileNo.getText().toString();
        String getRole = userRole.getText().toString().toLowerCase();

        // Pattern match for email id
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);

        // Check if all strings are null or not
        if (getFullName.equals("") || getFullName.length() == 0
                || getEmailId.equals("") || getEmailId.length() == 0
                || getDistrict.equals("") || getDistrict.length() == 0
                || getState.equals("") || getState.length() == 0
                || getPassword.equals("") || getPassword.length() == 0
                || getMobileNo.equals("")
                || getRole.length() == 0)

            new CustomToast().Show_Toast(getActivity(), view,
                    "All fields are required.");
        else if(getMobileNo.length() != 10)
            new CustomToast().Show_Toast(getActivity(), view, "Enter correct mobile number.");
            // Make sure user should check Terms and Conditions checkbox
        else {
            LinkedHashMap<String,String> hmap = new LinkedHashMap<>();
            hmap.put("full_name",getFullName);
            hmap.put("mobile_number",getMobileNo);
            hmap.put("username",getEmailId);
            hmap.put("password",getPassword);
            hmap.put("role",getRole);
            hmap.put("device_type","android");
            hmap.put("device_id","90347583475sdfhsdf3445");
            hmap.put("district",getDistrict);
            hmap.put("state",getState);

            isLoading = true;
            signUpPresenter.sendSignUpData(hmap);

           /* Toast.makeText(getActivity(), "Successfully Registered.", Toast.LENGTH_SHORT).show();
            // Replace login fragment
            new LoginActivity().replaceLoginFragment();*/
        }
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getActivity().openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = getActivity().openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
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
        if (error != null) {
            errorView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onHideErrorView() {
        isLoading = false;
        errorView.setVisibility(View.GONE);
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public void onSignUpSuccess(SignUpResponse data) {
        if(data != null && data.getStatus() == 1){
            Toast.makeText(getActivity().getApplicationContext(),data.getMessage(),Toast.LENGTH_SHORT).show();
            callFirebase();
        }else if(data != null && data.getStatus() == 0){
            Toast.makeText(getActivity().getApplicationContext(),data.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSignUpFailed(Error error) {

    }

    private void callFirebase()
    {
        String user = "", pass = "";
        user = emailId.getText().toString();
        pass = password.getText().toString();

        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.show();

        String url = "https://politicalparty-adadb.firebaseio.com/users.json";

        final String finalUser = user;
        final String finalPass = pass;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase("https://politicalparty-adadb.firebaseio.com/users");

                if(s.equals("null")) {
                    reference.child(finalUser).child("password").setValue(finalPass);
                    Toast.makeText(getActivity(), "registration successful", Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        JSONObject obj = new JSONObject(s);
                        if (!obj.has(finalUser)) {
                            reference.child(finalUser).child("password").setValue(finalPass);
                            Toast.makeText(getActivity(), "registration successful", Toast.LENGTH_LONG).show();
                            new LoginActivity().replaceLoginFragment();
                        } else {
                            Toast.makeText(getActivity(), "username already exists", Toast.LENGTH_LONG).show();
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
                System.out.println("" + volleyError );
                pd.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
        rQueue.add(request);
    }

}
