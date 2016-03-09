package io.fabianterhorst.apiclient.Utils;

import okhttp3.HttpUrl;
import okhttp3.Request;

public class AuthUtils {

    public static String DEFAULT_AUTH_HEADER = "Authorization";
    public static String DEFAULT_AUTH_HEADER_PREFIX = "Bearer ";

    public static HttpUrl.Builder addDefaultAuthentication(HttpUrl.Builder builder, String apiKeyParameter, String apiKey) {
        return builder.addQueryParameter(apiKeyParameter, apiKey);
    }

    public static Request.Builder addDefaultAuthentication(Request.Builder builder, String apiKey) {
        return builder.addHeader(DEFAULT_AUTH_HEADER, DEFAULT_AUTH_HEADER_PREFIX + apiKey);
    }
}
