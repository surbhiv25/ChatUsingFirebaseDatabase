package com.ezeia.politicalparty.model.signUp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SignUpPostData implements Serializable
{
    @SerializedName("full_name")
    @Expose
    private String full_name;
    @SerializedName("mobile_number")
    @Expose
    private String mobile_number;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("device_type")
    @Expose
    private String device_type;
    @SerializedName("device_id")
    @Expose
    private String device_id;
    @SerializedName("district")
    @Expose
    private String district;
    @SerializedName("state")
    @Expose
    private String state;

    private final static long serialVersionUID = 6570590562140663625L;

    public String getFullName() {
        return full_name;
    }

    public void setFullName(String full_name) {
        this.full_name = full_name;
    }

    public String getmobile_number() {
        return mobile_number;
    }

    public void setmobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public String getpassword() {
        return password;
    }

    public void setpassword(String password) {
        this.password = password;
    }

    public String getrole() {
        return role;
    }

    public void setrole(String role) {
        this.role = role;
    }

    public String getdevice_type() {
        return device_type;
    }

    public void setdevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getdevice_id() {
        return device_id;
    }

    public void setdevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getdistrict() {
        return district;
    }

    public void setdistrict(String district) {
        this.district = district;
    }

    public String getstate() {
        return state;
    }

    public void setstate(String state) {
        this.state = state;
    }

}
