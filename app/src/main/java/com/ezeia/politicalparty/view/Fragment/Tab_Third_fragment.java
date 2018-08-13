package com.ezeia.politicalparty.view.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.ezeia.politicalparty.R;
import com.ezeia.politicalparty.chat.UserDetails;
import com.ezeia.politicalparty.pref.Preferences;
import com.ezeia.politicalparty.utils.Database;
import com.ezeia.politicalparty.utils.Utils;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;

import io.fabric.sdk.android.Fabric;

import static android.app.Activity.RESULT_OK;

public class Tab_Third_fragment extends Fragment {

    boolean isImageFitToScreen = false;

    View view;
    ListView usersList;
    TextView noUsersText;
    ArrayList<String> al = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog pd;

    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton,sendImage;
    TextView txt_Note;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    int RC_PHOTO_PICKER = 100;
    private FirebaseApp app;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;
    boolean isImageLoading = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_chat, container, false);

        if(getActivity() != null)
            Fabric.with(getActivity(), new Crashlytics());

        initViews();
        return view;
    }

    void initViews()
    {
        layout = (LinearLayout) view.findViewById(R.id.layout1);
        layout_2 = (RelativeLayout) view.findViewById(R.id.layout2);
        sendButton = (ImageView) view.findViewById(R.id.sendButton);
        sendImage = view.findViewById(R.id.sendImage);
        messageArea = (EditText) view.findViewById(R.id.messageArea);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        txt_Note = view.findViewById(R.id.txt_Note);

        if(Preferences.getScheme(getActivity()).equals("")
                || Preferences.getScheme(getActivity()).equalsIgnoreCase("None"))
        {
            txt_Note.setText("PLEASE SELECT SCHEME");
        }else {
            txt_Note.setText(Preferences.getScheme(getActivity()));
        }
        callFirebaseUsers();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<>();
                    map.put("message", messageText);
                    map.put("user", UserDetails.username.split(Pattern.quote("-"))[0]);
                    map.put("time", getCurrentTimeStamp());
                    reference1.push().setValue(map);
                    messageArea.setText("");
                    createRequest(UserDetails.username.split(Pattern.quote("-"))[0],messageText);
                    //setFirebase(UserDetails.username.split(Pattern.quote("-"))[0],messageText);
                }
            }
        });

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_PHOTO_PICKER);
            }
        });
    }

    void createRequest(String title,String message)
    {
        JSONObject json=new JSONObject();
        try {
            json.put("to","/topics/news");
            JSONObject userData=new JSONObject();
            userData.put("title",title);
            userData.put("message",message);
            json.put("data",userData);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest("https://fcm.googleapis.com/fcm/send", json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.i("onResponse", "" + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "key=" + Utils.SERVER_KEY);
                    params.put("Content-Type", "application/json");
                    return params;
                }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        requestQueue.add(jsonObjectRequest);

    }

    public static String getCurrentTimeStamp(){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.ENGLISH);
            return dateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            final Uri filePath;
            filePath = data.getData();

            if(filePath != null)
            {
                /*String timeStamp = getCurrentTimeStamp().split(Pattern.quote(" "))[1];
                //Long changedTime = Long.valueOf(Integer.parseInt(currentDate.trim()));
                //String timeStamp = getDateCurrentTimeZone(changedTime);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                    addImageBox(bitmap,1,timeStamp);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }*/

                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                final StorageReference ref = storageRef.child("images/"+ UUID.randomUUID().toString());
                ref.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                //Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Map<String, String> map = new HashMap<>();
                                        map.put("message", uri.toString());
                                        map.put("user", UserDetails.username.split(Pattern.quote("-"))[0]);
                                        map.put("time", getCurrentTimeStamp());
                                        reference1.push().setValue(map);
                                        messageArea.setText("");
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Sorry some error occured.Please try again.", Toast.LENGTH_SHORT).show();
                                Log.d("TAG",e.getMessage());
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploaded "+(int)progress+"%");
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    UploadTask.TaskSnapshot downloadUri = task.getResult();
                                    //String uri = downloadUri.getMetadata().getPath();

                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });
            }
        }
    }

    void callFirebase()
    {
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.show();

        String url = "https://politicalparty-adadb.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
        rQueue.add(request);
    }

    void callFirebaseUsers()
    {
        if(getActivity() != null)
        {
            Firebase.setAndroidContext(getActivity());
            // Get the Firebase app and all primitives we'll use
            app = FirebaseApp.getInstance();
            database = FirebaseDatabase.getInstance(app);
            auth = FirebaseAuth.getInstance(app);
            storage = FirebaseStorage.getInstance(app);
            storageRef = storage.getReference();

            reference1 = new Firebase("https://politicalparty-adadb.firebaseio.com/groups/");
            if(Preferences.getRole(getActivity()).equals("User"))
            {
                reference2 = new Firebase("https://politicalparty-adadb.firebaseio.com/groups/");

                Database database = new Database(getActivity());
                String groupName = database.fetchUserByArea(Preferences.getDistrict(getActivity()),Preferences.getScheme(getActivity()));
                reference1 = new Firebase("https://politicalparty-adadb.firebaseio.com/groups/"+groupName+"-"+Preferences.getDistrict(getActivity())+"-"+Preferences.getScheme(getActivity())+"/messages");
                reference2 = new Firebase("https://politicalparty-adadb.firebaseio.com/groups/"+"Modi-NA"+"-"+Preferences.getScheme(getActivity())+"/messages");
                //reference1.child(groupName+"-"+Preferences.getDistrict(getActivity())+"-"+Preferences.getScheme(getActivity())+"/messages");

            }else if(Preferences.getRole(getActivity()).equals("Super_Admin"))
            {
                reference1 = new Firebase("https://politicalparty-adadb.firebaseio.com/groups/"+UserDetails.username+"-"+"NA"+"-"+Preferences.getScheme(getActivity())+"/messages");

            }else {
                //reference1.child(UserDetails.username+"-"+Preferences.getDistrict(getActivity())+"-"+Preferences.getScheme(getActivity())+"/messages");
                reference2 = new Firebase("https://politicalparty-adadb.firebaseio.com/groups/");

                reference1 = new Firebase("https://politicalparty-adadb.firebaseio.com/groups/"+UserDetails.username+"/messages");
                reference2 = new Firebase("https://politicalparty-adadb.firebaseio.com/groups/"+"Modi-NA"+"-"+Preferences.getScheme(getActivity())+"/messages");

            }

            reference1.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String mapKey = dataSnapshot.getKey();

                    Map map = dataSnapshot.getValue(Map.class);
                    if(map != null)
                    {
                        String timeStamp = "";
                        String message = map.get("message").toString();
                        String userName = map.get("user").toString();
                        if(map.get("time")!= null)
                            timeStamp = map.get("time").toString().trim().split(Pattern.quote(" "))[1];

                        if(Preferences.getRole(getActivity()).equals("District_Admin"))
                        {
                            if(userName.equals(UserDetails.username.split(Pattern.quote("-"))[0]))
                            {
                                if (message.contains("https://firebasestorage.googleapis.com/") || message.startsWith("content://")){
                                    AsyncGettingBitmapFromUrl sync = new AsyncGettingBitmapFromUrl();
                                    sync.execute(message,timeStamp,"1");
                                }else
                                    addMessageBox("You: \n" + message, 1,timeStamp);
                            } else{
                                if (message.contains("https://firebasestorage.googleapis.com/") || message.startsWith("content://")){
                                    AsyncGettingBitmapFromUrl sync = new AsyncGettingBitmapFromUrl();
                                    sync.execute(message,timeStamp,"2");
                                }else
                                    addMessageBox(userName + " -\n" + message, 2,timeStamp);
                            }
                        }else if(Preferences.getRole(getActivity()).equals("Super_Admin"))
                        {
                            if(userName.equals(UserDetails.username.split(Pattern.quote("-"))[0]))
                            {
                                if (message.contains("https://firebasestorage.googleapis.com/") || message.startsWith("content://")){
                                    AsyncGettingBitmapFromUrl sync = new AsyncGettingBitmapFromUrl();
                                    sync.execute(message,timeStamp,"1");
                                }else
                                    addMessageBox("You: \n" + message, 1,timeStamp);
                            }
                        }else{
                            if(userName.equals(UserDetails.username)){
                                if (message.contains("https://firebasestorage.googleapis.com/") || message.startsWith("content://")){
                                    AsyncGettingBitmapFromUrl sync = new AsyncGettingBitmapFromUrl();
                                    sync.execute(message,timeStamp,"1");
                                }else
                                    addMessageBox("You: \n" + message, 1,timeStamp);
                            } else{
                                if (message.contains("https://firebasestorage.googleapis.com/") || message.startsWith("content://")){
                                    AsyncGettingBitmapFromUrl sync = new AsyncGettingBitmapFromUrl();
                                    sync.execute(message,timeStamp,"2");
                                }else
                                    addMessageBox(userName + " -\n" + message, 2,timeStamp);
                            }
                        }
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            if(reference2 != null)
            {
                reference2.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String mapKey = dataSnapshot.getKey();

                        Map map = dataSnapshot.getValue(Map.class);
                        if(map != null) {

                            String message = map.get("message").toString();
                            String userName = map.get("user").toString();
                            String timeStamp = map.get("time").toString().trim().split(Pattern.quote(" "))[1];
                            //Long changedTime = Long.valueOf(Integer.parseInt(currentDate));
                            //String timeStamp = getDateCurrentTimeZone(changedTime);

                            if (message.contains("https://firebasestorage.googleapis.com/") || message.startsWith("content://")){
                                AsyncGettingBitmapFromUrl sync = new AsyncGettingBitmapFromUrl();
                                sync.execute(message,timeStamp,"2");
                            }else
                                addMessageBox(userName + " -\n" + message, 2,timeStamp);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) { }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) { }

                });
            }
        }
    }

    public  String getDateCurrentTimeZone(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private class AsyncGettingBitmapFromUrl extends AsyncTask<String, Void, Bitmap> {

        String timeStamp = "";
        String type = "";

        @Override
        protected Bitmap doInBackground(String... params) {

            isImageLoading = true;
            Bitmap myBitmap = null;
            timeStamp = params[1];
            type = params[2];
            //bitmap = AppMethods.downloadImage(params[0]);
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            System.out.println("bitmap" + bitmap);
            addImageBox(bitmap, Integer.parseInt(type),timeStamp);
            isImageLoading = false;
        }
    }

    public void doOnSuccess(String s){
        try {
            JSONObject obj = new JSONObject(s);
            ArrayList<String> arrayList = new ArrayList<>();
            Iterator i = obj.keys();
            String key = "";
            al.clear();

            String districtName = Preferences.getDistrict(getActivity());
            String role = Preferences.getRole(getActivity());
            Database database = new Database(getActivity());

            if(!role.equals("user"));
            {
               // arrayList = database.fetchUserWithDistrict(districtName,"user");
            }

            while(i.hasNext()){
                key = i.next().toString();

                if(arrayList.contains(key)) {
                    al.add(key);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void addMessageBox(String message, int type, String timeStamp){

        if(getActivity() != null)
        {
            LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp4.setMargins(0,5,0,0);
            if(type == 1) {
                lp4.gravity = Gravity.RIGHT;
            }else{
                lp4.gravity = Gravity.LEFT;
            }
            linearLayout.setLayoutParams(lp4);

            TextView textView = new TextView(getActivity());
            textView.setText(message);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if(type == 1) {
                textView.setTextColor(Color.parseColor("#FFFFFF"));
                textView.setBackgroundResource(R.drawable.rounded_corner_dark);
            }
            else{
                textView.setTextColor(Color.parseColor("#000000"));
                textView.setBackgroundResource(R.drawable.rounded_corner_light);
            }
            textView.setLayoutParams(lp2);
            linearLayout.addView(textView);

            TextView textTime = new TextView(getActivity());
            textTime.setText(timeStamp);
            textTime.setTextSize(11);
            textTime.setGravity(Gravity.BOTTOM);
            LinearLayout.LayoutParams lp5 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            textTime.setLayoutParams(lp5);
            linearLayout.addView(textTime);

            layout.addView(linearLayout);
            scrollView.fullScroll(View.FOCUS_DOWN);

      /*  ImageView image = new ImageView(getActivity());
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp3.weight = 1.0f;
        if(type == 1) {
            lp3.gravity = Gravity.LEFT;
            image.setBackgroundResource(R.drawable.speech_bubble_green);
        }
        else{
            lp3.gravity = Gravity.RIGHT;
            image.setBackgroundResource(R.drawable.speech_bubble_orange);
        }
        image.setLayoutParams(lp3);
        layout.addView(image);

        if (message.contains("https://firebasestorage.googleapis.com/") || message.startsWith("content://")) {
            textView.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
            Glide.with(getActivity())
                    .load(message)
                    .listener(new RequestListener<Drawable>() {
                                  @Override
                                  public boolean onLoadFailed(@android.support.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Toast.makeText(getActivity().getApplicationContext(),"Failed "+e.getMessage(),Toast.LENGTH_SHORT).show();
                                        Log.d("TAG",e.getMessage());
                                      return false;
                                  }

                                  @Override
                                  public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                      Toast.makeText(getActivity().getApplicationContext(),"Success ",Toast.LENGTH_SHORT).show();
                                      return false;
                                  }
                              }
                    )
                    .into(image);
        } else {
            textView.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
            textView.setText(message);
        }*/
        }
    }

    public void addImageBox(Bitmap message,int type, String timeStamp){

        if(getActivity() != null){
            LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp4.setMargins(0,5,0,0);
            if(type == 1) {
                lp4.gravity = Gravity.RIGHT;
            }else{
                lp4.gravity = Gravity.LEFT;
            }
            linearLayout.setLayoutParams(lp4);

            final ImageView image = new ImageView(getActivity());
            LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(200,400);
            lp3.setMargins(0,5,0,0);
            if(type == 1) {
                image.setBackgroundResource(R.drawable.rounded_corner_dark);
            }
            else{
                image.setBackgroundResource(R.drawable.rounded_corner_light);
            }
            image.setLayoutParams(lp3);
            image.setImageBitmap(message);
            linearLayout.addView(image);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(isImageFitToScreen) {
                        isImageFitToScreen=false;
                        image.setLayoutParams(new LinearLayout.LayoutParams(200, 400));
                        image.setAdjustViewBounds(true);
                    }else{
                        isImageFitToScreen=true;
                        image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        image.setScaleType(ImageView.ScaleType.FIT_XY);
                    }
                }
            });

            TextView textTime = new TextView(getActivity());
            textTime.setText(timeStamp);
            textTime.setTextSize(11);
            textTime.setGravity(Gravity.BOTTOM);
            LinearLayout.LayoutParams lp5 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            textTime.setLayoutParams(lp5);
            linearLayout.addView(textTime);

            layout.addView(linearLayout);

            scrollView.fullScroll(View.FOCUS_DOWN);

        }
    }

}


