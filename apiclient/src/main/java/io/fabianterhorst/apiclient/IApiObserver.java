package io.fabianterhorst.apiclient;

import java.util.List;

import io.realm.RealmObject;
import rx.Observable;

public interface IApiObserver {
    <Item extends RealmObject> Observable<List<Item>> getApiObservable(Observable<List<Item>> api, Class<Item> realmClass, String sortedField, List<Integer> ids);
    <Item extends RealmObject> Observable<List<Item>> getApiObservable(Observable<List<Item>> api, Class<Item> realmClass, String sortedField);
    <Item extends RealmObject> Observable<List<Item>> getApiObservable(Observable<List<Item>> api, Class<Item> realmClass);
}
