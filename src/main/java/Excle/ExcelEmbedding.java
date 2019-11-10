package Excle;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;
import java.util.*;


import static Excle.Util.ExcleUtil.*;

public class ExcelEmbedding {

    //嵌入
    public int embed(String inputPath, String outputPath) throws IOException, ClassNotFoundException {
        //读取excle
        List<List<String>> excelContent = getExcle(inputPath);

        //输入不允许嵌入的列数
        int banEmbeddedNum = getBanEmbeddedNum(excelContent);

//        //找出是整数的列数 Arraylist<int>
//        ArrayList<Integer> numericList = findNumericList(excelContent);
//        if (numericList.size() == 0) {
//            System.out.println("没有需要加密的内容");
//            return -1;
//        } else {
//            System.out.println("有" + numericList.size() + "列符合整形条件，待加密");
//        }


        //找出是小数的列数Arraylist<int>
        ArrayList<Integer> decimalList = findDecimalList(excelContent);
        if (decimalList.size() == 0) {
            System.out.println("不存在可以嵌入的位置");
            return -1;
        } else {
            if (decimalList.contains(banEmbeddedNum - 1)) {
                System.out.println("第" + banEmbeddedNum + "列不可嵌入");
                for (int i = 0; i < decimalList.size(); i++) {
                    if ((banEmbeddedNum - 1) == decimalList.get(i)) {
                        decimalList.remove(i);
                    }
                }
                System.out.println("去掉不可嵌入的列后，有" + decimalList.size() + "列符合浮点型条件，待嵌入");
                if (decimalList.size() == 0) {
                    System.out.println("不存在可以嵌入的位置");
                    return -1;
                }
            } else {
                System.out.println("有" + decimalList.size() + "列符合条件，待嵌入");
            }


        }

        //对每一行进行加密，但是不算浮点型的小数部分
        //对excelContent深复制为temp,不改变excelContent本身
        List<List<String>> temp =deepCopy(excelContent);
        String[] md5Int = getMd5Int(temp, decimalList);//每行待嵌入的MD5码的前decimalList.size()位，md5Int[0]是null

//        for (int i = 0; i < temp.get(1).size(); i++) {
//            System.out.print(temp.get(1).get(i) + " ");
//        }
//        System.out.println();
//        for (int i = 0; i < excelContent.get(1).size(); i++) {
//            System.out.print(excelContent.get(1).get(i) + " ");
//        }


        //把第一个小数的小数部分替换成md5码的第1位，把第二个小数的小数部分替换成md5码的第2位
        for (int i = 1; i < md5Int.length; i++) {
            for (int j = 0; j < md5Int[i].length(); j++) {
                int a = decimalList.get(j);
                List list = excelContent.get(i);
                String str = (String) list.get(a);
                String strNum = str.substring(0, str.lastIndexOf("."));//截取从字符串开始到小数点位置的字符串，就是整数部分
                strNum = strNum + ".";
                strNum += md5Int[i].charAt(j);
                strNum=strNum.trim();
                list.set(a, strNum);
            }
        }


//        for (int i = 0; i < 5; i++) {
//            List list = excelContent.get(i);
//            for (int j = 0; j < list.size(); j++) {
//                System.out.print(list.get(j) + " ");
//            }
//            System.out.println();
//            System.out.println(md5Int[i]);
//        }

        //生成新的excle
        createNewExcel(outputPath, excelContent);
        return 0;
    }


    private static void createNewExcel(String outputPath, List<List<String>> excelContent) {
        File newFile = new File(outputPath);
        HSSFWorkbook newWorkbook = new HSSFWorkbook();
        HSSFSheet newSheet = newWorkbook.createSheet();
        for (int i = 0; i < excelContent.size(); i++) {
            List list = excelContent.get(i);
            HSSFRow row = newSheet.createRow(i);
            for (int j = 0; j < list.size(); j++) {
                row.createCell(j).setCellValue((String) list.get(j));
            }
        }
        try {
            newWorkbook.write(new FileOutputStream(newFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

