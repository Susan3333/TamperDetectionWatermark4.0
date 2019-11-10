package CSV;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static CSV.CSVEmbedding.getCSV;
import static CSV.CSVEmbedding.getPrettyNumber;
import static Excle.Util.ExcleUtil.*;

public class CSVDetection {
    public void detection(String detectionPath) {
        //读取excle
        List<List<String>> csvContent = getCSV(detectionPath);
        for (int i = 1; i < csvContent.size(); i++) {
            List list = csvContent.get(i);
            for (int j = 0; j < list.size(); j++) {
                if (isNumeric((String) list.get(j))||isDouble((String) list.get(j))) {
                    list.set(j, getPrettyNumber((String) list.get(j)));
                }
            }
        }

        int banEmbeddedNum = getBanEmbeddedNum(csvContent);

        //找出是整数的列数 Arraylist<int>
        ArrayList<Integer> numericList = findNumericList(csvContent);

//        System.out.print("整数列有：");
//        for (int i = 0; i < numericList.size(); i++) {
//            System.out.print((numericList.get(i)+1) + " ");
//        }
        //找出是小数的列数Arraylist<int>
        ArrayList<Integer> decimalList = findDecimalList(csvContent);

        //如果是小数的列数中有banEmbeddedNum，就移除它
        if (decimalList.contains(banEmbeddedNum-1)) {
            for (int i = 0; i < decimalList.size(); i++) {
                if ((banEmbeddedNum - 1) == decimalList.get(i)) {
                    decimalList.remove(i);
                }
            }
        }

        //提取MD5值
        String[] md5Decimal = getDecimal(csvContent, decimalList);
        String[] md5Int = getMd5Int(csvContent, decimalList);//每行待嵌入的MD5码的前decimalList.size()位

        if (Arrays.equals(md5Decimal, md5Int))
            System.out.println("文件没有发生改动");
        else {
            for (int i = 1; i < md5Decimal.length; i++) {
                if (!md5Decimal[i].equals(md5Int[i])) {
                    System.out.println("检测到第" + (i + 1) + "行发生了改动");
//                    break;
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
