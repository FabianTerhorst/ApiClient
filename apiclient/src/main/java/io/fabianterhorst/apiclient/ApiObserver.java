package io.fabianterhorst.apiclient;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ApiObserver extends ApiStorage implements IApiObserver {

    public ApiObserver(Realm realm) {
        super(realm);
    }

    @Override
    public <Item extends RealmObject> Observable<List<Item>> getApiObservable(Observable<List<Item>> api, Class<Item> realmClass, String sortedField, List<Integer> ids) {
        if (getStorage() != null) {
            RealmResults<Item> realmResults;
            if (sortedField != null) {
                if (ids == null)
                    realmResults = getItems(realmClass, sortedField);
                else
                    realmResults = getItems(realmClass, sortedField, ids);
            } else
                realmResults = getItems(realmClass);
            Observable<List<Item>> realmObserver = realmResults.asObservable()
                    .filter(RealmResults::isLoaded)
                    .switchMap(Observable::just);
            Observable<List<Item>> retrofitObserver = api
                    .compose(applySchedulers())
                    .map(objects -> {
                        if (sortedField != null) {
                            Collections.sort(objects, (lhs, rhs) -> getGetterMethodReturnValue(lhs, sortedField).compareTo(getGetterMethodReturnValue(rhs, sortedField)));
                        }
                        setItems(objects);
                        return objects;
                    });
            return Observable.create(subscriber -> {
                realmObserver.subscribe(subscriber::onNext, subscriber::onError);
                retrofitObserver.subscribe(subscriber::onNext, subscriber::onError);
            });
        } else
            return api.compose(applySchedulers());
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
     * Get the value getter method return value
     * Finds the getter value from a object by appending get to the field name and returns the method return value as a string
     *
     * @param object    object where the getter has to be find
     * @param fieldName field name from the value that has a same named getter
     * @return the getter method return value
     */
    private String getGetterMethodReturnValue(Object object, String fieldName) {
        try {
            return (String) object.getClass().getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length())).invoke(object);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Apply the default android schedulers to a observable
     *
     * @param <T> the current observable
     * @return the transformed observable
     */
    public <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }
}
