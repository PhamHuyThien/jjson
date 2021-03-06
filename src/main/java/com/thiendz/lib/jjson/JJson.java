package com.thiendz.lib.jjson;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JJson {

    private static final String REGEX_JSON_VALID = "^((\\.\\w+(\\[\\d+\\])*)|((\\[\\d+\\])*))+$";
    private static final String REGEX_JSON_NAME = "^(\\w+)\\[";
    private static final String REGEX_JSON_KEY_ARRAY = "\\[(\\d+)\\]+";
    private static final String REGEX_DOT = "\\.";
    private static final String REGEX_BRACKET = "\\[|\\]";
    private Object json;

    public static JJson parse(Object json) {
        return new JJson(json);
    }

    public JJson(Object object) {
        if (object != null) {
            Object parseJson = JSONValue.parse(object.toString());
            this.json = parseJson == null ? object : parseJson;
        }
    }

    public JJson k(String key) {
        JSONObject jsonObj = toJsonObject(json);
        return parse(jsonObj != null ? jsonObj.get(key) : null);
    }

    public JJson i(int index) {
        JSONArray jsonArray = toJsonArray(json);
        return parse(jsonArray != null ? jsonArray.get(index) : null);
    }

    public JJson q(String query) {
        if (json == null || stringRegex(REGEX_JSON_VALID, query).isEmpty())
            return parse(null);
        Object jsonTemp = json;
        for (String node : query.split(REGEX_DOT)) {
            if (node.equals(""))
                continue;
            if (query.contains("[")) {
                if (!query.startsWith("[")) {
                    List<String> nameList = stringRegex(REGEX_JSON_NAME, node);
                    if (!nameList.isEmpty()) {
                        String name = nameList.get(0).replace("[", "");
                        jsonTemp = Objects.requireNonNull(toJsonObject(jsonTemp)).get(name);
                    }
                }
                List<String> indexesList = stringRegex(REGEX_JSON_KEY_ARRAY, node);
                if (!indexesList.isEmpty()) {
                    for (String strIndex : indexesList) {
                        int index = Integer.parseInt(strIndex.replaceAll(REGEX_BRACKET, ""));
                        JSONArray jsonArrayTemp = toJsonArray(jsonTemp);
                        jsonTemp = jsonArrayTemp != null ? jsonArrayTemp.get(index) : null;
                    }
                }
            } else {
                JSONObject jsonObjectTemp = toJsonObject(jsonTemp);
                jsonTemp = jsonObjectTemp != null ? jsonObjectTemp.get(node) : null;
            }
        }
        return parse(jsonTemp);
    }

    public JJson min() {
        List<Float> floatList = toFloats();
        if (floatList == null)
            return this;
        Collections.sort(floatList);
        return parse(floatList.get(0));
    }

    public JJson max() {
        List<Float> floatList = toFloats();
        if (floatList == null)
            return this;
        Collections.sort(floatList);
        final int flsLen = floatList.size();
        return parse(floatList.get(flsLen - 1));
    }

    public JJson sum() {
        List<Float> floatList = toFloats();
        if (floatList == null)
            return this;
        return parse(sumFloat(floatList));
    }

    public JJson avg() {
        List<Float> floatList = toFloats();
        if (floatList == null)
            return this;
        int size = floatList.size();
        return parse(sumFloat(floatList) / size);
    }

    public JJson sort() {
        List<String> jsonList = toStrs();
        if (jsonList == null)
            return this;
        jsonList.sort((sThis, sThat) -> {
            Float fThis = isNumber(sThis);
            Float fThat = isNumber(sThat);
            if (fThis != null && fThat != null)
                return fThis.compareTo(fThat);
            return sThis.compareTo(sThat);
        });
        return parse(arraysToString(jsonList));
    }

    public JJson reverse() {
        List<JJson> jsonList = toObjs();
        for (int i = 0; i < jsonList.size() / 2; i++) {
            int idMax = jsonList.size() - 1 - i;
            JJson json = jsonList.get(i);
            jsonList.set(i, jsonList.get(idMax));
            jsonList.set(idMax, json);
        }
        return parse(arraysToString(jsonList));
    }

    public Map<String, JJson> toPairObjs() {
        JSONObject jsonObject = toJsonObject(json);
        if (jsonObject == null)
            return null;
        Map<String, JJson> jsonHashMap = new HashMap<>();
        jsonObject.forEach((k, v) -> jsonHashMap.put(k.toString(), parse(v)));
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
            jsonList.add(parse(o));
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
        return toString();
    }

    @Override
    public String toString() {
        return json == null ? null : json.toString();
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
        int objectSize = -1;
        int arraySize = -1;
        JSONObject jsonObject = toJsonObject(json);
        JSONArray jsonArray = toJsonArray(json);
        if (jsonObject != null)
            objectSize = jsonObject.size();
        if (jsonArray != null)
            arraySize = jsonArray.size();
        return Math.max(objectSize, arraySize);
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

    private static List<String> stringRegex(String regex, String input) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        ArrayList<String> alMatch = new ArrayList<>();
        while (m.find())
            alMatch.add(m.group());
        return alMatch;
    }

    private static String strToInt(String fl) {
        return fl.contains(".") ? fl.substring(0, fl.indexOf(".")) : fl;
    }

    private static float sumFloat(List<Float> floatList) {
        return floatList.stream().reduce((float) 0, (total, elm) -> elm != null ? total + elm : total);
    }

    private static Float isNumber(String num) {
        try {
            return Float.parseFloat(num);
        } catch (NumberFormatException | NullPointerException ignored) {
        }
        return null;
    }

    private static String arraysToString(List<?> objectList) {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (Object obj : objectList) {
            String str = obj == null ? "" : obj.toString().replaceAll("\"", "\\\\\"");
            stringBuilder.append("\"").append(str).append("\",");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1) + "]";
    }
}