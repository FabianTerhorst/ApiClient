package io.fabianterhorst.apiclient.Utils;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import io.realm.RealmList;
import io.realm.RealmObject;

public class GsonUtils {

    /**
     * Add NullListSerializer to the Gson Builder
     * This allow the use of RealmList with Null Values from Json in Objects
     *
     * @param gsonBuilder gson builder
     * @param typeToken   type token
     * @param item        item for the serializer
     * @param <Item>      generic type from item
     */
    public static <Item extends RealmObject> void registerRemoveNullListSerializer(GsonBuilder gsonBuilder, TypeToken<RealmList<Item>> typeToken, Class<Item> item) {
        gsonBuilder.registerTypeAdapter(typeToken.getType(), new RemoveNullListSerializer<>(item));
    }
}
