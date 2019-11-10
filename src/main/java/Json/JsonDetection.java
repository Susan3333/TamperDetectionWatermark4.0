package Json;

import Json.utils.Util;
import Json.utils.WatermarkUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class JsonDetection {
    static WatermarkUtil wmUtil = new WatermarkUtil();

    public Map<String, String> detection(String dstfilename) throws IOException {
        // 读取Json
        JsonObject object = (JsonObject)new JsonParser().parse(new FileReader(dstfilename));

        // 提取
        TreeMap<String, String> Field = wmUtil.eliminateLevels(object);
        Map<String, List<String>> listField = wmUtil.getList(Field);

        Map<String, List<Integer>> extWmBins = extractProc(listField);

        Map<String, String> wmStrs = new HashMap<>();
        for(String key : extWmBins.keySet())
            wmStrs.put(key, Util.Bin2String(extWmBins.get(key)));

        return wmStrs;
    }

    public  Map<String, List<Integer>> extractProc(Map<String, List<String>> listField){
        Map<String, List<Integer>> wmBins = new HashMap<>();

        for(String key : listField.keySet()){
            List<String> markedList = listField.get(key);
            List<List<Integer>> wms = new LinkedList<>();

            boolean flag = false;
            int wmsC = -1;
            for(String value : markedList){
                int t = Integer.parseInt(value.substring(value.length()-1));
                if(t == 8 && flag == false){
                    flag = true;
                    wmsC++;
                    wms.add(new LinkedList<>());
                }else if(t == 9 && (wms.get(wms.size()-1).size()) >= 2){
                    flag = false;
                }else{
                    if (flag == true){
                        wms.get(wmsC).add(t);
                    }
                }
            }


            Map<Integer, Integer> cntMap = new TreeMap<>();
            for(int wmInd = 0; wmInd < wms.size(); wmInd++){
                if(!cntMap.containsKey(wms.get(wmInd).size())){
                    cntMap.put(wms.get(wmInd).size(), 1);
                }else {
                    cntMap.replace(wms.get(wmInd).size(), cntMap.get(wms.get(wmInd).size())+1);
                }
            }
            int maxCnt = -1, wmIntLen = -1;
            for(Integer c : cntMap.keySet()){
                if(maxCnt < cntMap.get(c)){
                    maxCnt = cntMap.get(c);
                    wmIntLen = c-3;
                }
            }

            //int wmIntLen = (int)(wmLen / 3) + ((wmLen%3 == 0) ? 0 : 1);
            //删除不满足容量的块
            int cnt = 0;
            for(int i = 0; i < wms.size(); i++){
                if(wms.get(i).size() != wmIntLen+3){
                    wms.remove(i);
                    i--;
                    cnt++;
                }
                else{
                    int seed = wms.get(i).get(1) + wms.get(i).get(2)*10;
                    int wmLen = (wmIntLen - (wms.get(i).get(0) == 0 ? 0 : 1)) * 3 + wms.get(i).get(0);
                    //int seed = wms.get(i).get(0) + wms.get(i).get(1)*10;
                    wms.get(i).remove(0);
                    wms.get(i).remove(0);
                    wms.get(i).remove(0);
                    // 置乱
                    List<Integer> indx = new ArrayList<>();
                    List<Integer> extWmInt = new ArrayList<>();
                    for(int m = 0; m < wms.get(i).size(); m++){
                        indx.add(m);
                        extWmInt.add(0);
                    }
                    Random rand = new Random(seed);
                    Collections.shuffle(indx, rand);

                    for(int m = 0; m < wms.get(i).size(); m++){
                        extWmInt.set(indx.get(m), wms.get(i).get(m));
                    }
                    List<Integer> tmp = Util.int2Bin(extWmInt, wmLen);
                    wms.get(i).clear();
                    wms.get(i).addAll(tmp);
                }
            }

            // 投票
            List<Integer> pt = new LinkedList<>();
            if(wms.size() > 0){
                if(wms.size() < 5){ // 没有水印
                    pt.clear();
                }else{
                    // 水印投票
                    pt.addAll(wms.get(0));
                    for(int i = 1; i < wms.size(); i++){
                        for(int j = 0; j < wms.get(i).size(); j++){
                            pt.set(j, pt.get(j)+wms.get(i).get(j));
                        }
                    }
                    for(int j = 0; j < pt.size(); j++){
                        double t = (double)pt.get(j)/ (double)wms.size();
                        pt.set(j, (int) Math.ceil(t));
                    }
                }
            }
            wmBins.put(key, pt);
        }

        return wmBins;
    }
}
