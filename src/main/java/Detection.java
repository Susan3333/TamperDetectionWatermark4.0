import CSV.CSVDetection;
import Excle.ExcleDetection;
import Json.JsonDetection;
import Json.utils.getWaterMark;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import static Json.utils.Util.getCorrespondenceOfIndex;

public class Detection {
    public int run(String detectionPath) throws IOException {
        int res = 0;
        Map<String, String> wmStrs = new TreeMap<>();
        String suffixString = detectionPath.substring(detectionPath.lastIndexOf("."));//后缀
        if (suffixString.equals(".xls") || suffixString.equals(".xlsx")) {
            ExcleDetection detection = new ExcleDetection();
            detection.detection(detectionPath);
        } else if (suffixString.equals(".csv")) {
            CSVDetection csvDetection = new CSVDetection();
            csvDetection.detection(detectionPath);
        } else if (suffixString.equals(".json")) {
            JsonDetection jsonDetection = new JsonDetection();
            String waterMark = getWaterMark.getWaterMark(detectionPath);
            System.out.println("waterMark = " + waterMark);
//            System.out.println(waterMark.length());
            wmStrs = jsonDetection.detection(detectionPath);
            String waterMarkDetection = new String();
            for (String key : wmStrs.keySet()) {
                if (wmStrs.get(key).length() == 0)
                    System.out.println("there is no water mark in : <<" + key + ">>");
                else {
                    System.out.println("<<" + key + ">> contains watermark ==> " + wmStrs.get(key));
                }
                waterMarkDetection = wmStrs.get(key);
            }
//            System.out.println(waterMarkDetection);
//            System.out.println(waterMark.equals(waterMarkDetection));
            if (waterMark.equals(waterMarkDetection)) {
                System.out.println("文件没有发生改动");
            } else {
                for (int i = 0; i < waterMark.length(); i++) {
                    if (waterMark.charAt(i) != waterMarkDetection.charAt(i)) {
//                        System.out.println("检测到第" + (i * getWaterMark.b + 1) + "-" + (i * getWaterMark.b + getWaterMark.b + 1) + "行发生了改动");

                        TreeMap<Integer, Integer> intMap = getCorrespondenceOfIndex(detectionPath);
                        //这里的行数是json中不包括{}，[]的行数
                        //根据key找value
                        System.out.println("检测到第" + (intMap.get(i * getWaterMark.b )+1 )+ "-" + (intMap.get(i * getWaterMark.b + getWaterMark.b )+1 )+ "行发生了改动");

                    }

                }

            }

        } else {
            System.out.println("此文件无法操作");
            return -1;
        }
        return res;
    }
}
