package io.fabianterhorst.apiclient.Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Iterator;

import io.realm.RealmList;
import io.realm.RealmObject;

public class RemoveNullListSerializer<Item extends RealmObject> implements JsonDeserializer<RealmList<Item>> {

    private Class<Item> clazz;

    public RemoveNullListSerializer(Class<Item> clazz){
        this.clazz = clazz;
    }

    @Override
    public RealmList<Item> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray array = json.getAsJsonArray();
        Iterator<JsonElement> i = array.iterator();
        while (i.hasNext()) {
            JsonElement je = i.next();
            if (je instanceof JsonNull)
                i.remove();
        }
        RealmList<Item> list = new RealmList<>();
        for (JsonElement je : array) {
            list.add(context.deserialize(je, clazz));
        }
        return list;
    }

}
