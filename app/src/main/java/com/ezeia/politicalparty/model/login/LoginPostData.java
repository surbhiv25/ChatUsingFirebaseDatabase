package com.ezeia.politicalparty.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginPostData implements Serializable
{
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("device_type")
    @Expose
    private String device_type;
    @SerializedName("device_id")
    @Expose
    private String device_id;

    private final static long serialVersionUID = 6570590562140663625L;

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

}
