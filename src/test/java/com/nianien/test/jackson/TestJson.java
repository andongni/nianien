package com.nianien.test.jackson;


import com.nianien.core.util.JsonParser;
import com.nianien.test.bean.Color;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings({"unchecked", "rawtypes"})
public class TestJson {

    @Test
    public void testPrimitive() {
        JsonParser jp = new JsonParser();
        FinanceDecimal financeDecimal=new FinanceDecimal("3.1415926");
        System.out.println(jp.toJson(financeDecimal));
    }
    @Test
    public void testBase() {
        JsonParser jp = new JsonParser();
        String json = "[1,2]";
        Object obj = jp.toObject(json);
        System.out.println(obj.getClass());
        json = "{name:'lining'}";
        obj = jp.toObject(json);
        System.out.println(obj.getClass());
        json = "1";
        obj = jp.toObject(json);
        System.out.println(obj.getClass());
        json = "1.0";
        obj = jp.toObject(json);
        System.out.println(obj.getClass());
        json = "10000000000000000";
        obj = jp.toObject(json);
        System.out.println(obj.getClass());
        json = "'1984-10-24'";
        obj = jp.toBean(json, Date.class);
        System.out.println(obj);
        System.out.println(obj.getClass());
        jp.setDatePatterns(new String[]{"yyyy年MM月dd日"});
        System.out.println(jp.toJson(new Date()));

        System.out.println(jp.toJson(Color.BLACK));
        obj = jp.toBean("\"BLACK\"",Color.class);
        System.out.println(obj);
        obj = jp.toBean(""+Color.BLACK.ordinal(),Color.class);
        System.out.println(obj);
    }

    @Test
    public void testMap() {
        JsonParser jp = new JsonParser();
        String json = "{name:['lining','wuhao']}";
        System.out.println("=============================>1:");
        Map<String, List<String>> map = jp.toBean(json, Map.class);
        for (Entry<String, List<String>> en : map.entrySet()) {
            System.out.print(en.getKey() + ":[");
            List<String> values = en.getValue();
            for (String e : values) {
                System.out.print(e + "\t");
            }
            System.out.println("]");
        }
        System.out.println("=============================>2:");

        Map<String, List> map2 = jp.toMap(json, String.class, List.class);
        for (Entry<String, List> en : map2.entrySet()) {
            System.out.print(en.getKey() + ":[");
            List<String> values = en.getValue();
            for (String e : values) {
                System.out.print(e + "\t");
            }
            System.out.println("]");
        }
        System.out.println("=============================>3:");
        Map<String, String[]> map3 = jp.toMap(json, String.class, String[].class);
        for (Entry<String, String[]> en : map3.entrySet()) {
            System.out.print(en.getKey() + ":[");
            String[] values = en.getValue();
            for (String e : values) {
                System.out.print(e + "\t");
            }
            System.out.println("]");
        }

    }

    @Test
    public void testOther() {
        JsonParser jp = new JsonParser();
        String[][] arr = new String[2][];
        arr[0] = new String[]{"zg", "\"中国"};
        arr[1] = new String[]{"mg", "美国"};
        System.out.println(jp.toJson(arr));
        String json = jp.toJson(arr);
        arr = jp.toBean(json, String[][].class);
        for (String[] s : arr) {
            System.out.println(Arrays.toString(s));
        }
        System.out.println("=============");
        List<List<String>> list = jp.toBean(json, List.class);
        for (List<String> ss : list) {
            for (String s : ss) {
                System.out.print(s + "\t");
            }
            System.out.println();
        }
        json = "['lining','wuhao']";
        Object obj = jp.toBean(json, String[].class);
        System.out.println(obj.getClass());

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>");
        json = "[{name:['lining']},{name:'wuhao'}]";
        Map[] omap = jp.toBean(json, Map[].class);
        for (Map m : omap) {
            for (Object o : m.entrySet()) {
                Entry en = (Entry) o;
                System.out.println(en.getKey() + "->" + en.getValue().getClass());
            }
        }
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>");
        Object objList = jp.toBean(json, Object.class);
        for (Object l : (List) objList) {
            Map m = (Map) l;
            for (Object o : m.entrySet()) {
                Entry en = (Entry) o;
                System.out.println(en.getKey() + "->" + en.getValue().getClass());
            }
        }
    }

}
