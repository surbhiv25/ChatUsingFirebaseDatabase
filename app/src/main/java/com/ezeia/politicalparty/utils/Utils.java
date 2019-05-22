package com.ezeia.politicalparty.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ezeia.politicalparty.R;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Utils {

    //Email Validation pattern
    public static final String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";

    public static final String SERVER_KEY = "Your firebase server key";
    //Fragments Tags
    public static final String Login_Fragment = "Login_Fragment";
    public static final String SignUp_Fragment = "SignUp_Fragment";
    public static final String ForgotPassword_Fragment = "ForgotPassword_Fragment";

    public static final int[] myImageList = new int[]{R.drawable.beti_bachao, R.drawable.pradhan_mantri_yojna, R.drawable.clean_ganga};

    public static ArrayList<String> getSchemesInfoList()
    {
        ArrayList<String> list = new ArrayList<>();
        list.add("Beti Bachao, Beti Padhao (translation: Save girl child, educate a girl child) is a personal " +
                "campaign of the Government of India that aims to generate awareness and improve the efficiency " +
                "of welfare services intended for girls. The scheme was launched with an initial funding of ₹100 crore " +
                "(US$15 million).It mainly targets the clusters in Uttar Pradesh, Haryana, Uttarakhand, " +
                "Punjab, Bihar and Delhi.");

        list.add("Pradhan Mantri Awas Yojana (PMAY) is an initiative by Government of India in which affordable " +
                "housing will be provided to the urban poor with a target of building 20 million affordable houses " +
                "by 31 March 2022.It has two components: Pradhan Mantri Awas Yojana (Urban) (PMAY-U) for the " +
                "urban poor and Pradhan Mantri Awaas Yojana (Gramin) (PMAY-G and also PMAY-R) for the rural poor.");

        list.add("The National Mission for Clean Ganga (NMCG) is the implementation wing of National Ganga " +
                "Council which was set up in October 2016 under the River Ganga (Rejuvenation, Protection and Management) " +
                "Authorities order 2016. The order dissolved National Ganga River Basin Authority." +
                " The aim is to clean the Ganga and its tributaries in a comprehensive manner.");

        return list;
    }

    public static LinkedHashMap<String,String> insertData(Context ctx)
    {
        LinkedHashMap<String,String> hmap = new LinkedHashMap<>();
        hmap.put("00"+"^"+"Modi Ji","123456"+"^"+"None"+"^"+"None"+"^"+"Super_Admin"+"^"+"00"+"^"+"1");

        hmap.put("11"+"^"+"District4","123456"+"^"+"West Delhi"+"^"+"Beti Bachao Beti Padhao"+"^"+"District_Admin"+"^"+"10:30 am"+"^"+"1");
        hmap.put("12"+"^"+"District5","123456"+"^"+"West Delhi"+"^"+"Pradhan Mantri Awas Yojna"+"^"+"District_Admin"+"^"+"10:30 am"+"^"+"1");
        hmap.put("13"+"^"+"District6","123456"+"^"+"West Delhi"+"^"+"Clean Ganga"+"^"+"District_Admin"+"^"+"10:30 am"+"^"+"1");

        hmap.put("14"+"^"+"User4","123456"+"^"+"West Delhi"+"^"+"None"+"^"+"User"+"^"+"10:30 am"+"^"+"1");
        hmap.put("15"+"^"+"User5","123456"+"^"+"West Delhi"+"^"+"None"+"^"+"User"+"^"+"10:30 am"+"^"+"1");
        hmap.put("16"+"^"+"User6","123456"+"^"+"West Delhi"+"^"+"None"+"^"+"User"+"^"+"10:30 am"+"^"+"1");

        hmap.put("21"+"^"+"District1","123456"+"^"+"Gurugram"+"^"+"Beti Bachao Beti Padhao"+"^"+"District_Admin"+"^"+"10:30 am"+"^"+"1");
        hmap.put("22"+"^"+"District2","123456"+"^"+"Gurugram"+"^"+"Pradhan Mantri Awas Yojna"+"^"+"District_Admin"+"^"+"10:30 am"+"^"+"1");
        hmap.put("23"+"^"+"District3","123456"+"^"+"Gurugram"+"^"+"Clean Ganga"+"^"+"District_Admin"+"^"+"10:30 am"+"^"+"1");

        hmap.put("24"+"^"+"User1","123456"+"^"+"Gurugram"+"^"+"None"+"^"+"User"+"^"+"10:30 am"+"^"+"1");
        hmap.put("25"+"^"+"User2","123456"+"^"+"Gurugram"+"^"+"None"+"^"+"User"+"^"+"10:30 am"+"^"+"1");
        hmap.put("26"+"^"+"User3","123456"+"^"+"Gurugram"+"^"+"None"+"^"+"User"+"^"+"10:30 am"+"^"+"1");

        return  hmap;
    }

    public static void insertDataDb(Context ctx)
    {
        Database database = new Database(ctx);
        database.open();
        database.Delete_tblData();

        LinkedHashMap<String,String> hmap = insertData(ctx);

        for(Map.Entry<String,String> entry: hmap.entrySet()){

            String userID = entry.getKey().split(Pattern.quote("^"))[0];
            String userName = entry.getKey().split(Pattern.quote("^"))[1];

            String password = entry.getValue().split(Pattern.quote("^"))[0];
            String district = entry.getValue().split(Pattern.quote("^"))[1];
            String scheme = entry.getValue().split(Pattern.quote("^"))[2];
            String role = entry.getValue().split(Pattern.quote("^"))[3];
            String loginTime = entry.getValue().split(Pattern.quote("^"))[4];
            String isInstalled = entry.getValue().split(Pattern.quote("^"))[5];

            database.saveUserDetails(userID,userName,password,district,scheme,role,loginTime,isInstalled);
        }

        database.saveSchemes("101","Beti Bachao Beti Padhao");
        database.saveSchemes("102","Pradhan Mantri Awas Yojna");
        database.saveSchemes("103","Clean Ganga");
        database.close();
    }

    public static void callFirebaseToSaveData(final Context ctx)
    {
        String url = "https://politicalparty-adadb.firebaseio.com/users.json";

        final LinkedHashMap<String,String> hmap = insertData(ctx);

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("Loading...");
        pd.show();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase("https://politicalparty-adadb.firebaseio.com/users");

                if(s.equals("null"))
                {
                    String firebaseUser = "";

                    for(Map.Entry<String,String> entry: hmap.entrySet()){

                        String userID = entry.getKey().split(Pattern.quote("^"))[0];
                        String userName = entry.getKey().split(Pattern.quote("^"))[1];

                        String password = entry.getValue().split(Pattern.quote("^"))[0];
                        String district = entry.getValue().split(Pattern.quote("^"))[1];
                        String scheme = entry.getValue().split(Pattern.quote("^"))[2];
                        String role = entry.getValue().split(Pattern.quote("^"))[3];
                        String loginTime = entry.getValue().split(Pattern.quote("^"))[4];
                        String isInstalled = entry.getValue().split(Pattern.quote("^"))[5];

                        /*if(role.equals("District_Admin"))
                        {
                            firebaseUser = userName+"-"+district+"-"+scheme;
                        }else{
                            firebaseUser = userName;
                        }*/

                        firebaseUser = userName;

                        reference.child(firebaseUser).child("password").setValue(password);
                    }
                    //Toast.makeText(ctx, "registration successful", Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        JSONObject obj = new JSONObject(s);

                        String firebaseUser = "";

                        for(Map.Entry<String,String> entry: hmap.entrySet()){

                            String userID = entry.getKey().split(Pattern.quote("^"))[0];
                            String userName = entry.getKey().split(Pattern.quote("^"))[1];

                            String password = entry.getValue().split(Pattern.quote("^"))[0];
                            String district = entry.getValue().split(Pattern.quote("^"))[1];
                            String scheme = entry.getValue().split(Pattern.quote("^"))[2];
                            String role = entry.getValue().split(Pattern.quote("^"))[3];
                            String loginTime = entry.getValue().split(Pattern.quote("^"))[4];
                            String isInstalled = entry.getValue().split(Pattern.quote("^"))[5];

                            if(role.equals("District_Admin"))
                            {
                                firebaseUser = userName+"-"+district+"-"+scheme;
                            }else{
                                firebaseUser = userName;
                            }

                            if (!obj.has(firebaseUser)) {
                                reference.child(firebaseUser).child("password").setValue(password);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    pd.dismiss();
                }
            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError );
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(ctx);
        rQueue.add(request);
    }

    public static void callFirebaseToSaveGroups(final Context ctx)
    {
        String url = "https://politicalparty-adadb.firebaseio.com/groups.json";

        final Database database = new Database(ctx);
        final LinkedHashMap<String,String> hmap = database.fetchGroupsList();

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("Loading...");
        pd.show();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase("https://politicalparty-adadb.firebaseio.com/groups");

                if(s.equals("null"))
                {
                    String firebaseUser = "";

                    for(Map.Entry<String,String> entry: hmap.entrySet()){

                        String userName = entry.getKey();

                        String district = entry.getValue().split(Pattern.quote("^"))[0];
                        String scheme = entry.getValue().split(Pattern.quote("^"))[1];

                        firebaseUser = userName+"-"+district+"-"+scheme;
                        reference.child(firebaseUser).child("messages").setValue("");
                    }
                }
                else {
                    try {
                        JSONObject obj = new JSONObject(s);

                        String firebaseUser = "";

                        for(Map.Entry<String,String> entry: hmap.entrySet()){

                            String userName = entry.getKey();

                            String district = entry.getValue().split(Pattern.quote("^"))[0];
                            String scheme = entry.getValue().split(Pattern.quote("^"))[1];

                            firebaseUser = userName+"-"+district+"-"+scheme;
                            reference.child(firebaseUser).child("messages").setValue("");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    pd.dismiss();
                }
            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError );
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(ctx);
        rQueue.add(request);
    }

    public static final String jsonObject ="{\n" +
            "    \"Tripura\": [\n" +
            "        \"Dhalai\",\n" +
            "        \"Gomati\",\n" +
            "        \"Khowai\",\n" +
            "        \"North Tripura\",\n" +
            "        \"Sepahijala\",\n" +
            "        \"South Tripura\",\n" +
            "        \"Unakoti\",\n" +
            "        \"West Tripura\"\n" +
            "    ],\n" +
            "    \"Lakshadweep (UT)\": [\n" +
            "        \"Lakshadweep\"\n" +
            "    ],\n" +
            "    \"Daman and Diu (UT)\": [\n" +
            "        \"Daman\",\n" +
            "        \"Diu\"\n" +
            "    ],\n" +
            "    \"Mizoram\": [\n" +
            "        \"Aizawl\",\n" +
            "        \"Champhai\",\n" +
            "        \"Kolasib\",\n" +
            "        \"Lawngtlai\",\n" +
            "        \"Lunglei\",\n" +
            "        \"Mamit\",\n" +
            "        \"Saiha\",\n" +
            "        \"Serchhip\"\n" +
            "    ],\n" +
            "    \"Kerala\": [\n" +
            "        \"Alappuzha\",\n" +
            "        \"Ernakulam\",\n" +
            "        \"Idukki\",\n" +
            "        \"Kannur\",\n" +
            "        \"Kasaragod\",\n" +
            "        \"Kollam\",\n" +
            "        \"Kottayam\",\n" +
            "        \"Kozhikode\",\n" +
            "        \"Malappuram\",\n" +
            "        \"Palakkad\",\n" +
            "        \"Pathanamthitta\",\n" +
            "        \"Thiruvananthapuram\",\n" +
            "        \"Thrissur\",\n" +
            "        \"Wayanad\"\n" +
            "    ],\n" +
            "    \"Jammu and Kashmir\": [\n" +
            "        \"Anantnag\",\n" +
            "        \"Bandipore\",\n" +
            "        \"Baramulla\",\n" +
            "        \"Budgam\",\n" +
            "        \"Doda\",\n" +
            "        \"Ganderbal\",\n" +
            "        \"Jammu\",\n" +
            "        \"Kargil\",\n" +
            "        \"Kathua\",\n" +
            "        \"Kishtwar\",\n" +
            "        \"Kulgam\",\n" +
            "        \"Kupwara\",\n" +
            "        \"Leh\",\n" +
            "        \"Poonch\",\n" +
            "        \"Pulwama\",\n" +
            "        \"Rajouri\",\n" +
            "        \"Ramban\",\n" +
            "        \"Reasi\",\n" +
            "        \"Samba\",\n" +
            "        \"Shopian\",\n" +
            "        \"Srinagar\",\n" +
            "        \"Udhampur\"\n" +
            "    ],\n" +
            "    \"West Bengal\": [\n" +
            "        \"Alipurduar\",\n" +
            "        \"Bankura\",\n" +
            "        \"Birbhum\",\n" +
            "        \"Burdwan (Bardhaman)\",\n" +
            "        \"Cooch Behar\",\n" +
            "        \"Dakshin Dinajpur (South Dinajpur)\",\n" +
            "        \"Darjeeling\",\n" +
            "        \"Hooghly\",\n" +
            "        \"Howrah\",\n" +
            "        \"Jalpaiguri\",\n" +
            "        \"Kalimpong\",\n" +
            "        \"Kolkata\",\n" +
            "        \"Malda\",\n" +
            "        \"Murshidabad\",\n" +
            "        \"Nadia\",\n" +
            "        \"North 24 Parganas\",\n" +
            "        \"Paschim Medinipur (West Medinipur)\",\n" +
            "        \"Purba Medinipur (East Medinipur)\",\n" +
            "        \"Purulia\",\n" +
            "        \"South 24 Parganas\",\n" +
            "        \"Uttar Dinajpur (North Dinajpur)\"\n" +
            "    ],\n" +
            "    \"Haryana\": [\n" +
            "        \"Ambala\",\n" +
            "        \"Bhiwani\",\n" +
            "        \"Charkhi Dadri\",\n" +
            "        \"Faridabad\",\n" +
            "        \"Fatehabad\",\n" +
            "        \"Gurugram (Gurgaon)\",\n" +
            "        \"Hisar\",\n" +
            "        \"Jhajjar\",\n" +
            "        \"Jind\",\n" +
            "        \"Kaithal\",\n" +
            "        \"Karnal\",\n" +
            "        \"Kurukshetra\",\n" +
            "        \"Mahendragarh\",\n" +
            "        \"Nuh\",\n" +
            "        \"Palwal\",\n" +
            "        \"Panchkula\",\n" +
            "        \"Panipat\",\n" +
            "        \"Rewari\",\n" +
            "        \"Rohtak\",\n" +
            "        \"Sirsa\",\n" +
            "        \"Sonipat\",\n" +
            "        \"Yamunanagar\"\n" +
            "    ],\n" +
            "    \"Bihar\": [\n" +
            "        \"Araria\",\n" +
            "        \"Arwal\",\n" +
            "        \"Aurangabad\",\n" +
            "        \"Banka\",\n" +
            "        \"Begusarai\",\n" +
            "        \"Bhagalpur\",\n" +
            "        \"Bhojpur\",\n" +
            "        \"Buxar\",\n" +
            "        \"Darbhanga\",\n" +
            "        \"East Champaran (Motihari)\",\n" +
            "        \"Gaya\",\n" +
            "        \"Gopalganj\",\n" +
            "        \"Jamui\",\n" +
            "        \"Jehanabad\",\n" +
            "        \"Kaimur (Bhabua)\",\n" +
            "        \"Katihar\",\n" +
            "        \"Khagaria\",\n" +
            "        \"Kishanganj\",\n" +
            "        \"Lakhisarai\",\n" +
            "        \"Madhepura\",\n" +
            "        \"Madhubani\",\n" +
            "        \"Munger (Monghyr)\",\n" +
            "        \"Muzaffarpur\",\n" +
            "        \"Nalanda\",\n" +
            "        \"Nawada\",\n" +
            "        \"Patna\",\n" +
            "        \"Purnia (Purnea)\",\n" +
            "        \"Rohtas\",\n" +
            "        \"Saharsa\",\n" +
            "        \"Samastipur\",\n" +
            "        \"Saran\",\n" +
            "        \"Sheikhpura\",\n" +
            "        \"Sheohar\",\n" +
            "        \"Sitamarhi\",\n" +
            "        \"Siwan\",\n" +
            "        \"Supaul\",\n" +
            "        \"Vaishali\",\n" +
            "        \"West Champaran\"\n" +
            "    ],\n" +
            "    \"Karnataka\": [\n" +
            "        \"Bagalkot\",\n" +
            "        \"Ballari (Bellary)\",\n" +
            "        \"Belagavi (Belgaum)\",\n" +
            "        \"Bengaluru (Bangalore) Rural\",\n" +
            "        \"Bengaluru (Bangalore) Urban\",\n" +
            "        \"Bidar\",\n" +
            "        \"Chamarajanagar\",\n" +
            "        \"Chikballapur\",\n" +
            "        \"Chikkamagaluru (Chikmagalur)\",\n" +
            "        \"Chitradurga\",\n" +
            "        \"Dakshina Kannada\",\n" +
            "        \"Davangere\",\n" +
            "        \"Dharwad\",\n" +
            "        \"Gadag\",\n" +
            "        \"Hassan\",\n" +
            "        \"Haveri\",\n" +
            "        \"Kalaburagi (Gulbarga)\",\n" +
            "        \"Kodagu\",\n" +
            "        \"Kolar\",\n" +
            "        \"Koppal\",\n" +
            "        \"Mandya\",\n" +
            "        \"Mysuru (Mysore)\",\n" +
            "        \"Raichur\",\n" +
            "        \"Ramanagara\",\n" +
            "        \"Shivamogga (Shimoga)\",\n" +
            "        \"Tumakuru (Tumkur)\",\n" +
            "        \"Udupi\",\n" +
            "        \"Uttara Kannada (Karwar)\",\n" +
            "        \"Vijayapura (Bijapur)\",\n" +
            "        \"Yadgir\"\n" +
            "    ],\n" +
            "    \"Nagaland\": [\n" +
            "        \"Dimapur\",\n" +
            "        \"Kiphire\",\n" +
            "        \"Kohima\",\n" +
            "        \"Longleng\",\n" +
            "        \"Mokokchung\",\n" +
            "        \"Mon\",\n" +
            "        \"Peren\",\n" +
            "        \"Phek\",\n" +
            "        \"Tuensang\",\n" +
            "        \"Wokha\",\n" +
            "        \"Zunheboto\"\n" +
            "    ],\n" +
            "    \"Assam\": [\n" +
            "        \"Baksa\",\n" +
            "        \"Barpeta\",\n" +
            "        \"Biswanath\",\n" +
            "        \"Bongaigaon\",\n" +
            "        \"Cachar\",\n" +
            "        \"Charaideo\",\n" +
            "        \"Chirang\",\n" +
            "        \"Darrang\",\n" +
            "        \"Dhemaji\",\n" +
            "        \"Dhubri\",\n" +
            "        \"Dibrugarh\",\n" +
            "        \"Dima Hasao (North Cachar Hills)\",\n" +
            "        \"Goalpara\",\n" +
            "        \"Golaghat\",\n" +
            "        \"Hailakandi\",\n" +
            "        \"Hojai\",\n" +
            "        \"Jorhat\",\n" +
            "        \"Kamrup\",\n" +
            "        \"Kamrup Metropolitan\",\n" +
            "        \"Karbi Anglong\",\n" +
            "        \"Karimganj\",\n" +
            "        \"Kokrajhar\",\n" +
            "        \"Lakhimpur\",\n" +
            "        \"Majuli\",\n" +
            "        \"Morigaon\",\n" +
            "        \"Nagaon\",\n" +
            "        \"Nalbari\",\n" +
            "        \"Sivasagar\",\n" +
            "        \"Sonitpur\",\n" +
            "        \"South Salamara-Mankachar\",\n" +
            "        \"Tinsukia\",\n" +
            "        \"Udalguri\",\n" +
            "        \"West Karbi Anglong\"\n" +
            "    ],\n" +
            "    \"Rajasthan\": [\n" +
            "        \"Ajmer\",\n" +
            "        \"Alwar\",\n" +
            "        \"Banswara\",\n" +
            "        \"Baran\",\n" +
            "        \"Barmer\",\n" +
            "        \"Bharatpur\",\n" +
            "        \"Bhilwara\",\n" +
            "        \"Bikaner\",\n" +
            "        \"Bundi\",\n" +
            "        \"Chittorgarh\",\n" +
            "        \"Churu\",\n" +
            "        \"Dausa\",\n" +
            "        \"Dholpur\",\n" +
            "        \"Dungarpur\",\n" +
            "        \"Hanumangarh\",\n" +
            "        \"Jaipur\",\n" +
            "        \"Jaisalmer\",\n" +
            "        \"Jalore\",\n" +
            "        \"Jhalawar\",\n" +
            "        \"Jhunjhunu\",\n" +
            "        \"Jodhpur\",\n" +
            "        \"Karauli\",\n" +
            "        \"Kota\",\n" +
            "        \"Nagaur\",\n" +
            "        \"Pali\",\n" +
            "        \"Pratapgarh\",\n" +
            "        \"Rajsamand\",\n" +
            "        \"Sawai Madhopur\",\n" +
            "        \"Sikar\",\n" +
            "        \"Sirohi\",\n" +
            "        \"Sri Ganganagar\",\n" +
            "        \"Tonk\",\n" +
            "        \"Udaipur\"\n" +
            "    ],\n" +
            "    \"Punjab\": [\n" +
            "        \"Amritsar\",\n" +
            "        \"Barnala\",\n" +
            "        \"Bathinda\",\n" +
            "        \"Faridkot\",\n" +
            "        \"Fatehgarh Sahib\",\n" +
            "        \"Fazilka\",\n" +
            "        \"Ferozepur\",\n" +
            "        \"Gurdaspur\",\n" +
            "        \"Hoshiarpur\",\n" +
            "        \"Jalandhar\",\n" +
            "        \"Kapurthala\",\n" +
            "        \"Ludhiana\",\n" +
            "        \"Mansa\",\n" +
            "        \"Moga\",\n" +
            "        \"Muktsar\",\n" +
            "        \"Nawanshahr (Shahid Bhagat Singh Nagar)\",\n" +
            "        \"Pathankot\",\n" +
            "        \"Patiala\",\n" +
            "        \"Rupnagar\",\n" +
            "        \"Sahibzada Ajit Singh Nagar (Mohali)\",\n" +
            "        \"Sangrur\",\n" +
            "        \"Tarn Taran\"\n" +
            "    ],\n" +
            "    \"Himachal Pradesh\": [\n" +
            "        \"Bilaspur\",\n" +
            "        \"Chamba\",\n" +
            "        \"Hamirpur\",\n" +
            "        \"Kangra\",\n" +
            "        \"Kinnaur\",\n" +
            "        \"Kullu\",\n" +
            "        \"Lahaul & Spiti\",\n" +
            "        \"Mandi\",\n" +
            "        \"Shimla\",\n" +
            "        \"Sirmaur (Sirmour)\",\n" +
            "        \"Solan\",\n" +
            "        \"Una\"\n" +
            "    ],\n" +
            "    \"Delhi (NCT)\": [\n" +
            "        \"Central Delhi\",\n" +
            "        \"East Delhi\",\n" +
            "        \"New Delhi\",\n" +
            "        \"North Delhi\",\n" +
            "        \"North East  Delhi\",\n" +
            "        \"North West  Delhi\",\n" +
            "        \"Shahdara\",\n" +
            "        \"South Delhi\",\n" +
            "        \"South East Delhi\",\n" +
            "        \"South West  Delhi\",\n" +
            "        \"West Delhi\"\n" +
            "    ],\n" +
            "    \"Goa\": [\n" +
            "        \"North Goa\",\n" +
            "        \"South Goa\"\n" +
            "    ],\n" +
            "    \"Sikkim\": [\n" +
            "        \"East Sikkim\",\n" +
            "        \"North Sikkim\",\n" +
            "        \"South Sikkim\",\n" +
            "        \"West Sikkim\"\n" +
            "    ],\n" +
            "    \"Meghalaya\": [\n" +
            "        \"East Garo Hills\",\n" +
            "        \"East Jaintia Hills\",\n" +
            "        \"East Khasi Hills\",\n" +
            "        \"North Garo Hills\",\n" +
            "        \"Ri Bhoi\",\n" +
            "        \"South Garo Hills\",\n" +
            "        \"South West Garo Hills \",\n" +
            "        \"South West Khasi Hills\",\n" +
            "        \"West Garo Hills\",\n" +
            "        \"West Jaintia Hills\",\n" +
            "        \"West Khasi Hills\"\n" +
            "    ],\n" +
            "    \"Telangana\": [\n" +
            "        \"Adilabad\",\n" +
            "        \"Bhadradri Kothagudem\",\n" +
            "        \"Hyderabad\",\n" +
            "        \"Jagtial\",\n" +
            "        \"Jangaon\",\n" +
            "        \"Jayashankar Bhoopalpally\",\n" +
            "        \"Jogulamba Gadwal\",\n" +
            "        \"Kamareddy\",\n" +
            "        \"Karimnagar\",\n" +
            "        \"Khammam\",\n" +
            "        \"Komaram Bheem Asifabad\",\n" +
            "        \"Mahabubabad\",\n" +
            "        \"Mahabubnagar\",\n" +
            "        \"Mancherial\",\n" +
            "        \"Medak\",\n" +
            "        \"Medchal\",\n" +
            "        \"Nagarkurnool\",\n" +
            "        \"Nalgonda\",\n" +
            "        \"Nirmal\",\n" +
            "        \"Nizamabad\",\n" +
            "        \"Peddapalli\",\n" +
            "        \"Rajanna Sircilla\",\n" +
            "        \"Rangareddy\",\n" +
            "        \"Sangareddy\",\n" +
            "        \"Siddipet\",\n" +
            "        \"Suryapet\",\n" +
            "        \"Vikarabad\",\n" +
            "        \"Wanaparthy\",\n" +
            "        \"Warangal (Rural)\",\n" +
            "        \"Warangal (Urban)\",\n" +
            "        \"Yadadri Bhuvanagiri\"\n" +
            "    ],\n" +
            "    \"Jharkhand\": [\n" +
            "        \"Bokaro\",\n" +
            "        \"Chatra\",\n" +
            "        \"Deoghar\",\n" +
            "        \"Dhanbad\",\n" +
            "        \"Dumka\",\n" +
            "        \"East Singhbhum\",\n" +
            "        \"Garhwa\",\n" +
            "        \"Giridih\",\n" +
            "        \"Godda\",\n" +
            "        \"Gumla\",\n" +
            "        \"Hazaribag\",\n" +
            "        \"Jamtara\",\n" +
            "        \"Khunti\",\n" +
            "        \"Koderma\",\n" +
            "        \"Latehar\",\n" +
            "        \"Lohardaga\",\n" +
            "        \"Pakur\",\n" +
            "        \"Palamu\",\n" +
            "        \"Ramgarh\",\n" +
            "        \"Ranchi\",\n" +
            "        \"Sahibganj\",\n" +
            "        \"Seraikela-Kharsawan\",\n" +
            "        \"Simdega\",\n" +
            "        \"West Singhbhum\"\n" +
            "    ],\n" +
            "    \"Manipur\": [\n" +
            "        \"Bishnupur\",\n" +
            "        \"Chandel\",\n" +
            "        \"Churachandpur\",\n" +
            "        \"Imphal East\",\n" +
            "        \"Imphal West\",\n" +
            "        \"Jiribam\",\n" +
            "        \"Kakching\",\n" +
            "        \"Kamjong\",\n" +
            "        \"Kangpokpi\",\n" +
            "        \"Noney\",\n" +
            "        \"Pherzawl\",\n" +
            "        \"Senapati\",\n" +
            "        \"Tamenglong\",\n" +
            "        \"Tengnoupal\",\n" +
            "        \"Thoubal\",\n" +
            "        \"Ukhrul\"\n" +
            "    ],\n" +
            "    \"Andhra Pradesh\": [\n" +
            "        \"Anantapur\",\n" +
            "        \"Chittoor\",\n" +
            "        \"East Godavari\",\n" +
            "        \"Guntur\",\n" +
            "        \"Krishna\",\n" +
            "        \"Kurnool\",\n" +
            "        \"Prakasam\",\n" +
            "        \"Srikakulam\",\n" +
            "        \"Sri Potti Sriramulu Nellore\",\n" +
            "        \"Visakhapatnam\",\n" +
            "        \"Vizianagaram\",\n" +
            "        \"West Godavari\",\n" +
            "        \"YSR District, Kadapa (Cuddapah)\"\n" +
            "    ],\n" +
            "    \"Gujarat\": [\n" +
            "        \"Ahmedabad\",\n" +
            "        \"Amreli\",\n" +
            "        \"Anand\",\n" +
            "        \"Aravalli\",\n" +
            "        \"Banaskantha (Palanpur)\",\n" +
            "        \"Bharuch\",\n" +
            "        \"Bhavnagar\",\n" +
            "        \"Botad\",\n" +
            "        \"Chhota Udepur\",\n" +
            "        \"Dahod\",\n" +
            "        \"Dangs (Ahwa)\",\n" +
            "        \"Devbhoomi Dwarka\",\n" +
            "        \"Gandhinagar\",\n" +
            "        \"Gir Somnath\",\n" +
            "        \"Jamnagar\",\n" +
            "        \"Junagadh\",\n" +
            "        \"Kachchh\",\n" +
            "        \"Kheda (Nadiad)\",\n" +
            "        \"Mahisagar\",\n" +
            "        \"Mehsana\",\n" +
            "        \"Morbi\",\n" +
            "        \"Narmada (Rajpipla)\",\n" +
            "        \"Navsari\",\n" +
            "        \"Panchmahal (Godhra)\",\n" +
            "        \"Patan\",\n" +
            "        \"Porbandar\",\n" +
            "        \"Rajkot\",\n" +
            "        \"Sabarkantha (Himmatnagar)\",\n" +
            "        \"Surat\",\n" +
            "        \"Surendranagar\",\n" +
            "        \"Tapi (Vyara)\",\n" +
            "        \"Vadodara\",\n" +
            "        \"Valsad\"\n" +
            "    ],\n" +
            "    \"Dadra and Nagar Haveli (UT)\": [\n" +
            "        \"Dadra & Nagar Haveli\"\n" +
            "    ],\n" +
            "    \"Madhya Pradesh\": [\n" +
            "        \"Agar Malwa\",\n" +
            "        \"Alirajpur\",\n" +
            "        \"Anuppur\",\n" +
            "        \"Ashoknagar\",\n" +
            "        \"Balaghat\",\n" +
            "        \"Barwani\",\n" +
            "        \"Betul\",\n" +
            "        \"Bhind\",\n" +
            "        \"Bhopal\",\n" +
            "        \"Burhanpur\",\n" +
            "        \"Chhatarpur\",\n" +
            "        \"Chhindwara\",\n" +
            "        \"Damoh\",\n" +
            "        \"Datia\",\n" +
            "        \"Dewas\",\n" +
            "        \"Dhar\",\n" +
            "        \"Dindori\",\n" +
            "        \"Guna\",\n" +
            "        \"Gwalior\",\n" +
            "        \"Harda\",\n" +
            "        \"Hoshangabad\",\n" +
            "        \"Indore\",\n" +
            "        \"Jabalpur\",\n" +
            "        \"Jhabua\",\n" +
            "        \"Katni\",\n" +
            "        \"Khandwa\",\n" +
            "        \"Khargone\",\n" +
            "        \"Mandla\",\n" +
            "        \"Mandsaur\",\n" +
            "        \"Morena\",\n" +
            "        \"Narsinghpur\",\n" +
            "        \"Neemuch\",\n" +
            "        \"Panna\",\n" +
            "        \"Raisen\",\n" +
            "        \"Rajgarh\",\n" +
            "        \"Ratlam\",\n" +
            "        \"Rewa\",\n" +
            "        \"Sagar\",\n" +
            "        \"Satna\",\n" +
            "        \"Sehore\",\n" +
            "        \"Seoni\",\n" +
            "        \"Shahdol\",\n" +
            "        \"Shajapur\",\n" +
            "        \"Sheopur\",\n" +
            "        \"Shivpuri\",\n" +
            "        \"Sidhi\",\n" +
            "        \"Singrauli\",\n" +
            "        \"Tikamgarh\",\n" +
            "        \"Ujjain\",\n" +
            "        \"Umaria\",\n" +
            "        \"Vidisha\"\n" +
            "    ],\n" +
            "    \"Tamil Nadu\": [\n" +
            "        \"Ariyalur\",\n" +
            "        \"Chennai\",\n" +
            "        \"Coimbatore\",\n" +
            "        \"Cuddalore\",\n" +
            "        \"Dharmapuri\",\n" +
            "        \"Dindigul\",\n" +
            "        \"Erode\",\n" +
            "        \"Kanchipuram\",\n" +
            "        \"Kanyakumari\",\n" +
            "        \"Karur\",\n" +
            "        \"Krishnagiri\",\n" +
            "        \"Madurai\",\n" +
            "        \"Nagapattinam\",\n" +
            "        \"Namakkal\",\n" +
            "        \"Nilgiris\",\n" +
            "        \"Perambalur\",\n" +
            "        \"Pudukkottai\",\n" +
            "        \"Ramanathapuram\",\n" +
            "        \"Salem\",\n" +
            "        \"Sivaganga\",\n" +
            "        \"Thanjavur\",\n" +
            "        \"Theni\",\n" +
            "        \"Thoothukudi (Tuticorin)\",\n" +
            "        \"Tiruchirappalli\",\n" +
            "        \"Tirunelveli\",\n" +
            "        \"Tiruppur\",\n" +
            "        \"Tiruvallur\",\n" +
            "        \"Tiruvannamalai\",\n" +
            "        \"Tiruvarur\",\n" +
            "        \"Vellore\",\n" +
            "        \"Viluppuram\",\n" +
            "        \"Virudhunagar\"\n" +
            "    ],\n" +
            "    \"Odisha\": [\n" +
            "        \"Angul\",\n" +
            "        \"Balangir\",\n" +
            "        \"Balasore\",\n" +
            "        \"Bargarh\",\n" +
            "        \"Bhadrak\",\n" +
            "        \"Boudh\",\n" +
            "        \"Cuttack\",\n" +
            "        \"Deogarh\",\n" +
            "        \"Dhenkanal\",\n" +
            "        \"Gajapati\",\n" +
            "        \"Ganjam\",\n" +
            "        \"Jagatsinghapur\",\n" +
            "        \"Jajpur\",\n" +
            "        \"Jharsuguda\",\n" +
            "        \"Kalahandi\",\n" +
            "        \"Kandhamal\",\n" +
            "        \"Kendrapara\",\n" +
            "        \"Kendujhar (Keonjhar)\",\n" +
            "        \"Khordha\",\n" +
            "        \"Koraput\",\n" +
            "        \"Malkangiri\",\n" +
            "        \"Mayurbhanj\",\n" +
            "        \"Nabarangpur\",\n" +
            "        \"Nayagarh\",\n" +
            "        \"Nuapada\",\n" +
            "        \"Puri\",\n" +
            "        \"Rayagada\",\n" +
            "        \"Sambalpur\",\n" +
            "        \"Sonepur\",\n" +
            "        \"Sundargarh\"\n" +
            "    ],\n" +
            "    \"Puducherry (UT)\": [\n" +
            "        \"Karaikal\",\n" +
            "        \"Mahe\",\n" +
            "        \"Pondicherry\",\n" +
            "        \"Yanam\"\n" +
            "    ],\n" +
            "    \"Chandigarh (UT)\": [\n" +
            "        \"Chandigarh\"\n" +
            "    ],\n" +
            "    \"Uttarakhand\": [\n" +
            "        \"Almora\",\n" +
            "        \"Bageshwar\",\n" +
            "        \"Chamoli\",\n" +
            "        \"Champawat\",\n" +
            "        \"Dehradun\",\n" +
            "        \"Haridwar\",\n" +
            "        \"Nainital\",\n" +
            "        \"Pauri Garhwal\",\n" +
            "        \"Pithoragarh\",\n" +
            "        \"Rudraprayag\",\n" +
            "        \"Tehri Garhwal\",\n" +
            "        \"Udham Singh Nagar\",\n" +
            "        \"Uttarkashi\"\n" +
            "    ],\n" +
            "    \"Uttar Pradesh\": [\n" +
            "        \"Agra\",\n" +
            "        \"Aligarh\",\n" +
            "        \"Allahabad\",\n" +
            "        \"Ambedkar Nagar\",\n" +
            "        \"Amethi (Chatrapati Sahuji Mahraj Nagar)\",\n" +
            "        \"Amroha (J.P. Nagar)\",\n" +
            "        \"Auraiya\",\n" +
            "        \"Azamgarh\",\n" +
            "        \"Baghpat\",\n" +
            "        \"Bahraich\",\n" +
            "        \"Ballia\",\n" +
            "        \"Balrampur\",\n" +
            "        \"Banda\",\n" +
            "        \"Barabanki\",\n" +
            "        \"Bareilly\",\n" +
            "        \"Basti\",\n" +
            "        \"Bhadohi\",\n" +
            "        \"Bijnor\",\n" +
            "        \"Budaun\",\n" +
            "        \"Bulandshahr\",\n" +
            "        \"Chandauli\",\n" +
            "        \"Chitrakoot\",\n" +
            "        \"Deoria\",\n" +
            "        \"Etah\",\n" +
            "        \"Etawah\",\n" +
            "        \"Faizabad\",\n" +
            "        \"Farrukhabad\",\n" +
            "        \"Fatehpur\",\n" +
            "        \"Firozabad\",\n" +
            "        \"Gautam Buddha Nagar\",\n" +
            "        \"Ghaziabad\",\n" +
            "        \"Ghazipur\",\n" +
            "        \"Gonda\",\n" +
            "        \"Gorakhpur\",\n" +
            "        \"Hamirpur\",\n" +
            "        \"Hapur (Panchsheel Nagar)\",\n" +
            "        \"Hardoi\",\n" +
            "        \"Hathras\",\n" +
            "        \"Jalaun\",\n" +
            "        \"Jaunpur\",\n" +
            "        \"Jhansi\",\n" +
            "        \"Kannauj\",\n" +
            "        \"Kanpur Dehat\",\n" +
            "        \"Kanpur Nagar\",\n" +
            "        \"Kanshiram Nagar (Kasganj)\",\n" +
            "        \"Kaushambi\",\n" +
            "        \"Kushinagar (Padrauna)\",\n" +
            "        \"Lakhimpur - Kheri\",\n" +
            "        \"Lalitpur\",\n" +
            "        \"Lucknow\",\n" +
            "        \"Maharajganj\",\n" +
            "        \"Mahoba\",\n" +
            "        \"Mainpuri\",\n" +
            "        \"Mathura\",\n" +
            "        \"Mau\",\n" +
            "        \"Meerut\",\n" +
            "        \"Mirzapur\",\n" +
            "        \"Moradabad\",\n" +
            "        \"Muzaffarnagar\",\n" +
            "        \"Pilibhit\",\n" +
            "        \"Pratapgarh\",\n" +
            "        \"RaeBareli\",\n" +
            "        \"Rampur\",\n" +
            "        \"Saharanpur\",\n" +
            "        \"Sambhal (Bhim Nagar)\",\n" +
            "        \"Sant Kabir Nagar\",\n" +
            "        \"Shahjahanpur\",\n" +
            "        \"Shamali (Prabuddh Nagar)\",\n" +
            "        \"Shravasti\",\n" +
            "        \"Siddharth Nagar\",\n" +
            "        \"Sitapur\",\n" +
            "        \"Sonbhadra\",\n" +
            "        \"Sultanpur\",\n" +
            "        \"Unnao\",\n" +
            "        \"Varanasi\"\n" +
            "    ],\n" +
            "    \"Arunachal Pradesh\": [\n" +
            "        \"Anjaw\",\n" +
            "        \"Changlang\",\n" +
            "        \"Dibang Valley\",\n" +
            "        \"East Kameng\",\n" +
            "        \"East Siang\",\n" +
            "        \"Kra Daadi\",\n" +
            "        \"Kurung Kumey\",\n" +
            "        \"Lohit\",\n" +
            "        \"Longding\",\n" +
            "        \"Lower Dibang Valley\",\n" +
            "        \"Lower Siang\",\n" +
            "        \"Lower Subansiri\",\n" +
            "        \"Namsai\",\n" +
            "        \"Papum Pare\",\n" +
            "        \"Siang\",\n" +
            "        \"Tawang\",\n" +
            "        \"Tirap\",\n" +
            "        \"Upper Siang\",\n" +
            "        \"Upper Subansiri\",\n" +
            "        \"West Kameng\",\n" +
            "        \"West Siang\"\n" +
            "    ],\n" +
            "    \"Andaman and Nicobar Island (UT)\": [\n" +
            "        \"Nicobar\",\n" +
            "        \"North and Middle Andaman\",\n" +
            "        \"South Andaman\"\n" +
            "    ],\n" +
            "    \"Maharashtra\": [\n" +
            "        \"Ahmednagar\",\n" +
            "        \"Akola\",\n" +
            "        \"Amravati\",\n" +
            "        \"Aurangabad\",\n" +
            "        \"Beed\",\n" +
            "        \"Bhandara\",\n" +
            "        \"Buldhana\",\n" +
            "        \"Chandrapur\",\n" +
            "        \"Dhule\",\n" +
            "        \"Gadchiroli\",\n" +
            "        \"Gondia\",\n" +
            "        \"Hingoli\",\n" +
            "        \"Jalgaon\",\n" +
            "        \"Jalna\",\n" +
            "        \"Kolhapur\",\n" +
            "        \"Latur\",\n" +
            "        \"Mumbai City\",\n" +
            "        \"Mumbai Suburban\",\n" +
            "        \"Nagpur\",\n" +
            "        \"Nanded\",\n" +
            "        \"Nandurbar\",\n" +
            "        \"Nashik\",\n" +
            "        \"Osmanabad\",\n" +
            "        \"Palghar\",\n" +
            "        \"Parbhani\",\n" +
            "        \"Pune\",\n" +
            "        \"Raigad\",\n" +
            "        \"Ratnagiri\",\n" +
            "        \"Sangli\",\n" +
            "        \"Satara\",\n" +
            "        \"Sindhudurg\",\n" +
            "        \"Solapur\",\n" +
            "        \"Thane\",\n" +
            "        \"Wardha\",\n" +
            "        \"Washim\",\n" +
            "        \"Yavatmal\"\n" +
            "    ],\n" +
            "    \"Chhattisgarh\": [\n" +
            "        \"Balod\",\n" +
            "        \"Baloda Bazar\",\n" +
            "        \"Balrampur\",\n" +
            "        \"Bastar\",\n" +
            "        \"Bemetara\",\n" +
            "        \"Bijapur\",\n" +
            "        \"Bilaspur\",\n" +
            "        \"Dantewada (South Bastar)\",\n" +
            "        \"Dhamtari\",\n" +
            "        \"Durg\",\n" +
            "        \"Gariyaband\",\n" +
            "        \"Janjgir-Champa\",\n" +
            "        \"Jashpur\",\n" +
            "        \"Kabirdham (Kawardha)\",\n" +
            "        \"Kanker (North Bastar)\",\n" +
            "        \"Kondagaon\",\n" +
            "        \"Korba\",\n" +
            "        \"Korea (Koriya)\",\n" +
            "        \"Mahasamund\",\n" +
            "        \"Mungeli\",\n" +
            "        \"Narayanpur\",\n" +
            "        \"Raigarh\",\n" +
            "        \"Raipur\",\n" +
            "        \"Rajnandgaon\",\n" +
            "        \"Sukma\",\n" +
            "        \"Surajpur  \",\n" +
            "        \"Surguja\"\n" +
            "    ]\n" +
            "}";
}
