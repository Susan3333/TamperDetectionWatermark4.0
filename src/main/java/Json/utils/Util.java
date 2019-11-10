package Json.utils;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import static java.lang.Math.min;

public class Util {
    public static String newTagName = "KEY_NUMBER";

    public static boolean isJSON(String jsonStr) {
        JsonElement jsonElement;
        try {
            jsonElement = new JsonParser().parse(jsonStr);
        } catch (Exception e) {
            return false;
        }
        if (jsonElement == null) {
            return false;
        }
        if (!jsonElement.isJsonObject()) {
            return false;
        }
        return true;
    }

    public static JsonElement replaceKey(JsonElement source, Map<String, String> rep, String prefix, String curr, int arrayCount) {
        if(arrayCount>0){
            //which indicates the parent object is an array
            //we assert that the child object of an JsonArray can only be either JsonArray or JsonObject
            prefix += arrayCount;
//            arrayCount = 0;
        }
        if (source == null || source.isJsonNull()) {
            return JsonNull.INSTANCE;
        }
        if (source.isJsonPrimitive()) {
            if(arrayCount!=0){
                System.out.println("[Error] we assert that the child object of an JsonArray can only be either JsonArray or JsonObject. However, JsonPrimitive found.");
                arrayCount = 0;
            }
            JsonElement value = source.getAsJsonPrimitive();
            String t = prefix.replaceAll("[^A-Za-z0-9]","");
            if (rep.containsKey(t)) {
                String newKey = rep.get(prefix.replaceAll("[^A-Za-z0-9]",""));
                JsonPrimitive newJsonObj = new JsonPrimitive(newKey);
                return newJsonObj;
            }
            return source;
        }
        if (source.isJsonArray()) {
            if(arrayCount!=0){
                System.out.println("[Warning] Recursive structure of JsonArray is currently under tests.");
                arrayCount = 0;
            }
            int count = 1;
            JsonArray jsonArr = source.getAsJsonArray();
            JsonArray jsonArray = new JsonArray();
            for(JsonElement item:jsonArr){
                jsonArray.add(replaceKey(item, rep,prefix,curr,count));
                count++;
            }
            return jsonArray;
        }
        if (source.isJsonObject()) {
            JsonObject jsonObj = source.getAsJsonObject();
            Iterator<Map.Entry<String, JsonElement>> iterator = jsonObj.entrySet().iterator();
            JsonObject newJsonObj = new JsonObject();
            if(arrayCount!=0){
                //newJsonObj.add(newTagName, new JsonPrimitive(arrayCount));
//                String randNum = formateRate(String.valueOf(nextDouble(0.0, 100.0)));
//                newJsonObj.add(newTagName, new JsonPrimitive(randNum));
                arrayCount = 0;
            }
            for (; iterator.hasNext();){
                Map.Entry<String, JsonElement> item = iterator.next();
                String key = item.getKey();
                JsonElement value = item.getValue();
                newJsonObj.add(key, replaceKey(value, rep,prefix+key,key,arrayCount));
            }

            return newJsonObj;
        }
        return JsonNull.INSTANCE;
    }

    public static void writeJsonStream(OutputStream out, JsonElement object) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent(" ");
        Gson gson = new Gson();
        gson.toJson(object,writer);
        // print the result
//        gson.toJson(object,System.out);

