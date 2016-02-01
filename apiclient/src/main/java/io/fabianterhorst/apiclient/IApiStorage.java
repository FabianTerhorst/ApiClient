package io.fabianterhorst.apiclient;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public interface IApiStorage {
    Realm getStorage();
    <Item extends RealmObject> RealmQuery<Item> getQuery(Class<Item> objectClass);
    <Item extends RealmObject> RealmResults<Item> getItems(Class<Item> objectClass);
    <Item extends RealmObject> RealmResults<Item> getItems(Class<Item> objectClass, String sortedFieldName);
    <Item extends RealmObject> RealmResults<Item> getItems(Class<Item> objectClass, String sortedFieldName, List<Integer> allowedIds);
    <Item extends RealmObject> void setItems(List<Item> items);
}
