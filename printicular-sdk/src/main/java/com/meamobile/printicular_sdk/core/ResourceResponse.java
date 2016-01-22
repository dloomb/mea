package com.meamobile.printicular_sdk.core;


import com.meamobile.printicular_sdk.core.models.Image;
import com.meamobile.printicular_sdk.core.models.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import rx.Observable;

public class ResourceResponse<T>
{
    protected Map<Long, T> mObjects;

    public static Observable<ResourceResponse> fromResponse(Map<String, Object> response, String type)
    {
        Map<String, Map> objects = Model.hydrate(response);

        assert objects != null;
        Map<Long, Model> models = objects.get(type);

        ResourceResponse rr = new ResourceResponse(objects);
        return Observable.just(rr);
    }

    protected ResourceResponse(Map<Long, Model> objects) {
        mObjects = (Map<Long, T>) objects;
    }

    protected ResourceResponse(){}


    public T findModelWithId(long id) {
        return null;
    }

    public List<T> asList() {
        Collection<T> c = mObjects.values();
        return new ArrayList<T>(c);
    }

}

