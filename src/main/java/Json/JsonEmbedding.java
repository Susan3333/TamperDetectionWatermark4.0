package Json;


import Json.utils.Util;
import Json.utils.WatermarkUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class JsonEmbedding {
    public TreeMap<String, String> watermarkedJSON = new TreeMap<>();
    WatermarkUtil wmUtil = new WatermarkUtil();

    public int embed(String filename, String dstfilename, String wmStr) throws IOException {
        int msgLen = -1;

        // 读取Json
        JsonObject object = (JsonObject) new JsonParser().parse(new FileReader(filename));


        // 嵌入
        TreeMap<String, String> Field = wmUtil.eliminateLevels(object);
        Map<String, List<String>> listField = wmUtil.getList(Field);

        Map<Integer, Map<String, List<String>>> tmp = embedProc(wmStr, listField);
        for (Integer t : tmp.keySet()) msgLen = t;
        Map<String, List<String>> markedListField = tmp.get(msgLen);

        TreeMap<String, String> markedField = updateField(Field, markedListField);

        // 对watermarkedJSON操作，然后更新写入
        for (String key : markedField.keySet()) {
            String newValue = markedField.get(key);
            String key_t = key.replaceAll("/", "").replaceAll("[^A-Za-z0-9]", "");
            ;
            watermarkedJSON.put(key_t, newValue);
        }
        System.out.print("Embedding Successfully to KEY : <<");
        for (String key : markedListField.keySet())
            System.out.print(" " + key + " ");
        System.out.println(">> !");

        // 更新写入
        JsonElement newJsonElement = JsonUpdating(object);
        FileOutputStream out = new FileOutputStream(dstfilename);
        Util.writeJsonStream(out, newJsonElement);

        return msgLen;
    }

    public TreeMap<String, String> updateField(TreeMap<String, String> Field, Map<String, List<String>> markedListField) {
        for (String key : markedListField.keySet()) {
            List<String> tmp = markedListField.get(key);
            for (int i = 0; i < tmp.size(); i++) {
                for (String key2 : Field.keySet()) {
                    String[] strs = key2.split("/");
                    //System.out.println(String.valueOf(i+1) + "  " + strs[strs.length-1]);
                    if (strs.length > 2 && strs[2].equals(String.valueOf(i + 1)) && strs[strs.length - 1].equals(key)) {
                        //System.out.println(Field.get(key2) + " ===> " + tmp.get(i));
                        Field.put(key2, tmp.get(i));
                        break;
                    }
                }
            }
        }
        return Field;
    }

    public Map<Integer, Map<String, List<String>>> embedProc(String wmStr, Map<String, List<String>> listField) {
        // 处理水印，用seed置乱水印
        int seed = 0;
        int msgLen = 0;
        Map<String, List<String>> markedField = new HashMap<>();

        for (String key : listField.keySet()) {
            List<Integer> wmInt = getWmInt(wmStr, seed);
            msgLen = wmInt.size() - 4;
            List<String> arr = embedHandle(wmInt, listField.get(key));
            markedField.put(key, arr);
            seed++;
        }
        Map<Integer, Map<String, List<String>>> marked = new HashMap<>();
        marked.put(msgLen, markedField);
        return marked;
    }

    // 对某一个字段的嵌入
    public List<String> embedHandle(List<Integer> wmInt, List<String> arr) {
        for (int i = 0; i < arr.size(); i++)
            arr.set(i, arr.get(i).toString() + wmInt.get(i % wmInt.size()));

        return arr;
    }

    public JsonElement JsonUpdating(JsonObject object) {
        JsonElement jsonElement = Util.replaceKey(object, watermarkedJSON, "", "", 0);
        System.out.println("Marked data updated successfully!......");

        return jsonElement;

    }


    public List<Integer> getWmInt(String wmStr, int seed) {
        List<Integer> wmBin = Util.String2Bin(wmStr);
        List<Integer> wmIntO = Util.bin2Int(wmBin);
        List<Integer> wmInt = new ArrayList<>();

        // 置乱
        List<Integer> indx = new ArrayList<>();
        for (int i = 0; i < wmIntO.size(); i++) {
            indx.add(i);
        }
        Random rand = new Random(seed);
        Collections.shuffle(indx, rand);
        for (int i = 0; i < wmIntO.size(); i++) {
            wmInt.add(wmIntO.get(indx.get(i)));
        }
        // 保存seed
        wmInt.add(0, seed / 10);
        wmInt.add(0, seed % 10);
        int msgLen = wmBin.size();
        wmInt.add(0, msgLen % 3);

        // 添加头信息
        List<Integer> wm = addHeader(wmInt);

        return wm;
    }

    /*
     * 添加定位信息，将十进制水印放置到8和9之间
     * @param pt : 水印
     * @param wmInt : 返回包含定位信息的水印
     */
    public List<Integer> addHeader(List<Integer> pt) {
        int len = pt.size() + 2;
        List<Integer> newPt = new LinkedList<>();

        for (int i = 0; i < len; i++) {
            if (i == 0) {
                newPt.add(8);
            } else if (i == len - 1)
                newPt.add(9);
            else
                newPt.add(pt.get(i - 1));
        }

        return newPt;
    }
}
