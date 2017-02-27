package com.thiendn.coderschool.newyorktime.api;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created by thiendn on 27/02/2017.
 */

public class ApiResponse {
    @SerializedName("response")
    private JsonObject response;

    @SerializedName("status")
    private String status;

    public JsonObject getResponse() {
        if(null == response){
            return new JsonObject();
        }
        return response;
    }

    public String getStatus() {
        return status;
    }
}
