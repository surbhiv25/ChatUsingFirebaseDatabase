package com.ezeia.politicalparty.presenter;

import com.ezeia.politicalparty.model.Error;
import com.ezeia.politicalparty.model.NetworkCallback;
import com.ezeia.politicalparty.model.RetrofitHandler;
import com.ezeia.politicalparty.model.signUp.SignUpPostData;
import com.ezeia.politicalparty.model.signUp.SignUpResponse;
import com.ezeia.politicalparty.utils.CheckInternetConnection;
import com.ezeia.politicalparty.utils.Constants;
import com.ezeia.politicalparty.utils.SignUpApiService;
import com.ezeia.politicalparty.view.viewsInterface.SignUpView;

import java.util.LinkedHashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class SignUpPresenter {
    private final SignUpView signUpView;

    public SignUpPresenter(SignUpView signUpView) {
        this.signUpView = signUpView;
    }

    public void sendSignUpData(LinkedHashMap<String,String> signUpHmap) {
        if (signUpView.isLoading()) {
            signUpView.onShowLoadingView();

            SignUpPostData requestBody = new SignUpPostData();
            if (signUpHmap != null && signUpHmap.size() > 0)
            {
                requestBody.setFullName(signUpHmap.get("full_name"));
                requestBody.setmobile_number(signUpHmap.get("mobile_number"));
                requestBody.setusername(signUpHmap.get("username"));
                requestBody.setpassword(signUpHmap.get("password"));
                requestBody.setrole(signUpHmap.get("role"));
                requestBody.setdevice_type(signUpHmap.get("device_type"));
                requestBody.setdevice_id(signUpHmap.get("device_id"));
                requestBody.setdistrict(signUpHmap.get("district"));
                requestBody.setstate(signUpHmap.get("state"));
            }

            SignUpApiService apiInterface = RetrofitHandler.getInstance().getRetrofit(signUpView.getContext(), Constants.BASE_URL).create(SignUpApiService.class);
            Call<ResponseBody> call = apiInterface.sendRegistration(requestBody);
            RetrofitHandler.getInstance().get(networkCallback, call, SignUpResponse.class);
        }
    }

    private final NetworkCallback networkCallback = new NetworkCallback() {

        @Override
        public void onSuccess(Object o) {
            if (signUpView.getContext() != null) {
                signUpView.onHideLoadingView();
                signUpView.onHideEmptyView();
                signUpView.onHideErrorView();

                SignUpResponse centerResponse = (SignUpResponse) o;
                if (centerResponse != null) {
                    signUpView.onSignUpSuccess(centerResponse);
                } else {
                    com.ezeia.politicalparty.model.Error error = new com.ezeia.politicalparty.model.Error();
                    error.setErrorTitle("Error");
                    error.setErrorMessage("Some technical error found.");
                    signUpView.onShowErrorView(error);
                }
            }
        }

        @Override
        public void onFailure(Error error) {
            if (signUpView.getContext() != null) {
                signUpView.onHideLoadingView();
                signUpView.onHideEmptyView();
                if (CheckInternetConnection.isNetworkAvailable(signUpView.getContext())) {
                    com.ezeia.politicalparty.model.Error errorNew = new com.ezeia.politicalparty.model.Error();
                    errorNew.setErrorTitle("Oops");
                    errorNew.setErrorMessage("No Internet Found.");
                    signUpView.onSignUpFailed(errorNew);
                } else {
                    signUpView.onSignUpFailed(error);
                }
            }
        }
    };

}
