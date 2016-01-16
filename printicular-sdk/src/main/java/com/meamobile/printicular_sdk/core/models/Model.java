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
    protected long mId;
    protected String mType;
    protected Date mCreatedAt, mUpdatedAt;
    protected Map<String, Object> mRelationshipMap;
    protected Map<String, Object> mMeta;

    public Model(){}

    public void populate(Map data)
    {
        mId = ((Number) data.get("id")).longValue();
        mType = (String) data.get("type");

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

    public long getId()
    {
        return mId;
    }

    public String getType()
    {
        return mType;
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

    ///-----------------------------------------------------------
    /// @name Helpers
    ///-----------------------------------------------------------


    protected Object safeParse(Object input, String wantedClass)
    {
        if (input instanceof String)
        {
            switch (wantedClass)
            {
                case "STRING":
                    return input;

                case "DOUBLE":
                    return Double.parseDouble((String) input);
            }
        }

        if (input instanceof Number)
        {
            switch (wantedClass)
            {
                case "STRING":
                    return ((Number) input).toString();

                case "DOUBLE":
                    return ((Number) input).doubleValue();

                case "BOOLEAN":
                    return ((Number) input).intValue() > 0;
            }
        }

        if (input instanceof Boolean)
        {
            switch (wantedClass)
            {
                case "BOOLEAN":
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss zzz");
        return sdf.format(date);
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
        if (shrunk.substring(0, shrunk.length() - 3) == "ses")
        {
            out = shrunk.substring(0, shrunk.length() - 2);
        }

        return "com.meamobile.printicular_sdk.core.models." + out;
    }



    ///-----------------------------------------------------------
    /// @name Evaporate
    ///-----------------------------------------------------------

    public Map<String, Object> evaporate()
    {
        Map<String, Object> data = new HashMap<>();

        data.put("id", mId);
        data.put("type", mType);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("created_at", dateToString(mCreatedAt));
        attributes.put("upadted_at", dateToString(mUpdatedAt));

        data.put("attributes", attributes);

        if (mMeta != null)
        {
            data.put("meta", mMeta);
        }

        if (mRelationshipMap != null)
        {
            data.put("relationships", mRelationshipMap);
        }

        return data;
    }

    public String toJsonString()
    {
        Map evaporated = evaporate();
        return new Gson().toJson(evaporated);
    }


}
