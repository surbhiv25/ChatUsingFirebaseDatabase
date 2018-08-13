package com.ezeia.politicalparty.model;

import android.content.Context;
import android.util.Log;

import com.ezeia.politicalparty.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;


/**
 * Handles all Network Transactions.
 * Create connection, read data from server.
 *
 * @param <T>
 */

public class RetrofitHandler<T> {

    private static final String TAG = "RetrofitHandler";
    private static final boolean SHOW_LOG = true;
    private static RetrofitHandler retrofitHandler = null;
    private static final int TIME_OUT = 60000;

    private RetrofitHandler() {
    }

    public static RetrofitHandler getInstance() {
        if (retrofitHandler == null) {
            retrofitHandler = new RetrofitHandler();
        }
        return retrofitHandler;
    }

    /**
     * Build retrofit instance.
     *
     * @param baseUrl     url used to fetch data
     * @return Retrofit instance.
     */

    public Retrofit getRetrofit(final Context context, String baseUrl) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(final String message) {
                try {
                    Timber.tag(Constants.TAG_NETWORK);
                    Log.d(Constants.TAG_NETWORK, message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(SHOW_LOG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        builder.addInterceptor(loggingInterceptor);

        builder.connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(TIME_OUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS);
        OkHttpClient client = builder.build();

        return new Retrofit.Builder().client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build();
    }

    public void get(final NetworkCallback<T> responseListener, Call<ResponseBody> call, final Class<T> tClass) {
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                T callResponse = null;
                try {

                    Gson gson = new Gson();
                    callResponse = gson.fromJson(getBodyFromResponse(response), tClass);
                    if (callResponse != null) {
                        responseListener.onSuccess(callResponse);
                    }
                } catch (Exception e) {
                        e.printStackTrace();
                        Error error1 = new Error();
                        error1.setErrorTitle("Oops");
                        error1.setErrorMessage("Technical Issue Found.");
                        responseListener.onFailure(error1);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                try {
                    if (t instanceof SocketTimeoutException) {
                        Error errorNet = new Error();
                       /* errorNet.setErrorTitle(getContext().getResources().getString(R.string.error));
                        errorNet.setErrorMessage(getContext().getResources().getString(R.string.socketException));*/
                        responseListener.onFailure(errorNet);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void checkResponse(Response<ResponseBody> response, NetworkResponseListener callback) {
      /*  try {
            String body = getBodyFromResponse(response);
            JSONObject jsonObject = new JSONObject(body);
            if (jsonObject != null) {
                callback.onSuccess(body);
            } else {
                Error error = new Error();
                error.setErrorTitle(ErrorConstants.TITLE_DATA_FORMAT_ERROR);
                error.setErrorMessage(ErrorConstants.MSG_DATA_FORMAT_ERROR);
                callback.onFailure(error);
            }
        } catch (Exception e) {
            Error error = new Error();
            error.setErrorTitle(ErrorConstants.TITLE_DATA_FORMAT_ERROR);
            error.setErrorMessage(ErrorConstants.MSG_DATA_FORMAT_ERROR);
            callback.onFailure(error);
            callback.onFailure(error);
            e.printStackTrace();
        }*/
    }

    private String getBodyFromResponse(Response<ResponseBody> response) {
        String body = "";
        try {
            if (response != null) {
                if (response.body() != null) {
                    body = getResponseBody(response);
                } else if (response.errorBody() != null) {
                    body = getErrorBody(response);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return body;
    }

    private String getErrorBody(Response<ResponseBody> response) {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        reader = new BufferedReader(new InputStreamReader(response.errorBody().byteStream()));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        }

        return sb.toString();

    }

    private String getResponseBody(Response<ResponseBody> response) {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        reader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        }

        return sb.toString();

    }

}
