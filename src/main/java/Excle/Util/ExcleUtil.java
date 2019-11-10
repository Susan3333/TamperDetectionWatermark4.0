package Excle.Util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ExcleUtil {
    //excle的内容，用List<List<String>>
    public static List setExcelContent(Workbook workbook) {
        List<List<String>> excelContent = new ArrayList<List<String>>();
        Sheet sheet;
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            sheet = workbook.getSheetAt(i);//获取sheet
            for (int j = 0; j < sheet.getPhysicalNumberOfRows(); j++) {
                List<String> rowContent = new ArrayList<String>();
                Row row = sheet.getRow(j); // 获取行
                for (int k = 0; k < row.getPhysicalNumberOfCells(); k++) {
                    Cell cell = row.getCell(k); //获取单元格
//                    if (cell.getCellType()==Cell.CELL_TYPE_NUMERIC)
//                        cell.setCellType(Cell.CELL_TYPE_STRING);
//                    System.out.println(cell.getCellType());
//                    if(row.getCell(0)!=null){
//                        row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
//                        rowContent.add(row.getCell(0).getStringCellValue());
//                    }
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_STRING://字符串  poi4以上版本用case STRING:
                            case Cell.CELL_TYPE_BOOLEAN://boolean类型  poi4以上版本用case BOOLEAN:
//                            System.out.print(cell.getBooleanCellValue());
                                rowContent.add(cell.getRichStringCellValue().getString());
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    Date date = cell.getDateCellValue();
                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                    rowContent.add(df.format(date));
                                } else {
                                    rowContent.add(String.valueOf(cell.getNumericCellValue()));
                                }

                                break;
//                            System.out.print(" ");
                            default:
                        }
//                        Object columnValue = null;
//                        if (cell != null) {
//                            DecimalFormat df = new DecimalFormat("0");// 格式化 number
//                            // String
//                            // 字符
//                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化日期字符串
//                            DecimalFormat nf = new DecimalFormat("0");// 格式化数字
//                            switch (cell.getCellType()) {
//                                case 1:
//                                    columnValue = cell.getStringCellValue();
//                                    break;
//                                case 0:
//                                    if ("@".equals(cell.getCellStyle().getDataFormatString())) {
//                                        columnValue = df.format(cell.getNumericCellValue());
//                                    } else if ("General".equals(cell.getCellStyle().getDataFormatString())) {
//                                        columnValue = nf.format(cell.getNumericCellValue());
//                                    } else {
//                                        columnValue = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
//                                    }
//                                    break;
//                                case 5:
//                                    columnValue = cell.getBooleanCellValue();
//                                    break;
//                                case 4:
//                                    columnValue = "";
//                                    break;
//                                case 3:
//                                    // 格式单元格
//                                    FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
//                                    evaluator.evaluateFormulaCell(cell);
//                                    CellValue cellValue = evaluator.evaluate(cell);
//                                    columnValue = cellValue.getNumberValue();
//                                    break;
//                                default:
//                                    columnValue = cell.toString();
//                            }
//                        }
                    }
//                    excelContent.add(rowContent);
//                System.out.println();
                }
                excelContent.add(rowContent);
            }
            return excelContent;
        }
        return null;
    }

    public static List<List<String>> getExcle(String inputPath) {
        //读取excle
        Workbook originalWorkbook = null;
        File originalFile = new File(inputPath);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(originalFile);
            originalWorkbook = WorkbookFactory.create(fileInputStream);
        } catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return setExcelContent(originalWorkbook);
    }


    public static ArrayList<Integer> findDecimalList(List<List<String>> excelContent) {
        ArrayList<Integer> decimalList = new ArrayList<Integer>();
        List rowOne = excelContent.get(1);//第一行是表头不作考虑，从第二行开始考虑
        for (int i = 0; i < rowOne.size(); i++) {
            if (isDouble((String) rowOne.get(i)) && (!isNumeric((String) rowOne.get(i)))) {
                decimalList.add(i);
            }
        }
        return decimalList;
    }

    public static ArrayList<Integer> findNumericList(List<List<String>> excelContent) {
        ArrayList<Integer> numericList = new ArrayList<Integer>();
        List rowOne = excelContent.get(1);
        for (int i = 0; i < rowOne.size(); i++) {
            if (isNumeric((String) rowOne.get(i))) {
                numericList.add(i);
            }
        }
        return numericList;
    }

    //判断是否整数
//    public static boolean isNumeric(String str) {
//        for (int i = str.length(); --i >= 0; ) {
//            if (!Character.isDigit(str.charAt(i))) {// 不是数字，并且不是“-”负号
//                return false;
//            }
//        }
//        return true;
//    }
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if ((chr < 48 || chr > 57 )&& chr!=45)
                return false;
        }
        return true;
    }

    //判断是否是浮点型
    public static boolean isDouble(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static int getBanEmbeddedNum(List<List<String>> excelContent) {
        System.out.println("请输入不可嵌入的字段的列数：（例如：1）");
        System.out.println("范围是1-" + excelContent.get(0).size());
        Scanner sc = new Scanner(System.in);
        int banEmbeddedNum = sc.nextInt();
        while (banEmbeddedNum < 1 || banEmbeddedNum > excelContent.get(0).size()) {
            System.out.println("请输入正确范围的数字");
            banEmbeddedNum = sc.nextInt();
        }
        System.out.println("输入正确!");
        return banEmbeddedNum;
    }

    //对每一行进行加密，但是不算浮点型的小数部分
    public static String[] getMd5Int(List<List<String>> temp, ArrayList<Integer> decimalList) {

        String[] md5Int = new String[temp.size()];
        for (int i = 1; i < temp.size(); i++) {//第一行是表头不作考虑
            List list = temp.get(i);
            for (int j = 0; j < list.size(); j++) {
                //把小数变成整型
                if (isDouble((String) list.get(j)) && (!isNumeric((String) list.get(j)))) {//如果是浮点型，变成整形
                    String str = (String) list.get(j);
                    int n = str.lastIndexOf(".");
//                    System.out.println("i是" + i + "j是" + j + "n是" + n);
                    str = str.substring(0, n);
                    list.set(j, str);
                }
                md5Int[i] += list.get(j);
            }
        }

        //加密
        for (int i = 1; i < md5Int.length; i++) {
            md5Int[i] = new BigInteger(md5(md5Int[i].substring(4)), 16).toString();
            md5Int[i] = md5Int[i].substring(0, decimalList.size());
        }
        return md5Int;
    }


    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    //MD5加密
    public static String md5(String text) {
        //加密后的字符串
        String encodeStr = DigestUtils.md5Hex(text);
//        System.out.println("MD5加密后的字符串为:encodeStr=" + encodeStr);
        return encodeStr;
    }
}
