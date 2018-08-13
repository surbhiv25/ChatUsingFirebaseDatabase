package com.ezeia.politicalparty.view.viewsInterface;

import com.ezeia.politicalparty.model.scheme.SchemeData;

import java.util.List;

public interface SchemeView extends BaseView {
    void onSchemeSuccess(List<SchemeData> data);

    void onSchemeFailed(com.ezeia.politicalparty.model.Error error);

}
