package com.ezeia.politicalparty.utils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;

public interface SchemeApiService
{
    @POST(Constants.API_SCHEMES)
    Call<ResponseBody> getSchemesList();
}
