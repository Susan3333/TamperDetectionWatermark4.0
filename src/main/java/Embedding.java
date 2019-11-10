import CSV.CSVEmbedding;
import Excle.ExcelEmbedding;
import Json.JsonEmbedding;
import Json.utils.getWaterMark;

public class Embedding {
    public int run(String inputPath, String outputPath) throws Exception {
        int res = 0;
        String suffixString = inputPath.substring(inputPath.lastIndexOf("."));//后缀
        if (suffixString.equals(".xls") || suffixString.equals(".xlsx")) {
            ExcelEmbedding embed = new ExcelEmbedding();
            res = embed.embed(inputPath, outputPath);
            System.out.println("嵌入后文件另存为 " + outputPath);
        } else if (suffixString.equals(".csv")) {
            CSVEmbedding embed = new CSVEmbedding();
            res = embed.embed(inputPath, outputPath);
            if (res == 0)
                System.out.println("嵌入后文件另存为" + outputPath);
        } else if (suffixString.equals(".json")) {
            JsonEmbedding embed = new JsonEmbedding();
            String waterMark = getWaterMark.getWaterMark(inputPath);
//            String waterMark="123";
            res = embed.embed(inputPath, outputPath, waterMark);
            if (res == 0)
                System.out.println("嵌入后文件另存为" + outputPath);
        } else {
            System.out.println("此文件无法操作");
            return -1;
        }
        return res;
    }
}
