package com.ezeia.politicalparty.utils;

import com.ezeia.politicalparty.model.signUp.SignUpPostData;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SignUpApiService
{
    @POST(Constants.API_SIGNUP)
    Call<ResponseBody> sendRegistration(@Body SignUpPostData body);

}
