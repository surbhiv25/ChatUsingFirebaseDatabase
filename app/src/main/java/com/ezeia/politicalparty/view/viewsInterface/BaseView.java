package com.ezeia.politicalparty.view.viewsInterface;

import android.content.Context;

import java.io.Serializable;

public interface BaseView extends Serializable {
    Context getContext();

    void onShowLoadingView();

    void onHideLoadingView();

    void onShowEmptyView();

    void onHideEmptyView();

    void onShowErrorView(com.ezeia.politicalparty.model.Error error);

    void onHideErrorView();

    boolean isLoading();
}
