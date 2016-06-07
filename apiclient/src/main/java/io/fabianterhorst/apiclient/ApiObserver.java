package io.fabianterhorst.apiclient;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ApiObserver extends ApiStorage implements IApiObserver {

    private Observable.Transformer mLifecycle;

    public ApiObserver() {
    }

    public ApiObserver(Realm realm) {
        super(realm);
    }

    protected void setLifecycle(Observable.Transformer mLifecycle) {
        this.mLifecycle = mLifecycle;
    }

    protected <T> Observable.Transformer<T, T> getLifecycle() {
        return mLifecycle != null ? mLifecycle : o -> o;
    }

    @Override
    public <Item extends RealmObject> Observable<List<Item>> getApiObservable(Observable<List<Item>> api, Class<Item> realmClass, String sortedField, List<Integer> ids) {
        if (getStorage() != null) {
            RealmResults<Item> realmResults;
            if (sortedField != null)
                realmResults = (ids == null) ? getItems(realmClass, sortedField) : getItems(realmClass, sortedField, ids);
            else
                realmResults = getItems(realmClass);
            Observable<List<Item>> realmObserver = realmResults.asObservable()
                    .filter(RealmResults::isLoaded)
                    .compose(getLifecycle())
                    .switchMap(Observable::just);
            Observable<List<Item>> retrofitObserver = api
                    .compose(applySchedulers())
                    .compose(getLifecycle());
            return Observable.<List<Item>>create(subscriber -> {
                realmObserver.take(2).subscribe(subscriber::onNext, subscriber::onError, subscriber::onCompleted);
                retrofitObserver.subscribe(this::setItems, subscriber::onError);
            }).compose(getLifecycle());
        } else
            return api;
    }

    @Override
    public <Item extends RealmObject> Observable<List<Item>> getApiObservable(Observable<List<Item>> api, Class<Item> realmClass) {
        return getApiObservable(api, realmClass, null);
    }

    @Override
    public <Item extends RealmObject> Observable<List<Item>> getApiObservable(Observable<List<Item>> api, Class<Item> realmClass, String sortedField) {
        return getApiObservable(api, realmClass, sortedField, null);
    }

    /**
     * Apply the default android schedulers to a observable
     *
     * @param <T> the current observable
     * @return the transformed observable
     */
    protected <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }
}
