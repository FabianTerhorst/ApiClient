package io.fabianterhorst.apiclient.app;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface GithubApi {

    String END_POINT = "https://api.github.com";

    @GET("/users/{user}/repos")
    Observable<List<Repository>> getRepositories(@Path("user") String user);
}
