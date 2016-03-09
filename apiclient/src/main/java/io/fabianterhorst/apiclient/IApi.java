package io.fabianterhorst.apiclient;

import com.google.gson.GsonBuilder;

import okhttp3.HttpUrl;
import okhttp3.Request;

public interface IApi<Api> {
    GsonBuilder getGsonBuilder(GsonBuilder gsonBuilder);
    Api getApi();
    void setApiKey(String mApiKey);
    HttpUrl.Builder getHttpUrlBuilder(HttpUrl.Builder builder);
    Request.Builder getRequestBuilder(Request.Builder builder);
}
