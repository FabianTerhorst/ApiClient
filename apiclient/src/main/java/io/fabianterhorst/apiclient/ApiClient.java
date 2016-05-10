package io.fabianterhorst.apiclient;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;

import io.realm.Realm;
import io.realm.RealmObject;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class ApiClient<Api> extends ApiObserver implements IApi<Api> {

    private Api mApi;

    private final Class<Api> mClazz;

    private String mApiKey;

    private final String mApiBaseUrl;

    private static ApiClient mInstance;

    private final Interceptor API_KEY_INTERCEPTOR = chain -> {
        Request oldRequest = chain.request();
        HttpUrl httpUrl = getHttpUrlBuilder(oldRequest.url().newBuilder()).build();
        Request.Builder builder = getRequestBuilder(oldRequest.newBuilder());
        builder.url(httpUrl);
        return chain.proceed(builder.build());
    };

    public ApiClient(Class<Api> clazz, String apiBaseUrl) {
        this.mClazz = clazz;
        this.mApiBaseUrl = apiBaseUrl;
    }

    public ApiClient(String apiKey, Class<Api> clazz, String apiBaseUrl) {
        this.mApiKey = apiKey;
        this.mClazz = clazz;
        this.mApiBaseUrl = apiBaseUrl;
    }

    public ApiClient(Realm realm, String apiKey, Class<Api> clazz, String apiBaseUrl) {
        super(realm);
        this.mApiKey = apiKey;
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
     * Get the api key
     */
    public String getApiKey(){
        return mApiKey;
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
        return builder;
    }

    /**
     * Getting the RequestBuilder for the OkHttp interceptor
     * Here you can add your own implementation to authenticate your application
     *
     * @param builder http request builder
     * @return the modified http request builder for the interceptor
     */
    @Override
    public Request.Builder getRequestBuilder(Request.Builder builder) {
        return builder;
    }

    /**
     * Get the current api instance. You have to call init() before
     *
     * @return the current api instance
     */
    @SuppressWarnings("unchecked")
    public static <E extends ApiClient> E getInstance() {
        mInstance.setLifecycle(null);
        return (E) mInstance;
    }

    /**
     *
     * Get the current api instance with the given lifecycle. You have to call init() before
     *
     * @param lifecycle lifecycle from the activity or the fragment
     * @param <E> api client type
     * @return the current api instance
     */
    @SuppressWarnings("unchecked")
    public static <E extends ApiClient> E getInstance(Observable.Transformer<?, ?> lifecycle) {
        mInstance.setLifecycle(lifecycle);
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
}
