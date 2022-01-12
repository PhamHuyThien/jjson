package com.thiendz.lib.jjson.exam;

import com.thiendz.lib.jjson.JJson;

import java.util.List;
import java.util.Map;

public class JJsonExample {
    public static void main(String[] args) {
        readJson();
    }

    public static void readJson() {
        String jsonStr = "{\n" +
                "    \"firstName\":\"Vinh\",\n" +
                "    \"lastName\":\"Phan\",\n" +
                "    \"address\":{\n" +
                "        \"streetAddress\":\"11 Tu Lap\",\n" +
                "        \"district\":\"Me Linh\",\n" +
                "        \"city\":\"Ha Noi\",\n" +
                "        \"state\":\"\",\n" +
                "        \"postalCode\":\"100000\"\n" +
                "    },\n" +
                "    \"age\":25,\n" +
                "    \"phoneNumbers\":[\n" +
                "        {\n" +
                "            \"type\":\"home\",\n" +
                "            \"number\":\"096677028\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"type\":\"fax\",\n" +
                "            \"number\":\"0435508028\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"girlFriend\": false,\n" +
                "    \"sex\":\"Male\",\n" +
                "    \"salary\":1200.0,\n" +
                "    \"hobbit\": [\"watch movie\", \"coding\", \"sleep\"]\n" +
                "    \"codeLove\": [1, 1, 0, 7, 2, 0, 0, 0, 1107, 2000, 11, 07, 20]\n" +
                "}";
        JJson json = JJson.parse(jsonStr);

        System.out.println("firstName: " + json.q(".firstName").toString());

        System.out.println("lastName: " + json.k("lastName").toString());

        System.out.println("firstName index 0: " + json.k("firstName").toChar());

        System.out.println("streetAddress: " + json.q(".address.streetAddress").toString());

        System.out.println("postalCode: " + json.q(".address.postalCode").toLong());

        System.out.println("salary: " + json.q(".salary").toDouble());

        System.out.println("json.hobbit[1]: " + json.q(".hobbit[1]").toStr());

        System.out.println("json.hobbit[2]: " + json.k("hobbit").i(2).toStr());

        System.out.println("json.girlFriend: " + json.q(".girlFriend").toBool());

        System.out.println("json.address object forEach map:");
        Map<String, JJson> addressMap = json.q(".address").toPairObjs();
        for (Map.Entry<String, JJson> entry : addressMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("json.hobbit list: " + json.q(".hobbit").toStrs());

        System.out.println("json.phoneNumbers array forEach key value:");
        List<JJson> phoneList = json.q(".phoneNumbers").toObjs();
        for (JJson j : phoneList) {
            Map<String, JJson> phone = j.toPairObjs();
            for (Map.Entry<String, JJson> entry : phone.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }

        System.out.println("json.hobbit sort string:" + json.q(".hobbit").sort().toStrs());
        System.out.println("json.hobbit reverse: " + json.q(".hobbit").reverse().toStrs());
        System.out.println("json.codeLove sort int: " + json.q(".codeLove").sort().toInts());
        System.out.println("json.codeLove find max: " + json.q(".codeLove").max().toInt());
        System.out.println("json.codeLove find min: " + json.q(".codeLove").min().toInt());
        System.out.println("json.codeLove sum: " + json.q(".codeLove").sum().toInt());
        System.out.println("json.codeLove avg: " + json.q(".codeLove").avg().toInt());
        System.out.println("json.address get list key: " + json.q(".address").toKeys());
        System.out.println("json.address get list value: " + json.q(".address").toValues());
    }
}
