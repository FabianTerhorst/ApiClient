package io.fabianterhorst.apiclient;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import io.realm.RealmList;
import io.realm.RealmObject;
import okhttp3.HttpUrl;

public interface IApi<Api> {
    GsonBuilder getGsonBuilder(GsonBuilder gsonBuilder);
    Api getApi();
    void setApiKey(String mApiKey);
    HttpUrl.Builder getHttpUrlBuilder(HttpUrl.Builder builder);
    <Item extends RealmObject> ApiClient registerRemoveNullListSerializer(GsonBuilder gsonBuilder, TypeToken<RealmList<Item>> typeToken, Class<Item> item);
}
