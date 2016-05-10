package io.fabianterhorst.apiclient.app;

import java.util.List;

import io.fabianterhorst.apiclient.ApiClient;
import retrofit2.http.Path;
import rx.Observable;

public class Github extends ApiClient<GithubApi> implements GithubApi {

    public Github() {
        super(GithubApi.class, GithubApi.END_POINT);
    }

    public static void init() {
        init(new Github());
    }

    @Override
    public Observable<List<Repository>> getRepositories(@Path("user") String user) {
        return getApiObservable(getApi().getRepositories(user), Repository.class);
    }
}
