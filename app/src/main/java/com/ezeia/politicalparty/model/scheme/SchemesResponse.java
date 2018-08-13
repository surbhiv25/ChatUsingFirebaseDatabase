package com.ezeia.politicalparty.model.scheme;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SchemesResponse implements Serializable
{
    @SerializedName("data")
    @Expose
    private List<SchemeData> data = null;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    private final static long serialVersionUID = 478355503827257179L;

    public List<SchemeData> getData() {
        return data;
    }

    public void setData(List<SchemeData> data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}