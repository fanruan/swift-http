package com.fr.bi.conf.provider;

import com.fr.json.JSONObject;

/**
 * Created by Young's on 2016/5/19.
 */
public interface BICubeConfManagerProvider {
    String XML_TAG = "BICubeConfManagerProvider";

    String getCubePath();

    void saveCubePath(String path);

    String getLoginInfoField();

    void saveLoginInfoField(String fieldId);

    Object getLoginFieldValue(long userId);

    JSONObject createJSON(long userId) throws Exception;

    @Deprecated
    void persistData(long userId);
}
