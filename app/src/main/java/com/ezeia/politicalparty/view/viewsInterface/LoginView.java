package com.ezeia.politicalparty.view.viewsInterface;

import com.ezeia.politicalparty.model.login.LoginResponse;

public interface LoginView extends BaseView {
    void onLoginSuccess(LoginResponse data);

    void onLoginFailed(com.ezeia.politicalparty.model.Error error);

}
