package CSV;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Excle.Util.ExcleUtil.*;

public class CSVEmbedding {
    public static String getPrettyNumber(String number) {
        return BigDecimal.valueOf(Double.parseDouble(number)).stripTrailingZeros().toPlainString();
    }


    public int embed(String inputPath, String outputPath) throws IOException, ClassNotFoundException {
        //读取CSV
        List<List<String>> csvContent = getCSV(inputPath);

        for (int i = 1; i < csvContent.size(); i++) {
            List list = csvContent.get(i);
            for (int j = 0; j < list.size(); j++) {
                if (isNumeric((String) list.get(j)) || isDouble((String) list.get(j))) {
                    list.set(j, getPrettyNumber((String) list.get(j)));
                }
            }
        }
        //输入不允许嵌入的列数
        int banEmbeddedNum = getBanEmbeddedNum(csvContent);

        //找出是整数的列数 Arraylist<int>
//        ArrayList<Integer> numericList = findNumericList(csvContent);
//        if (numericList.size() == 0) {
//            System.out.println("没有需要加密的内容");
//            return -1;
//        } else {
//            System.out.println("有" + numericList.size() + "列符合整形条件，待加密");
//        }
//        System.out.print("整数列有：");
//        for (int i = 0; i < numericList.size(); i++) {
//            System.out.print((numericList.get(i)+1) + " ");
//        }

        //找出是小数的列数Arraylist<int>
        ArrayList<Integer> decimalList = findDecimalList(csvContent);
//        System.out.print("小数列有：");
//        for (int i = 0; i < decimalList.size(); i++) {
//            System.out.print((decimalList.get(i)+1) + " ");
//        }
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
        //对csvContent深复制为temp,不改变excelContent本身
        List<List<String>> temp = deepCopy(csvContent);
//        for (int i = 0; i < temp.get(1).size(); i++) {
//            System.out.print(temp.get(1).get(i) + " ");
//        }
//        System.out.println();
//        for (int i = 0; i < csvContent.get(1).size(); i++) {
//            System.out.print(csvContent.get(1).get(i) + " ");
//        }
        String[] md5Int = getMd5Int(temp, decimalList);//每行待嵌入的MD5码的前decimalList.size()位


        //把第一个小数的小数部分替换成md5码的第1位，把第二个小数的小数部分替换成md5码的第2位
        for (int i = 1; i < md5Int.length; i++) {
            for (int j = 0; j < md5Int[i].length(); j++) {
                int a = decimalList.get(j);
                List list = csvContent.get(i);
                String str = (String) list.get(a);
                String strNum = str.substring(0, str.lastIndexOf("."));//截取从字符串开始到小数点位置的字符串，就是整数部分
                strNum = strNum + ".";
                strNum += md5Int[i].charAt(j);
                list.set(a, strNum);
            }
        }

        createNewCsv(outputPath, csvContent);
        return 0;
    }

    public static List<List<String>> getCSV(String inputPath) {
        List<List<String>> csvContent = new ArrayList<List<String>>();
        CsvReader reader = null;
        try {
            reader = new CsvReader(inputPath, ',', Charset.forName("GBK"));
            // 逐条读取记录，直至读完
            while (reader.readRecord()) {
                String str[] = reader.getValues();
                csvContent.add(Arrays.asList(str));
            }
        } catch (Exception e) {
            System.out.println("读取CSV出错..." + e);
        } finally {
            if (null != reader) {
                reader.close();
            }
        }
        reader.close();
        return csvContent;
    }

    private void createNewCsv(String outputPath, List<List<String>> csvContent) {
        try {
            // 创建CSV写对象 例如:CsvWriter(文件路径，分隔符，编码格式);
            CsvWriter csvWriter = new CsvWriter(outputPath, ',', Charset.forName("GBK"));
            // 写内容
            for (int i = 0; i < csvContent.size(); i++) {
                List list = csvContent.get(i);
                String[] strings = new String[list.size()];
                list.toArray(strings);
                csvWriter.writeRecord(strings);
            }
            csvWriter.close();
            System.out.println("--------CSV文件已经写入--------");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
