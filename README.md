# ApiClient
A easy to use api client that combines the power of Retrofit, Realm, Gson, Rxjava and Retrolambda in a library for Java and Android

##### Add to build.gradle

```groovy
compile 'io.fabianterhorst:apiclient:0.4'
compile 'io.fabianterhorst:apiclient-accountmanager:0.1'
compile 'io.fabianterhorst:apiclient-components:0.1'
```

#### First Step

Create your Api Class

```java
public class Twitter extends ApiClient<TwitterApi> implements TwitterApi {

    public Twitter(Realm realm, String apiKey) {
        super(realm, TwitterApi.PARAM_API_KEY, apiKey, TwitterApi.class, TwitterApi.END_POINT);
    }

    public static void init(String apiKey) {
        init(new Twitter(apiKey));
    }

    @Override
    public Observable<List<Character>> getTweets() {
    	//Here you can define the tablename for realm and the fieldname if needed to sort the tweets with
        return getApiObservable(getApi().getTweets(), Tweet.class, "name");
    }

    @Override
    public Observable<List<Character>> getComments(ArrayList<Integer> ids) {
    	//You can also get results only for specific ids
        return getApiObservable(getApi().getComments(), Comment.class, "id", ids);
    }
}
```

#### Second Step

Create your Api Interface (The Retrofit way)

```java
public interface TwitterApi {
	
	@GET("tweets")
	Observable<List<Tweet>> getTweets();

	@GET("comments")
	Observable<List<Tweet>> getComments(@Query("id") ArrayList<Integer> ids);
}
```

#### Third Step

Initiate the Singleton in the Application onCreate

```java
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        Twitter.init("0123456789");
    }
}
```

#### Fourth Step

Use it and have fun. The library is handling the saving, the loading and the refreshing for you.

```java
Twitter twitter = Twitter.getInstance();

twitter.getTweets().subscribe(tweets-> System.out.println(tweets));
```

### FAQ

##### How to handle Android Activity lifecycle

You can use the ApiClient component module to get access to RxActivity and RxFragment

In your Activity you have to get the Singleton with the Activity lifecycle. Your activity has to extend RxActivity.

```java
Twitter twitter = Twitter.getInstance(bindToLifecycle());
```

And thats everythink you have to do to prevent memory leaks.

##### RealmList doesn´t support null objects. How can i ignore null object inside the response json?

You can override the gson builder inside your api class and add custom deserializer adapters to avoid adding null objects.

```java
@Override
public GsonBuilder getGsonBuilder(GsonBuilder gsonBuilder) {
    GsonUtils.registerRemoveNullListSerializer(gsonBuilder, new TypeToken<RealmList<MyFirstObject>>() {}, MyFirstObject.class);
    GsonUtils.registerRemoveNullListSerializer(gsonBuilder, new TypeToken<RealmList<MySecondObject>>() {}, MySecondObject.class);
    GsonUtils.registerRemoveNullListSerializer(gsonBuilder, new TypeToken<RealmList<MyThirdObject>>() {}, MyThirdObject.class);
    return gsonBuilder;
}
```

##### How to change the api key from everywhere?

You can use the ```setApiKey``` method.

```java
Twitter.getInstance().setApiKey("9876543210");
```

##### How to add other query parameters?

You can override the ```getHttpUrlBuilder(HttpUrl.Builder builder)``` method from the api client.

```java
@Override
public HttpUrl.Builder getHttpUrlBuilder(HttpUrl.Builder builder) {
    return addQueryParameter("lang", Locale.getDefault().getLanguage());
}
```

##### How to use a authentication

The easiest way is to use the AuthUtils to add a authentication via the request builder for post parameters and headers or the http url builder for query parameter

myurl.com/api
```java
@Override
public Request.Builder getRequestBuilder(Request.Builder builder) {
    return AuthUtils.addDefaultAuthentication(builder, getApiKey());
}
```

myurl.com/api?apiKey=012345
```java
@Override
public HttpUrl.Builder getHttpUrlBuilder(HttpUrl.Builder builder) {
    AuthUtils.addDefaultAuthentication(builder, "apiKey", getApiKey());
        return builder.addQueryParameter("lang", Locale.getDefault().getLanguage());
}
```

### License
    Copyright 2016 Fabian Terhorst

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
