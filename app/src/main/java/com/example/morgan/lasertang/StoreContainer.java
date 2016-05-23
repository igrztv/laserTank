package com.example.morgan.lasertang;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by v.denisov on 17.05.16.
 */
public class StoreContainer {
    Map<String, List<List<String>>> data = new HashMap<String, List<List<String>>>();

    void setData(String key, List<List<String>> itemData){
        data.put(key, itemData);
    }

    List<List<String>> getData(String key){
        return data.get(key);
    }
}
