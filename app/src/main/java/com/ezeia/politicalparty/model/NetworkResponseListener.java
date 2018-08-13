package com.ezeia.politicalparty.model;

import java.lang.Error;

public interface NetworkResponseListener<T> {
    void onStartProgress();

    void onSuccess(T t);

    void stringResponse(String string);

    void onFailure(Error error);

}
