package com.ezeia.politicalparty.view.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.ezeia.politicalparty.R;
import com.ezeia.politicalparty.view.adapter.MessagesAdapter;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class Tab_Second_fragment extends Fragment {

    private RecyclerView recyclerView;
    View view;
    private MessagesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_two, container, false);

        Fabric.with(getActivity(), new Crashlytics());

        initViews();
        return view;
    }

    void initViews()
    {
        ArrayList<String> hashMap = new ArrayList<>();
        hashMap.add("Admin1_Gurugram_Beti Bachao Beti Padhao_It is a good initiative taken by Mr. Narendra Modi to start this scheme."+"_"+"20-July-2018_1");
        hashMap.add("User5_West Delhi_Beti Bachao Beti Padhao_Modi Ji had called this scheme for the eradication of female foeticide."+"_"+"18-July-2018_0");
        hashMap.add("Admin8_West Delhi_Pradhan Mantri Awas Yojna_This scheme by Modi Ji was launched in June 2015."+"_"+"10-July-2018_0");
        hashMap.add("Admin4_North Delhi_Pradhan Mantri Awas Yojna_Modi Ji wants every person in India to have their own house."+"_"+"08-July-2018_0");
        hashMap.add("User9_North Delhi_Clean Ganga_Mr. Narendra Modi took the initiative to clean the holy river Ganga."+"_"+"05-June-2018_0");

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new MessagesAdapter(hashMap,getActivity());
        recyclerView.setAdapter(adapter);
    }
}


