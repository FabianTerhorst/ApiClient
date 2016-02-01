package io.fabianterhorst.apiclient;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient<Api> extends ApiObserver implements IApi<Api> {

    private Api mApi;

    private Class<Api> mClazz;

    private String mApiKey;

    private String mApiKeyParameter;

    private String mApiBaseUrl;

    private static ApiClient mInstance;

    private final Interceptor API_KEY_INTERCEPTOR = chain -> {
        Request oldRequest = chain.request();
        HttpUrl httpUrl = getHttpUrlBuilder(oldRequest.url().newBuilder()).build();
        Request.Builder builder = oldRequest.newBuilder();
        builder.url(httpUrl);
        return chain.proceed(builder.build());
    };

    public ApiClient(Realm realm, String apiKeyParameter, String apiKey, Class<Api> clazz, String apiBaseUrl) {
        super(realm);
        this.mApiKey = apiKey;
        this.mApiKeyParameter = apiKeyParameter;
        this.mClazz = clazz;
        this.mApiBaseUrl = apiBaseUrl;
    }

    /**
     * Set the api key for the default interceptor
     * This api key will set as a query parameter value with the parameter key you have specify by initialising the ApiClient to the url
     *
     * @param apiKey to set as a url parameter
     */
    @Override
    public void setApiKey(String apiKey) {
        this.mApiKey = apiKey;
    }

    /**
     * Getting the UrlBuilder for the OkHttp interceptor
     * Here you can add your own implementation to authenticate your application
     *
     * @param builder http url builder
     * @return the modified http url builder for the interceptor
     */
    @Override
    public HttpUrl.Builder getHttpUrlBuilder(HttpUrl.Builder builder) {
        return builder.addQueryParameter(mApiKeyParameter, mApiKey);
    }

    /**
     * Get the current api instance. You have to call init() before
     *
     * @return the current api instance
     */
    @SuppressWarnings("unchecked")
    public static <E extends ApiClient> E getInstance() {
        return (E) mInstance;
    }

    /**
     * Lightweight method to init the api instance. Should be called in Application#onCreate()
     * Can be modified in the api class to prevent the application to initiate the api class itself
     *
     * @param apiClient api client
     */
    public static void init(ApiClient apiClient) {
        mInstance = apiClient;
    }

    @Override
    public GsonBuilder getGsonBuilder(GsonBuilder gsonBuilder) {
        return gsonBuilder;
    }

    /**
     * Get the api singleton from the api interface
     *
     * @return api
     */
    @Override
    public Api getApi() {
        if (mApi == null) {
            mApi = new Retrofit.Builder()
                    .client(new OkHttpClient.Builder()
                            .addInterceptor(API_KEY_INTERCEPTOR)
                            .build())
                    .baseUrl(mApiBaseUrl)
                    .addConverterFactory(GsonConverterFactory.create(getGsonBuilder(new GsonBuilder())
                            .setExclusionStrategies(new ExclusionStrategy() {
                                @Override
                                public boolean shouldSkipField(FieldAttributes f) {
                                    return f.getDeclaringClass().equals(RealmObject.class);
                                }

                                @Override
                                public boolean shouldSkipClass(Class<?> clazz) {
                                    return false;
                                }
                            })
                            .create()))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()
                    .create(mClazz);
        }
        return mApi;
    }

    /**
     * Add NullListSerializer to the Gson Builder
     * This allow the use of RealmList with Null Values from Json in Objects
     *
     * @param gsonBuilder gson builder
     * @param typeToken type token
     * @param item item for the serializer
     * @param <Item> generic type from item
     * @return the api client
     */
    @Override
    public <Item extends RealmObject> ApiClient registerRemoveNullListSerializer(GsonBuilder gsonBuilder, TypeToken<RealmList<Item>> typeToken, Class<Item> item){
        gsonBuilder.registerTypeAdapter(typeToken.getType(), new RemoveNullListSerializer<>(item));
        return this;
    }
}
