package com.ezeia.politicalparty.model;


public interface NetworkCallback<T> {
    void onSuccess(T t);

    void onFailure(com.ezeia.politicalparty.model.Error error);
}
