package com.jio.rtlsappfull.network;

import com.jio.rtlsappfull.model.SubmitAPIData;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIEndPointInterface {
    @POST("apirouter/v7/submit-api")
    Call<SubmitAPIData> submitAPI(@HeaderMap Map<String, String> headers, @Body SubmitAPIData submitAPIData);
}
