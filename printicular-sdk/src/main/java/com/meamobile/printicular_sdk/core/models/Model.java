package com.meamobile.printicular_sdk.core.models;

import com.google.gson.Gson;

import org.apache.commons.lang3.text.WordUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model
{
    protected enum ClassType
    {
        STRING,
        DOUBLE,
        BOOLEAN,
        INTEGER,
        LONG
    }

    protected long mId = -1;

    protected Date
            mCreatedAt,
            mUpdatedAt;

    protected Map<String, Object>
            mMeta,
            mRelationshipMap;



    public Model(){}

    ///-----------------------------------------------------------
    /// @name Data Management
    ///-----------------------------------------------------------


    public void populate(Map data)
    {
        Map nestedData = findMapWithKey(data, "data");
        data = nestedData != null ? nestedData : data;

        mId = ((Number) data.get("id")).longValue();

        Map attributes = (Map) data.get("attributes");
        if (attributes != null)
        {
            String created = (String) attributes.get("created_at");
            if (created != null && created != "null")
            {
                mCreatedAt = parseDate(created);
            }
            String updated = (String) attributes.get("updated_at");
            if (updated != null && updated != "null")
            {
                mUpdatedAt = parseDate(updated);
            }
        }

        mMeta = (Map) data.get("meta");

        mRelationshipMap = (Map) data.get("relationships");
    }

    public void associate(Map<String, Map> objects)
    {

    }

    public void update(Model model) {
        mId = isSetOr(model.getId(), mId);
        mCreatedAt = (Date) isSetOr(model.getCreatedAt(), mCreatedAt);
        mUpdatedAt = (Date) isSetOr(model.getUpdatedAt(), mUpdatedAt);

        if (model.getMeta() != null) {
            mMeta.putAll(model.getMeta());
        }

        if (model.getRelationshipsMap() != null) {
            mRelationshipMap.putAll(model.getRelationshipsMap());
        }
    }


    ///-----------------------------------------------------------
    /// @name Hydration
    ///-----------------------------------------------------------

    public static Map<String, Map> hydrate(Map<String, Object> json)
    {
        try
        {
            Object data = json.get("data");
            List included = (List) json.get("included");
            Map<String, Map> objects = new HashMap();

            if (data != null)
            {
                if (data instanceof List)
                {
                    hydrateArray((List) data, objects);
                }
                else
                {
                    hydrateMap((Map) data, objects);
                }
            }

            if (included != null)
            {
                hydrateArray(included, objects);
            }

            if (data == null && included == null)
            {
                hydrateMap(json, objects);
            }

            for (String groupType : objects.keySet())
            {
                Map<String, Model> group = (Map) objects.get(groupType);
                Collection<Model> models = group.values();

                for (Model model : models)
                {
                    model.associate(objects);
                }
            }

            return objects;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static void hydrateMap(Map input, Map output) throws Exception
    {
        String type = (String) input.get("type");
        Class _class = Class.forName(classNameFromType(type));

        Model object = (Model) _class.newInstance();
        object.populate(input);

        Map grouped = (Map) output.get(type);
        grouped = (grouped != null) ? grouped : new HashMap<Long, Model>();
        grouped.put(object.getId(), object);
        output.put(type, grouped);
    }

    private static void hydrateArray(List<Map> input, Map output) throws Exception
    {
        for (Map<String, Object> single : input)
        {
            hydrateMap(single, output);
        }
    }

    private static String classNameFromType(String type)
    {
        String capitalized = WordUtils.capitalizeFully(type, '_');
        String shrunk = capitalized.replace("_", "");
        String out = shrunk.substring(0, shrunk.length() - 1);
        String end = shrunk.substring(shrunk.length() - 3, shrunk.length());
        if (end.equals("ses"))
        {
            out = shrunk.substring(0, shrunk.length() - 2);
        }
        else if (end.equals("ies"))
        {
            out = shrunk.substring(0, shrunk.length() - 3);
            out += "y";
        }

        return "com.meamobile.printicular_sdk.core.models." + out;
    }



    ///-----------------------------------------------------------
    /// @name Evaporate
    ///-----------------------------------------------------------

    public Map<String, Map> evaporate()
    {
        Map<String, Object> data = new HashMap<>();

        safePut(data, "id", mId);
        data.put("type", getType());

        Map<String, Object> attributes = new HashMap<>();
        safePut(attributes, "created_at", dateToString(mCreatedAt));
        safePut(attributes, "updated_at", dateToString(mUpdatedAt));

        data.put("attributes", attributes);


        safePut(attributes, "meta", mMeta);

        safePut(attributes, "relationships", mRelationshipMap);

        Map<String, Map> out = new HashMap();
        out.put("data", data);
        return out;
    }

    public String toJsonString()
    {
        Map evaporated = evaporate();
        return new Gson().toJson(evaporated);
    }







    ///-----------------------------------------------------------
    /// @name Property Access
    ///-----------------------------------------------------------

    public long getId()
    {
        return mId;
    }

    public String getType()
    {
        throw new RuntimeException("Cannot call getType on Base Model");
    }

    public Date getCreatedAt()
    {
        return mCreatedAt;
    }

    public Date getUpdatedAt()
    {
        return mUpdatedAt;
    }

    public Object getMeta(String key)
    {
        if (mMeta != null)
        {
            return mMeta.get(key);
        }
        return null;
    }

    public Map<String, Object> getMeta()
    {
        return mMeta;
    }

    protected Map<String, Object> getRelationshipsMap() {
        return mRelationshipMap;
    }


    ///-----------------------------------------------------------
    /// @name Helpers
    ///-----------------------------------------------------------

    protected void safePut(Map map, Object key, Object value)
    {
        if (map != null && key != null && value != null)
        {
            if (value instanceof Number && ((Number) value).intValue() == 0)
            {
                return;
            }

            map.put(key, value);
        }
    }

    protected Object safeParse(Object input, ClassType wantedClass)
    {
        if (input instanceof String)
        {
            switch (wantedClass)
            {
                case STRING:
                    return ((String) input).length() == 0 ? null : input;

                case INTEGER:
                    return Integer.parseInt((String) input);

                case LONG:
                    return Long.parseLong((String) input);

                case DOUBLE:
                    return Double.parseDouble((String) input);
            }
        }

        if (input instanceof Number)
        {
            switch (wantedClass)
            {
                case STRING:
                    return ((Number) input).toString();

                case INTEGER:
                    return ((Number) input).intValue();

                case LONG:
                    return ((Number) input).longValue();

                case DOUBLE:
                    return ((Number) input).doubleValue();

                case BOOLEAN:
                    return ((Number) input).intValue() > 0;
            }
        }

        if (input instanceof Boolean)
        {
            switch (wantedClass)
            {
                case BOOLEAN:
                    return input;
            }
        }

        return null;
    }

    protected Date parseDate(String input)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss zzz");
        Date date = null;
        try {
            date = sdf.parse(input);
        } catch (ParseException e) {
            sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            try {
                date = sdf.parse(input);
            } catch (ParseException e1) {
                sdf = new SimpleDateFormat("yyyy-MM-dd");

                try
                {
                    date = sdf.parse(input);
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                    return null;
                }
            }
        }
        return date;
    }

    protected String dateToString(Date date) {
        return (date == null) ? null : new SimpleDateFormat("yyyy-MM-dd kk:mm:ss zzz").format(date);
    }

    protected Map<String, Object> findMapWithKey(Map<String, Map> map, String key)
    {
        Map result = null;
        if (map != null)
        {
            result = map.get(key);

            if (result == null)
            {
                Map<String, Map> data = map.get("data");
                if (data != null)
                {
                    result = data.get(key);
                }
            }
        }

        return result;
    }

    protected Object isSetOr(Object isValue, Object orValue) {
        return isValue != null ? isValue : orValue;
    }

    protected long isSetOr(long isValue, long orValue) {
        return isValue != -1 ? isValue : orValue;
    }

    protected int isSetOr(int isValue, int orValue) {
        return isValue != -1 ? isValue : orValue;
    }

}
