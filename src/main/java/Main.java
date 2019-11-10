/*
请用嵌入后的文件进行检测
 */
public class Main {
    public static void main(String[] args) throws Exception {

        //Excle
//        String fileName = "oquhg3qm8mo";//在这里修改您的待检测的文件名称
//        String fileSuffix_xlsx = ".xlsx";//excle文件后缀xlsx
//        String fileSuffix_xls = ".xls";//excle文件后缀xls
//        String inputPath = "G:\\360水印项目\\数据样本\\Excel\\" + fileName + fileSuffix_xlsx;//输入的路径
//        String outputPath = "G:\\360水印项目\\output\\excle\\" + fileName + "_new" + fileSuffix_xls;//嵌入后的路径
//        String detectionPath = "G:\\360水印项目\\detection\\Excel\\" + fileName + fileSuffix_xls;//待检测文件的路径

        //CSV
//        String fileName = "31e8a49ee29394fabd588662d3d82a83_windows";
//        String inputPath = "G:\\360水印项目\\数据样本\\csv\\" + fileName + ".csv";//输入的路径
//        String outputPath = "G:\\360水印项目\\output\\csv\\" + fileName + "_new.csv";//嵌入后的路径，后续对输出名称做处理，暂时叫new
//        String detectionPath = "G:\\360水印项目\\detection\\csv\\" + fileName + ".csv";

        //JSON
        String fileName = "ta_cb_person_heatmap_collect";
        String fileSuffix = ".json";
        String inputPath = "G:\\360水印项目\\数据样本\\数据样本 - 0505\\结构化\\" + fileName + fileSuffix;//输入的路径
        String outputPath = "G:\\360水印项目\\output\\json\\" + fileName + "_new" + fileSuffix;//嵌入后的路径，后续对输出名称做处理，暂时叫new
        String detectionPath = "G:\\360水印项目\\detection\\json\\" + fileName + fileSuffix;

//        嵌入，使用md5加密（三个格式）
//        System.out.println("======================================嵌入======================================");
//        Embedding embed = new Embedding();
//        int res = embed.run(inputPath, outputPath);
//        System.out.println("=====================================================================================");
//        if (res == -1) { // 返回值为-1表示嵌入失败
//            System.out.println("嵌入失败！");
//            System.out.println("=====================================================================================");
//            return;
//        }
//
        //检测
        System.out.println("======================================检测======================================");
        Detection detection = new Detection();
        int msglen = detection.run(detectionPath);
        System.out.println("=====================================================================================");
        if (msglen == -1) { // 返回值为-1表示失败
            System.out.println("提取错误！");
            System.out.println("=====================================================================================");
            return;
        }
    }
}
