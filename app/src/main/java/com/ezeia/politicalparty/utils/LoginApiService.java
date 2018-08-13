package com.ezeia.politicalparty.utils;

import com.ezeia.politicalparty.model.login.LoginPostData;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApiService
{
    @POST(Constants.API_LOGIN)
    Call<ResponseBody> sendLoginInfo(@Body LoginPostData body);

}