        writer.close();
    }
    /**
     * 生成max到min范围的浮点数
     * */
    public static double nextDouble(final double min, final double max) {
        return min + ((max - min) * new Random().nextDouble());
    }
    /**
     * 保留小数点后两位小数
     * @param rateStr xx.xxxxx
     * @return result
     * */
    public static String formateRate(String rateStr) {
        if (rateStr.indexOf(".") != -1) {
            // 获取小数点的位置
            int num = 0;
            num = rateStr.indexOf(".");

            String dianAfter = rateStr.substring(0, num + 1);
            String afterData = rateStr.replace(dianAfter, "");

            return rateStr.substring(0, num) + "." + afterData.substring(0, 2);
        } else {
            if (rateStr == "1") {
                return "100";
            } else {
                return rateStr;
            }
        }
    }
    //判断整数（int）
    public static boolean isInteger(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    //判断浮点数（double和float）
    public static boolean isDouble(String str) {
        boolean isOK = true;
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        isOK = (pattern.matcher(str).matches())^(isInteger(str));
        String[] nstr = str.split("\\.");
        if(isOK && nstr.length == 2)
            return true;
        else
            return false;
    }

    /*
     * 字符串转二进制
     * @param s: 字符串信息
     * @retuen : 信息的二进制形式
     */
    public static List<Integer> Bytes2Bin(byte[] s){
        List<Integer> msg = new LinkedList<>();

        for(int i = 0; i < s.length; i++){
            byte b = s[i];
            for(int j = 0; j < 8; j++)
                msg.add( (b>>(7-j) ) & 0x01 );
        }

        return msg;
    }

    /*
     * 二进制信息转字符串
     * @param msg: 二进制信息
     * @retuen : 信息的字符串形式
     */
    public static byte[] Bin2Bytes(List<Integer> msg){
        byte[] str = new byte[msg.size()/8];

        for(int i = 0; i < msg.size(); i+=8){
            byte t = 0;
            for(int j = 0; j < 8; j++){
                t = (byte)((t << 1) + msg.get(i+j));
            }
            str[i/8] = t;
        }

        return str;
    }

    /*
     * 二进制信息转字符串
     * @param msg: 二进制信息
     * @retuen : 信息的字符串形式
     */
    public static String Bin2String(List<Integer> msg){
        byte[] b = Bin2Bytes(msg);
        String str = null;
        try {
            str = new String(b, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return str;
    }
    /*
     * 十进制信息转二进制
     * @param intWm: 十进制水印
     * @param wmLen : 二进制水印长度
     * @retuen : 二进制水印
     */
    public static List<Integer> int2Bin(List<Integer> intWm, int wmLen){ //
        List<Integer> wm = new LinkedList<>();

        for(int i = 0; i < intWm.size(); i++){
            int t = intWm.get(i);
            int idx = wm.size();
            for (int j = 0; j < 3; j++){
                if(t == 0)
                    wm.add(idx,0);
                else{
                    wm.add(idx,t % 2);
                    t /= 2;
                }
            }
        }

        int s = wm.size();
        if(s > wmLen){
            int over = s - wmLen;
            if(over == 1)
                wm.remove(s - 3);
            else if(over == 2){
                wm.remove(s - 3);
                wm.remove(s - 3);
            }
        }

        return wm;
    }


    /*
     * 二进制信息转十进制
     * @param binWm: 二进制水印
     * @retuen : 十进制水印
     */
    public static List<Integer> bin2Int(List<Integer> binWm){ // 将二进制数转换成十进制数：十进制范围是0-8
        List<Integer> wm = new LinkedList<>();

        for(int i = 0; i < binWm.size(); i+=3){
            int maxShift = min(3, binWm.size()-i);
            int wm_t = 0;
            for(int j = 0; j < maxShift; j++){
                wm_t = wm_t * 2 + binWm.get(i+j);
            }
            wm.add(wm_t);
        }

        return wm;
    }

    /*
     * 字符串转二进制
     * @param s: 字符串信息
     * @retuen : 信息的二进制形式
     */
    public static List<Integer> String2Bin(String s){
        List<Integer> msg = null;
        try {
            msg = Bytes2Bin(s.getBytes("GBK"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public static TreeMap<Integer, Integer> getCorrespondenceOfIndex(String detectionPath) {
        File file1 = new File(detectionPath); // 创建File类对象
        FileInputStream fis = null; // 创建FileInputStream类对象读取File
        InputStreamReader isr = null; // 创建InputStreamReader对象接收文件流
        BufferedReader br = null; // 创建reader缓冲区将文件流装进去
        List<String> strList = new ArrayList<>();
        List<String> strListAfter = new ArrayList<>();
        TreeMap<Integer, Integer> intMap = new TreeMap<>();
        try {
            fis = new FileInputStream(file1);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String lineTxt = null;
            // 从缓冲区中逐行读取代码，调用readLine()方法
            while ((lineTxt = br.readLine()) != null) {
//                System.out.println(lineTxt); // 逐行输出文件内容
                strList.add(lineTxt);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 文件执行完毕别忘了关闭数据流
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String s = "\":\"";
        for (int i = 0; i < strList.size(); i++) {
            if (strList.get(i).replace(" ", "").contains(s)) {
                strListAfter.add(strList.get(i));
            }
        }

        int flag = 0, flagAfter = 0;
        for (int i = 0; i < strList.size() && flagAfter < strListAfter.size(); i++) {
            if (strListAfter.get(flagAfter).equals(strList.get(i))) {
                intMap.put(flagAfter,i);
                flagAfter++;
            }
        }
//        for (Integer key : intMap.keySet()) {
//            System.out.println("Key = " + key + " Value = " + intMap.get(key));
//        }
        return intMap;
    }


}
