package com.ezeia.politicalparty.presenter;

import com.ezeia.politicalparty.model.Error;
import com.ezeia.politicalparty.model.NetworkCallback;
import com.ezeia.politicalparty.model.RetrofitHandler;
import com.ezeia.politicalparty.model.login.LoginPostData;
import com.ezeia.politicalparty.model.login.LoginResponse;
import com.ezeia.politicalparty.utils.CheckInternetConnection;
import com.ezeia.politicalparty.utils.Constants;
import com.ezeia.politicalparty.utils.LoginApiService;
import com.ezeia.politicalparty.view.viewsInterface.LoginView;
import java.util.LinkedHashMap;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class LoginPresenter {
    private final LoginView loginView;

    public LoginPresenter(LoginView loginView) {
        this.loginView = loginView;
    }

    public void sendLogin(LinkedHashMap<String,String> signUpHmap) {
        if (loginView.isLoading()) {
            loginView.onShowLoadingView();

            LoginPostData requestBody = new LoginPostData();
            if (signUpHmap != null && signUpHmap.size() > 0)
            {
                requestBody.setusername(signUpHmap.get("username"));
                requestBody.setpassword(signUpHmap.get("password"));
                requestBody.setdevice_type(signUpHmap.get("device_type"));
                requestBody.setdevice_id(signUpHmap.get("device_id"));
            }

            LoginApiService apiInterface = RetrofitHandler.getInstance().getRetrofit(loginView.getContext(), Constants.BASE_URL).create(LoginApiService.class);
            Call<ResponseBody> call = apiInterface.sendLoginInfo(requestBody);
            RetrofitHandler.getInstance().get(networkCallback, call, LoginResponse.class);
        }
    }

    private final NetworkCallback networkCallback = new NetworkCallback() {

        @Override
        public void onSuccess(Object o) {
            if (loginView.getContext() != null) {
                loginView.onHideLoadingView();
                loginView.onHideEmptyView();
                loginView.onHideErrorView();

                LoginResponse centerResponse = (LoginResponse) o;
                if (centerResponse != null) {
                    if (centerResponse.getStatus() != null) {
                        loginView.onLoginSuccess(centerResponse);
                    }
                } else {
                    Error error = new Error();
                    error.setErrorTitle("Oops");
                    error.setErrorMessage("Some technical issue occurred");
                    loginView.onShowErrorView(error);
                }
            }
        }

        @Override
        public void onFailure(Error error) {
            if (loginView.getContext() != null) {
                loginView.onHideLoadingView();
                loginView.onHideEmptyView();
                if (CheckInternetConnection.isNetworkAvailable(loginView.getContext())) {
                    Error errorNew = new Error();
                    errorNew.setErrorTitle("Error");
                    errorNew.setErrorMessage("No Internet Connection Found.");
                    loginView.onLoginFailed(errorNew);
                } else {
                    loginView.onLoginFailed(error);
                }
            }
        }
    };

}
