package com.ezeia.politicalparty.presenter;

import com.ezeia.politicalparty.model.Error;
import com.ezeia.politicalparty.model.NetworkCallback;
import com.ezeia.politicalparty.model.RetrofitHandler;
import com.ezeia.politicalparty.model.scheme.SchemeData;
import com.ezeia.politicalparty.model.scheme.SchemesResponse;
import com.ezeia.politicalparty.utils.CheckInternetConnection;
import com.ezeia.politicalparty.utils.Constants;
import com.ezeia.politicalparty.utils.SchemeApiService;
import com.ezeia.politicalparty.view.viewsInterface.SchemeView;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class SchemePresenter {
    private final SchemeView loginView;

    public SchemePresenter(SchemeView loginView) {
        this.loginView = loginView;
    }

    public void getSchemesList() {
            SchemeApiService apiInterface = RetrofitHandler.getInstance().getRetrofit(loginView.getContext(), Constants.BASE_URL).create(SchemeApiService.class);
            Call<ResponseBody> call = apiInterface.getSchemesList();
            RetrofitHandler.getInstance().get(networkCallback, call, SchemesResponse.class);
    }

    private final NetworkCallback networkCallback = new NetworkCallback() {

        @Override
        public void onSuccess(Object o) {
            if (loginView.getContext() != null) {

                SchemesResponse centerResponse = (SchemesResponse) o;
                if (centerResponse != null) {
                    List<SchemeData> data = centerResponse.getData();
                    loginView.onSchemeSuccess(data);
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

                if (CheckInternetConnection.isNetworkAvailable(loginView.getContext())) {
                    Error errorNew = new Error();
                    errorNew.setErrorTitle("Error");
                    errorNew.setErrorMessage("No Internet Connection Found.");
                    loginView.onSchemeFailed(errorNew);
                } else {
                    loginView.onSchemeFailed(error);
                }
            }
        }
    };

}
