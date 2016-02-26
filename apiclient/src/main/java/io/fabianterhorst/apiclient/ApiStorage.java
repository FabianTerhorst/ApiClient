package io.fabianterhorst.apiclient;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ApiStorage implements IApiStorage {

    private Realm mRealm;

    public ApiStorage(Realm realm){
        this.mRealm = realm;
    }

    @Override
    public Realm getStorage() {
        return mRealm;
    }

    @Override
    public <Item extends RealmObject> RealmQuery<Item> getQuery(Class<Item> objectClass) {
        return mRealm.where(objectClass);
    }

    @Override
    public <Item extends RealmObject> RealmResults<Item> getItems(Class<Item> objectClass) {
        return getQuery(objectClass).findAllAsync();
    }

    @Override
    public <Item extends RealmObject> RealmResults<Item> getItems(Class<Item> objectClass, String sortedFieldName) {
        return getQuery(objectClass).findAllSortedAsync(sortedFieldName);
    }

    @Override
    public <Item extends RealmObject> RealmResults<Item> getItems(Class<Item> objectClass, String sortedFieldName, List<Integer> ids) {
        RealmQuery<Item> query = getQuery(objectClass);
        for (int i = 0; i < ids.size() - 1; i++) {
            query = query.equalTo(sortedFieldName, ids.get(i)).or();
        }
        query = query.equalTo(sortedFieldName, ids.get(ids.size() - 1));

        return query.findAllSortedAsync(sortedFieldName);
    }

    @Override
    public <Item extends RealmObject> void setItems(List<Item> items) {
        mRealm.executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(items));
    }
}
