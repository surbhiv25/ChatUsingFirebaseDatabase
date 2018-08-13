package com.ezeia.politicalparty.view.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.ezeia.politicalparty.R;
import com.ezeia.politicalparty.pref.Preferences;
import com.ezeia.politicalparty.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class SchemeAdapter  extends RecyclerView.Adapter<SchemeAdapter.SchemeViewHolder> {


    private List<String> heroList;
    private Context context;
    private ViewPager viewPager;
    private ArrayList<String> listSchemeName;
    private int[] schemeIcons;

    private static int currentPosition = 0;

    public SchemeAdapter(List<String> heroList, Context context, ViewPager viewPager) {
        this.heroList = heroList;
        this.context = context;
        this.viewPager = viewPager;
        this.listSchemeName = Utils.getSchemesInfoList();
        this.schemeIcons = Utils.myImageList;
    }

    @Override
    public SchemeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);

        Fabric.with(context, new Crashlytics());

        return new SchemeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SchemeViewHolder holder, final int position)
    {
        String hero = heroList.get(position);

        holder.txt_SchemeName.setText(hero);
        holder.txt_SchemeName.setTag(hero);
        holder.txt_SchemeMSg.setText(listSchemeName.get(position));
        holder.img_scheme.setBackgroundResource(schemeIcons[position]);

        //String imgURL = Constants.BASE_IMAGE_URL+hero.getImage();

        //Glide.with(context).load(imgURL).into(holder.img_scheme);
        //holder.ll_expand.setVisibility(View.GONE);

        //if the position is equals to the item position which is to be expanded
        if (currentPosition == position) {
            Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
            holder.ll_expand.setVisibility(View.VISIBLE);
            holder.ll_expand.startAnimation(slideDown);
        }

        holder.txt_SchemeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentPosition = position;
                notifyDataSetChanged();

                String tagName = view.getTag().toString();
                Preferences.setScheme(context,tagName);

                viewPager.setCurrentItem(2);
            }
        });
    }

    @Override
    public int getItemCount() {
        return heroList.size();
    }

    class SchemeViewHolder extends RecyclerView.ViewHolder {
        TextView txt_SchemeName, txt_SchemeMSg;
        ImageView img_scheme;
        LinearLayout ll_expand;

        SchemeViewHolder(View itemView) {
            super(itemView);

            txt_SchemeName = (TextView) itemView.findViewById(R.id.txt_SchemeName);
            txt_SchemeMSg = (TextView) itemView.findViewById(R.id.txt_SchemeMSg);
            img_scheme = itemView.findViewById(R.id.img_scheme);
            ll_expand = itemView.findViewById(R.id.ll_expand);
        }
    }
}
