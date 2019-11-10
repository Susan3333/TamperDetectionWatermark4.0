package Excle;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Excle.Util.ExcleUtil.*;

public class ExcleDetection {

    public void detection(String detectionPath) {
        //读取excle
        List<List<String>> excelContent = getExcle(detectionPath);

        int banEmbeddedNum = getBanEmbeddedNum(excelContent);

        //找出是整数的列数 Arraylist<int>
        ArrayList<Integer> numericList = findNumericList(excelContent);

        //找出是小数的列数Arraylist<int>
        ArrayList<Integer> decimalList = findDecimalList(excelContent);

        //如果是小数的列数中有banEmbeddedNum，就移除它
        if (decimalList.contains(banEmbeddedNum - 1)) {
            for (int i = 0; i < decimalList.size(); i++) {
                if ((banEmbeddedNum - 1) == decimalList.get(i)) {
                    decimalList.remove(i);
                }
            }
        }

        //提取MD5值
        String[] md5Decimal = getDecimal(excelContent, decimalList);
        String[] md5Int = getMd5Int(excelContent,  decimalList);//每行待嵌入的MD5码的前decimalList.size()位

        if (Arrays.equals(md5Decimal, md5Int))
            System.out.println("文件没有发生改动");
        else {
            for (int i = 1; i < md5Decimal.length; i++) {
                if (!md5Decimal[i].equals(md5Int[i])) {
                    System.out.println("检测到第" + (i + 1) + "行发生了改动");
                }
            }
        }


    }

    private String[] getDecimal(List<List<String>> excelContent, ArrayList<Integer> decimalList) {
        String[] md5Decimal = new String[excelContent.size()];
        for (int i = 1; i < excelContent.size(); i++) {
            List list = excelContent.get(i);
            for (int j = 0; j < decimalList.size(); j++) {
                int a = decimalList.get(j);
                String str = (String) list.get(a);
//                System.out.println(str);
                str = str.substring(str.lastIndexOf(".") + 1, str.lastIndexOf(".") + 2);
                md5Decimal[i] += str;
            }
            md5Decimal[i] = md5Decimal[i].substring(4);
        }
        return md5Decimal;
    }


}
