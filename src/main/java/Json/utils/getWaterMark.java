package Json.utils;

import Json.utils.Util;
import com.google.gson.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class getWaterMark {
    static LinkedHashMap<String, String> JSON = new LinkedHashMap<>();
    public static int sum = 0;
    public static int valid = 0;
    public static String waterMark = null;
    public static int b = 0;
    public static List<String> keyValueList;

    public static void main(String[] args) throws Exception {
        String fileName = "ta_cb_person_heatmap_collect";
        String fileSuffix = ".json";
        String inputPath = "G:\\360水印项目\\数据样本\\数据样本 - 0505\\结构化\\" + fileName + fileSuffix;
//        String detectionPath = "G:\\360水印项目\\detection\\json\\" + fileName + fileSuffix;
//        String realWaterMark = getWaterMark(detectionPath);
//        System.out.println(realWaterMark);

        String detectionPath1 = "G:\\360水印项目\\detection\\json\\ta_cb_person_heatmap_collect_gai" + fileSuffix;
        String realWaterMark1 = getWaterMark(detectionPath1);
//        System.out.println(realWaterMark1);
//        251254815201832612535776543180315817825
//        251254815201832612535776543180315817825
    }

    public static String getWaterMark(String inputPath) throws FileNotFoundException {
        keyValueList = getJSON(inputPath);
        List<String> md5keyValueList = new ArrayList<>();
        for (int i = 0; i < keyValueList.size(); i++) {
            String str = keyValueList.get(i);
//            System.out.println(str);
            str = new BigInteger(md5(str), 16).toString();
            md5keyValueList.add(str);
        }
//        System.out.println(md5keyValueList.get(487));

        for (int i = 0; i < md5keyValueList.size(); i++) {
            String str = md5keyValueList.get(i);
//            System.out.println(str);
            int a = ((int) str.charAt(0) + (int) str.charAt(1) + (int) str.charAt(3)) / 3 % 9;
            waterMark += String.valueOf(a);
        }

        waterMark = waterMark.substring(4);
//        System.out.println(waterMark.length());
        int a = 70;//能处理的String最多长度为70,json文件从第0行到最后一行，被平均分成了70组
        String realwatermark = null;
        b = waterMark.length() / a;//每组b行
        if (b != 0) {
            String[] str1 = new String[a];
            for (int i = 0; i < a; i++) {
                str1[i] = waterMark.substring(i * b, i * b + b);
                BigDecimal tmp = new BigDecimal(str1[i]);
//            System.out.println(i + " " + str1[i] + "  " + tmp.divideAndRemainder(BigDecimal.valueOf(8))[1]);
                BigDecimal t1 = tmp.divideAndRemainder(BigDecimal.valueOf(9))[1];
                BigDecimal t2 = tmp.divideAndRemainder(BigDecimal.valueOf(8))[1];
//                System.out.println(t1.add(t2).divideToIntegralValue(BigDecimal.valueOf(2)));
                realwatermark += t1.add(t2).divideToIntegralValue(BigDecimal.valueOf(2)).toString();
            }
            return realwatermark.substring(4);
        } else {
            return waterMark;
        }


    }

    //MD5加密
    public static String md5(String text) {
        //加密后的字符串
        String encodeStr = DigestUtils.md5Hex(text);
//        System.out.println("MD5加密后的字符串为:encodeStr=" + encodeStr);
        return encodeStr;
    }

    public static List<String> getJSON(String inputPath) throws FileNotFoundException {
        JsonObject object = (JsonObject) new JsonParser().parse(new FileReader(inputPath));
        JSON = eliminateLevels(object);
        List<String> keyList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();
        List<String> keyValueList = new ArrayList<>();
        for (String key : JSON.keySet()) {
//            System.out.println("Key = " + key + "   value = " + JSON.get(key));
            keyList.add(key);
            valueList.add(JSON.get(key));
        }


        for (int i = 0; i < keyList.size(); i++) {
            String str = keyList.get(i);
            str = str.replace("/", "");
            keyList.set(i, str);
            String temp = str + valueList.get(i);
            keyValueList.add(temp);
        }
        return keyValueList;
    }

    public static void recursiveEliminateHelper(JsonElement object, String prefix, int arrayCount) {
        if (arrayCount > 0) {
            //which indicates the parent object is an array
            prefix += ("/" + arrayCount);
            arrayCount = 0;
        }
        if (object instanceof JsonObject) {
            // continue the recursion
            for (Map.Entry<String, JsonElement> entry : ((JsonObject) object).entrySet()) {
                recursiveEliminateHelper(entry.getValue(), prefix + "/" + entry.getKey(), arrayCount);
            }
        } else if (object instanceof JsonArray) {
            int count = 1;
            for (Iterator<JsonElement> iter = ((JsonArray) object).iterator(); iter.hasNext(); ) {
                recursiveEliminateHelper(iter.next(), prefix, count);
                count++;
            }
        } else if (!(object instanceof JsonNull)) {
            // instance of JsonPrimitive
            String value = object.getAsString();
            if (!Util.isJSON(value) && Util.isDouble(value)) {
                value = value.substring(0, value.indexOf('.'));
            }
            JSON.put(prefix, value);
            valid++;
            sum++;
        }
    }

    public static LinkedHashMap<String, String> eliminateLevels(JsonObject object) {
        // clear
        recursiveEliminateHelper(object, "", 0);

        if (JSON.size() == 0)
            System.out.println("there is no field to embedding!\n");
//        for(String key:JSON.keySet())
//            System.out.println(key+"   "+JSON.get(key));

        System.out.println("-------------------------------------------------");

        System.out.println("Sum of KEYS: " + sum + ". Sum of Valid: " + valid);
        return JSON;
    }
}
