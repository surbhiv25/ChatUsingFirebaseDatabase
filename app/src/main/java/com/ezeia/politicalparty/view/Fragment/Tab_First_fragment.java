package com.ezeia.politicalparty.view.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.ezeia.politicalparty.R;
import com.ezeia.politicalparty.model.Error;
import com.ezeia.politicalparty.model.scheme.SchemeData;
import com.ezeia.politicalparty.pref.Preferences;
import com.ezeia.politicalparty.presenter.SchemePresenter;
import com.ezeia.politicalparty.utils.Database;
import com.ezeia.politicalparty.view.adapter.SchemeAdapter;
import com.ezeia.politicalparty.view.viewsInterface.SchemeView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class Tab_First_fragment  extends Fragment implements SchemeView {

    private static View view;
    private RecyclerView recyclerView;
    private LinearLayout ll_beti,ll_Digital,ll_Ganga,ll_Swachh;
    private TextView txt_BetiBachao,txt_Swachh,txt_Ganga,txt_Digital;
    private ArrayList<String> schemeList;
    private SchemePresenter presenter;
    private SchemeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_one, container, false);

        Fabric.with(getActivity(), new Crashlytics());

        initViews();
        return view;
    }

    // Initiate Views
    private void initViews() {

        ViewPager pager = null;
        if(getActivity() != null)
            pager = getActivity().findViewById(R.id.viewPager);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        schemeList = new ArrayList<>();

        //presenter = new SchemePresenter(this);
        //presenter.getSchemesList();

        Database database = new Database(getActivity());
        LinkedHashMap<String,String> hmap = database.fetchSchemes();
        if(hmap != null && hmap.size() > 0)
        {
            for(Map.Entry<String,String> entry: hmap.entrySet())
            {
                if(!Preferences.getRole(getActivity()).equals("User") && !Preferences.getRole(getActivity()).equals("Super_Admin"))
                {
                    if(entry.getValue().equals(Preferences.getScheme(getActivity())))
                        schemeList.add(entry.getValue());
                }
                else
                {
                    schemeList.add(entry.getValue());
                }
            }
        }
        adapter = new SchemeAdapter(schemeList,getActivity(),pager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSchemeSuccess(List<SchemeData> data) {

        //schemeList.addAll(data);
        //adapter = new SchemeAdapter(schemeList,getActivity());
        //recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSchemeFailed(Error error) {

    }

    @Override
    public void onShowLoadingView() {

    }

    @Override
    public void onHideLoadingView() {

    }

    @Override
    public void onShowEmptyView() {

    }

    @Override
    public void onHideEmptyView() {

    }

    @Override
    public void onShowErrorView(Error error) {

    }

    @Override
    public void onHideErrorView() {

    }

    @Override
    public boolean isLoading() {
        return false;
    }
}


