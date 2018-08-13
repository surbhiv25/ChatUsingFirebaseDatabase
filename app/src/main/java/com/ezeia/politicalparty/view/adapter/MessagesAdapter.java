package com.ezeia.politicalparty.view.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.ezeia.politicalparty.R;

import java.util.ArrayList;
import java.util.regex.Pattern;

import io.fabric.sdk.android.Fabric;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.SchemeViewHolder> {


    ArrayList<String> hashMap;
    private Context context;

    private static int currentPosition = 0;

    public MessagesAdapter( ArrayList<String> hashMap, Context context) {
        this.hashMap = hashMap;
        this.context = context;
    }

    @Override
    public SchemeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_messgaes, parent, false);

        Fabric.with(context, new Crashlytics());

        return new SchemeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SchemeViewHolder holder, final int position)
    {
        String hero = hashMap.get(position);

        String userName = hero.split(Pattern.quote("_"))[0];
        String district = hero.split(Pattern.quote("_"))[1];
        String scheme = hero.split(Pattern.quote("_"))[2];
        String userMsg = hero.split(Pattern.quote("_"))[3];
        String date = hero.split(Pattern.quote("_"))[4];
        String count = hero.split(Pattern.quote("_"))[5];

        holder.txt_UserName.setText(userName);
        holder.txt_UserMsg.setText(userMsg);
        holder.txt_Count.setText(count);
        holder.txt_Date.setText(date);

        if(count.equals("0"))
        {
            holder.txt_Count.setVisibility(View.INVISIBLE);
        }else {
            holder.txt_Count.setVisibility(View.VISIBLE);
        }

        holder.ll_Parent.setTag(hero);

        holder.ll_Parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = view.getTag().toString().split(Pattern.quote("_"))[0];
                String district = view.getTag().toString().split(Pattern.quote("_"))[1];
                String scheme = view.getTag().toString().split(Pattern.quote("_"))[2];
                String userMsg = view.getTag().toString().split(Pattern.quote("_"))[3];
                //String date = view.getTag().toString().split(Pattern.quote("_"))[4];
                String count = view.getTag().toString().split(Pattern.quote("_"))[5];

                if(count.equals("1"))
                {
                    holder.txt_Count.setVisibility(View.INVISIBLE);
                }
                openPopUp(userName,district,scheme,userMsg);
            }
        });
    }

    private void openPopUp(String userName, String district, String scheme, String userMsg)
    {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = mInflater.inflate(R.layout.popup_messgaes, null, false);
        dialog.setView(mView);

        TextView txt_UserName= mView.findViewById(R.id.txt_UserName);
        TextView txt_District= mView.findViewById(R.id.txt_District);
        TextView txt_schemeName= mView.findViewById(R.id.txt_schemeName);
        TextView textMsg= mView.findViewById(R.id.textMsg);

        txt_UserName.setText(userName);
        txt_District.setText(district);
        txt_schemeName.setText(scheme);
        highlightWord(userMsg,textMsg);
        //textMsg.setText(userMsg);

        final AlertDialog alert = dialog.create();
        alert.show();

        Button btn_dismiss = mView.findViewById(R.id.btn_dismiss);
        btn_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
    }

    private void highlightWord(String userMsg, TextView textMsg)
    {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String firstWord = userMsg.split(Pattern.quote("Modi"))[0];
        String secondWord = userMsg.split(Pattern.quote("Modi"))[1];
        String keyword = "Modi";

        builder.append(firstWord);

        SpannableString str1= new SpannableString(keyword);
        str1.setSpan(new ForegroundColorSpan(Color.RED), 0, str1.length(), 0);
        builder.append(str1);

        builder.append(secondWord);

        textMsg.setText( builder, TextView.BufferType.SPANNABLE);
    }

    @Override
    public int getItemCount() {
        return hashMap.size();
    }

    class SchemeViewHolder extends RecyclerView.ViewHolder {
        TextView txt_UserName, txt_Date,txt_UserMsg, txt_Count;
        ImageView img_scheme;
        LinearLayout ll_Parent;

        SchemeViewHolder(View itemView) {
            super(itemView);

            txt_UserName = (TextView) itemView.findViewById(R.id.txt_UserName);
            txt_Date = (TextView) itemView.findViewById(R.id.txt_Date);
            txt_UserMsg = itemView.findViewById(R.id.txt_UserMsg);
            txt_Count = itemView.findViewById(R.id.txt_Count);
            img_scheme = itemView.findViewById(R.id.img_scheme);
            ll_Parent = itemView.findViewById(R.id.ll_Parent);
        }
    }
}
