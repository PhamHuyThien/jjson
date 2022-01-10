package com.thiendz.lib.jjson;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JJson implements Comparable<JJson> {

    private Object json;

    public static JJson parse(String json) {
        return new JJson(json);
    }

    public static JJson parse(Object json) {
        return new JJson(json);
    }

    public static JJson build() {
        return new JJson();
    }

    public JJson() {
    }

    public JJson(Object json) {
        this.json = json;
    }

    public JJson(String json) {
        if (json != null) {
            this.json = JSONValue.parse(json);
        }
    }

    public JJson k(String key) {
        JSONObject jsonObj = toJsonObject(json);
        return new JJson(jsonObj != null ? jsonObj.get(key) : null);
    }

    public JJson i(int index) {
        JSONArray jsonArray = toJsonArray(json);
        return new JJson(jsonArray != null ? jsonArray.get(index) : null);
    }

    public JJson q(String query) {
        final String REGEX_JSON_VALID = "^((\\.\\w+(\\[\\d+\\])*)|((\\[\\d+\\])*))+$";
        final String REGEX_JSON_NAME = "^(\\w+)\\[";
        final String REGEX_JSON_KEY_ARRAY = "\\[(\\d+)\\]+";
        if (json == null) {
            return this;
        }
        if (stringRegex(REGEX_JSON_VALID, query) == null) {
            return new JJson("");
        }
        Object jsonTemp = json;
        for (String node : query.split("\\.")) {
            if (node.equals("")) {
                continue;
            }
            if (query.contains("[")) {
                if (!query.startsWith("[")) {
                    String[] names = stringRegex(REGEX_JSON_NAME, node);
                    assert names != null;
                    String name = names[0].replace("[", "");
                    jsonTemp = Objects.requireNonNull(toJsonObject(jsonTemp)).get(name);
                }
                String[] strIndexes = stringRegex(REGEX_JSON_KEY_ARRAY, node);
                assert strIndexes != null;
                for (String strIndex : strIndexes) {
                    int index = Integer.parseInt(strIndex.replaceAll("\\[|\\]", ""));
                    JSONArray jsonArrayTemp = toJsonArray(jsonTemp);
                    jsonTemp = jsonArrayTemp != null ? jsonArrayTemp.get(index) : null;
                }
            } else {
                JSONObject jsonObjectTemp = toJsonObject(jsonTemp);
                jsonTemp = jsonObjectTemp != null ? jsonObjectTemp.get(node) : null;
            }
        }
        return new JJson(jsonTemp);
    }

    public JJson put(Map<?, ?> map) {
        JSONObject jsonObject = json == null ? new JSONObject() : (JSONObject) json;
        if (map != null)
            jsonObject.putAll(map);
        return new JJson(jsonObject);
    }

    public JJson put(List<?> list) {
        JSONArray jsonArray = json == null ? new JSONArray() : (JSONArray) json;
        if (list != null)
            jsonArray.addAll(list);
        return new JJson(jsonArray);
    }

    public JJson min() {
        List<Float> floatList = toFloats();
        if (floatList == null)
            return new JJson(null);
        Collections.sort(floatList);
        return new JJson(floatList.get(0));
    }

    public JJson max() {
        List<Float> floatList = toFloats();
        if (floatList == null)
            return new JJson(null);
        Collections.sort(floatList);
        final int flsLen = floatList.size();
        return new JJson(floatList.get(flsLen - 1));
    }

    public JJson sum() {
        List<Float> floatList = toFloats();
        if (floatList == null)
            return new JJson(null);
        return new JJson(sumFloat(floatList));
    }

    public JJson avg() {
        List<Float> floatList = toFloats();
        if (floatList == null)
            return new JJson(null);
        int size = floatList.size();
        return new JJson(sumFloat(floatList) / size);
    }

    public JJson sort() {
        List<JJson> jsonList = toObjs();
        if (jsonList == null)
            return new JJson(null);
        Collections.sort(jsonList);
        return new JJson(JSONValue.parse(arraysToString(Collections.singletonList(jsonList))));
    }

    public JJson reverse() {
        List<JJson> jsonList = toObjs();
        for (int i = 0; i < jsonList.size() / 2; i++) {
            int idMax = jsonList.size() - 1 - i;
            JJson json = jsonList.get(i);
            jsonList.set(i, jsonList.get(idMax));
            jsonList.set(idMax, json);
        }
        return new JJson(JSONValue.parse(arraysToString(Collections.singletonList(jsonList))));
    }

    public Map<String, JJson> toPairObjs() {
        JSONObject jsonObject = toJsonObject(json);
        if (jsonObject == null)
            return null;
        Map<String, JJson> jsonHashMap = new HashMap<>();
        for (Object o : jsonObject.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            jsonHashMap.put(pair.getKey().toString(), new JJson(pair.getValue()));
        }
        return jsonHashMap;
    }

    public List<String> toKeys() {
        Map<String, JJson> pairMap = toPairObjs();
        if (pairMap == null)
            return null;
        List<String> keyList = new ArrayList<>();
        for (Map.Entry<String, JJson> entry : pairMap.entrySet()) {
            keyList.add(entry.getKey());
        }
        return keyList;
    }

    public List<JJson> toValues() {
        Map<String, JJson> pairMap = toPairObjs();
        if (pairMap == null)
            return null;
        List<JJson> valueList = new ArrayList<>();
        for (Map.Entry<String, JJson> entry : pairMap.entrySet()) {
            valueList.add(entry.getValue());
        }
        return valueList;
    }

    public List<JJson> toObjs() {
        JSONArray jsonArray = toJsonArray(json);
        if (jsonArray == null)
            return null;
        List<JJson> jsonList = new ArrayList<>();
        for (Object o : jsonArray) {
            jsonList.add(new JJson(o));
        }
        return jsonList;
    }

    public List<String> toStrs() {
        JSONArray jsonArray = toJsonArray(json);
        if (jsonArray == null)
            return null;
        List<String> listStr = new ArrayList<>();
        for (Object o : jsonArray) {
            listStr.add(o.toString());
        }
        return listStr;
    }

    public List<Character> toChars() {
        JSONArray jsonArray = toJsonArray(json);
        if (jsonArray == null)
            return null;
        List<Character> characterList = new ArrayList<>();
        for (Object o : jsonArray) {
            try {
                characterList.add(o.toString().toCharArray()[0]);
            } catch (ArrayIndexOutOfBoundsException e) {
                characterList.add(null);
            }
        }
        return characterList;
    }

    public List<Integer> toInts() {
        JSONArray jsonArray = toJsonArray(json);
        if (jsonArray == null)
            return null;
        List<Integer> integerList = new ArrayList<>();
        for (Object o : jsonArray) {
            try {
                integerList.add(Integer.parseInt(strToInt(o.toString())));
            } catch (NumberFormatException ignored) {
                integerList.add(null);
            }
        }
        return integerList;
    }

    public List<Long> toLongs() {
        JSONArray jsonArray = toJsonArray(json);
        if (jsonArray == null)
            return null;
        List<Long> longList = new ArrayList<>();
        for (Object o : jsonArray) {
            try {
                longList.add(Long.parseLong(strToInt(o.toString())));
            } catch (NumberFormatException ignored) {
                longList.add(null);
            }
        }
        return longList;
    }

    public List<Double> toDoubles() {
        JSONArray jsonArray = toJsonArray(json);
        if (jsonArray == null)
            return null;
        List<Double> doubleList = new ArrayList<>();
        for (Object o : jsonArray) {
            try {
                doubleList.add(Double.parseDouble(o.toString()));
            } catch (NumberFormatException ignored) {
                doubleList.add(null);
            }
        }
        return doubleList;
    }

    public List<Float> toFloats() {
        JSONArray jsonArray = toJsonArray(json);
        if (jsonArray == null)
            return null;
        List<Float> floatList = new ArrayList<>();
        for (Object o : jsonArray) {
            try {
                floatList.add(Float.parseFloat(o.toString()));
            } catch (NumberFormatException ignored) {
                floatList.add(null);
            }
        }
        return floatList;
    }

    public List<Boolean> toBools() {
        JSONArray jsonArray = toJsonArray(json);
        if (jsonArray == null)
            return null;
        List<Boolean> booleanList = new ArrayList<>();
        for (Object o : jsonArray) {
            try {
                booleanList.add(Boolean.parseBoolean(o.toString()));
            } catch (Exception ignored) {
                booleanList.add(null);
            }
        }
        return booleanList;
    }

    public Object toObj() {
        return json;
    }

    public String toStr() {
        return json == null ? null : json.toString();
    }

    @Override
    public String toString() {
        return toStr();
    }

    public Character toChar() {
        try {
            return json.toString().toCharArray()[0];
        } catch (ArrayIndexOutOfBoundsException | NullPointerException ignored) {
        }
        return null;
    }

    public Integer toInt() {
        try {
            return Integer.parseInt(strToInt(json.toString()));
        } catch (NumberFormatException | NullPointerException ignored) {
        }
        return null;
    }

    public Long toLong() {
        try {
            return Long.parseLong(strToInt(json.toString()));
        } catch (NumberFormatException | NullPointerException ignored) {
        }
        return null;
    }

    public Double toDouble() {
        try {
            return Double.parseDouble(json.toString());
        } catch (NumberFormatException | NullPointerException ignored) {
        }
        return null;
    }

    public Float toFloat() {
        try {
            return Float.parseFloat(json.toString());
        } catch (NumberFormatException | NullPointerException ignored) {
        }
        return null;
    }

    public Boolean toBool() {
        try {
            return Boolean.parseBoolean(json.toString());
        } catch (NumberFormatException | NullPointerException ignored) {
        }
        return null;
    }

    public int length() {
        int lengthObject = isInstanceOfJsonObject(json) ? Objects.requireNonNull(toJsonObject(json)).size() : -1;
        int lengthArray = isInstanceOfJsonArray(json) ? Objects.requireNonNull(toJsonArray(json)).size() : -1;
        return Math.max(lengthArray, lengthObject);
    }

    @Override
    public int compareTo(JJson jjson) {
        if (json == null) {
            return -1;
        }
        if (jjson == null) {
            return 1;
        }
        String oThis = this.toString();
        String oThat = jjson.toString();
        if (isNumber(oThis) && isNumber(oThat)) {
            return Float.parseFloat(oThis) > Float.parseFloat(oThat) ? 1 : -1;
        } else {
            int len1 = oThis.length();
            int len2 = oThat.length();
            int lim = Math.min(len1, len2);
            char[] v1 = oThis.toCharArray();
            char[] v2 = oThat.toCharArray();
            int k = 0;
            while (k < lim) {
                char c1 = v1[k];
                char c2 = v2[k];
                if (c1 != c2) {
                    return c1 - c2;
                }
                k++;
            }
            return len1 - len2;
        }

    }

    private static JSONObject toJsonObject(Object json) {
        return isInstanceOfJsonObject(json) ? (JSONObject) JSONValue.parse(json.toString()) : null;
    }

    private static JSONArray toJsonArray(Object json) {
        return isInstanceOfJsonArray(json) ? (JSONArray) JSONValue.parse(json.toString()) : null;
    }

    private static boolean isInstanceOfJsonObject(Object json) {
        return (json instanceof JSONObject || json instanceof JJson);
    }

    private static boolean isInstanceOfJsonArray(Object json) {
        return (json instanceof JSONArray || json instanceof JJson);
    }

    private static String[] stringRegex(String regex, String input) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);

        ArrayList<String> alMatch = new ArrayList<>();
        while (m.find()) {
            alMatch.add(m.group());
        }
        String[] matches = new String[alMatch.size()];
        for (int i = 0; i < matches.length; i++) {
            matches[i] = alMatch.get(i);
        }
        return matches.length == 0 ? null : matches;
    }

    private static String strToInt(String fl) {
        if (fl.contains(".")) {
            return fl.substring(0, fl.indexOf("."));
        }
        return fl;
    }

    private static float sumFloat(List<Float> floatList) {
        float fl = 0;
        int size = floatList.size();
        for (int i = 0; i < size / 2; i++) {
            fl += floatList.get(i) + floatList.get(size - 1 - i);
        }
        fl += size % 2 == 0 ? 0 : floatList.get(size / 2);
        return fl;
    }

    private static String arraysToString(List<Object> objectList) {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (Object obj : objectList) {
            String str = obj == null ? "" : obj.toString().replaceAll("\"", "\\\\\"");
            stringBuilder.append("\"").append(str).append("\",");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1) + "]";
    }

    private static boolean isNumber(String num) {
        try {
            Float.parseFloat(num);
            return true;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }
}