package Json.utils;

import com.google.gson.*;

import java.util.*;

 public class WatermarkUtil {
    public TreeMap<String, String> JSON = new TreeMap<>();
    public int sum = 0;
    public int valid = 0;

    public TreeMap<String, String> eliminateLevels(JsonObject object){
        // clear
        recursiveEliminateHelper(object,"",0);

        if(JSON.size() == 0)
            System.out.println("there is no field to embedding!\n");
//        for(String key:JSON.keySet())
//            System.out.println(key+"   "+JSON.get(key));

        System.out.println("-------------------------------------------------");

        System.out.println("Sum of KEYS: " + sum + ". Sum of Valid: " + valid);
        return JSON;
    }

    public void recursiveEliminateHelper(JsonElement object, String prefix, int arrayCount){
        if(arrayCount>0){
            //which indicates the parent object is an array
            prefix += ("/" + arrayCount);
            arrayCount = 0;
        }
        if(object instanceof JsonObject){
            // continue the recursion
            for(Map.Entry<String, JsonElement> entry:((JsonObject) object).entrySet()){
                recursiveEliminateHelper(entry.getValue(),prefix+"/"+entry.getKey(),arrayCount);
            }
        }else if(object instanceof JsonArray){
            int count = 1;
            for (Iterator<JsonElement> iter = ((JsonArray) object).iterator(); iter.hasNext();){
                recursiveEliminateHelper(iter.next(),prefix,count);
                count++;
            }
        }else if(!(object instanceof JsonNull)){
            // instance of JsonPrimitive
            String value = ((JsonPrimitive)object).getAsString();
            if (!Util.isJSON(value) && Util.isDouble(value)){
                //valid
                //JSON.put(prefix.replaceAll("[^A-Za-z0-9]",""),value);
                JSON.put(prefix,value);
                valid++;
            }
            sum++;
        }
    }

    public Map<String, List<String>> getList(TreeMap<String, String> Field){
        //解析字段
        Set<String> KEYS = new HashSet<>();
        for(String s : Field.keySet()){
            String[] strs = s.split("/");
            KEYS.add(strs[strs.length-1]);
        }

        Map<String, List<String>> vFeild = new HashMap<>();
        // 按顺序排放
        for(String key : KEYS){
            String[] vFeild_t = new String[JSON.size()];
            int maxI = 0;
            for(String s : Field.keySet()){
                String[] strs = s.split("/");
                if(strs[strs.length-1].equals(key)){
                    int index = Integer.parseInt(strs[2])-1;
                    vFeild_t[index] = Field.get(s);
                    if(index > maxI)
                        maxI = index;
                }
            }

            List<String> t = Arrays.asList(vFeild_t);
            t = t.subList(0,maxI+1);
            vFeild.put(key, t);
        }
        return vFeild;
    }
}
