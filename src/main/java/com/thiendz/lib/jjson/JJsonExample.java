package com.thiendz.lib.jjson;

import org.json.simple.JSONValue;

import java.util.List;
import java.util.Map;

public class JJsonExample {
    public static void main(String[] args) {
//        readJson();
        sort();
//        System.out.println(JSONValue.parse("1234567889"));
    }

    public static void sort() {
        String jsonStr = "[1.7, 7, 9, \"b\"]";
        JJson json = new JJson(jsonStr);

        Double a = json.sum().toDouble();
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
                "    \"sex\":\"Male\",\n" +
                "    \"salary\":1200.0,\n" +
                "    \"hobbit\": [\"watch movie\", \"coding\", \"sleep\"]\n" +
                "}";
        JJson json = JJson.parse(jsonStr);

        System.out.println("firstName: " + json.q(".firstName").toString());
        System.out.println("lastName: " + json.q(".lastName").toString());
        System.out.println("streetAddress: " + json.q(".address.streetAddress").toString());
        System.out.println("postalCode: " + json.q(".address.postalCode").toLong());
        System.out.println("salary: " + json.q(".salary").toDouble());


        Map<String, JJson> addressMap = json.q(".address").toPairObjs();
        for (Map.Entry<String, JJson> entry : addressMap.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }

        List<String> hobbitList = json.q(".hobbit").toStrs();
        for (String hobbit : hobbitList)
            System.out.println(hobbit);

        List<JJson> phoneList = json.q(".phoneNumbers").toObjs();
        for (JJson j : phoneList) {
            Map<String, JJson> phone = j.toPairObjs();
            for (Map.Entry<String, JJson> entry : phone.entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }
        }

        List<String> hobbitListSort = json.q(".hobbit").sort().toStrs();
        for (String hobbit : hobbitListSort)
            System.out.println(hobbit);

        List<String> hobbitListReverse = json.q(".hobbit").reverse().toStrs();
        for (String hobbit : hobbitListReverse)
            System.out.println(hobbit);
    }
}
