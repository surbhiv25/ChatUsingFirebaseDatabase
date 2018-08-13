package com.ezeia.politicalparty.view.viewsInterface;

import com.ezeia.politicalparty.model.signUp.SignUpResponse;

public interface SignUpView extends BaseView {
    void onSignUpSuccess(SignUpResponse data);

    void onSignUpFailed(com.ezeia.politicalparty.model.Error error);

}
