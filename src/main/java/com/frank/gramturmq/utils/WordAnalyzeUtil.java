package com.frank.gramturmq.utils;

import com.frank.gramturmq.bean.ResponseTurnitinBean;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * word文件分析类.
 *
 * @author 张孝党 2020/03/09.
 * @version V1.00.
 * <p>
 * 更新履历： V1.00 2020/03/09. 张孝党 创建.
 */
@Slf4j
public class WordAnalyzeUtil {

    /**
     * 计算word文档大小及字数.
     */
    public static boolean calcWordCnt(ResponseTurnitinBean responseTurnitinBean, String filePath, String fileName) {

        // 解析结果
        boolean calcResult = true;
        // 开始时间
        long startTime = System.currentTimeMillis();

        try {
            // 文件大小
            String fileSize = getFileSize(new File(Paths.get(filePath, fileName).toString()).length());
            responseTurnitinBean.setFileSize(fileSize);
            log.info("论文[{}]大小为[{}]", fileName, fileSize);

            // 论文路径
            Path path = Paths.get(filePath, fileName);

            // txt文档时需要先转word
            if (fileName.substring(fileName.lastIndexOf(".") + 1).equals("txt")) {
                // 先读取txt内容
                StringBuffer sb = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path.toString()), "UTF-8"));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                reader.close();
                log.info("读取的内容为：\n[{}]", sb.toString());

                // 保存为word
                String docxFileName = CommonUtil.getUUid() + ".docx";
                path = Paths.get(filePath, docxFileName);
                XWPFDocument docx = new XWPFDocument();
                XWPFParagraph paragraph = docx.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(sb.toString());
                OutputStream os = new FileOutputStream(path.toString());
                docx.write(os);
                docx.close();
                os.close();
                log.info("写word完成");
            }

            ActiveXComponent wordApp = new ActiveXComponent("Word.Application");
            // word应用程序不可见
            wordApp.setProperty("Visible", false);
            // 返回wrdCom.Documents的Dispatch
            // Documents表示word的所有文档窗口（word是多文档应用程序）
            Dispatch wrdDocs = wordApp.getProperty("Documents").toDispatch();
            // 调用wrdCom.Documents.Open方法打开指定的word文档，返回wordDoc
            Dispatch wordDoc = Dispatch.call(wrdDocs, "Open", path.toString(), false, true, false).toDispatch();

            // 计算页数
            Dispatch selection = Dispatch.get(wordApp, "Selection").toDispatch();
            String pages = Dispatch.call(selection, "Information", new Variant(4)).toString();
            log.info("论文[{}]共[{}]页", fileName, fileSize);
            responseTurnitinBean.setPageCount(pages);

            Dispatch activeDocument = Dispatch.get(wordApp, "ActiveDocument").toDispatch();
            Dispatch builtInDocumentProperties = Dispatch.get(activeDocument, "BuiltInDocumentProperties").toDispatch();
            int charS = Dispatch.call(builtInDocumentProperties, "Item", new Variant(16)).toInt();
            int wordCnt = Dispatch.call(builtInDocumentProperties, "Item", new Variant(15)).toInt();
            log.info("论文[{}]共[{}]字[{}]字符", fileName, wordCnt, charS);
            responseTurnitinBean.setWordCount(String.valueOf(wordCnt));
            responseTurnitinBean.setCharCount(String.valueOf(charS));

            // 关闭文档且不保存
            Dispatch.call(wordDoc, "Close", new Variant(false));
            // 退出进程对象
            wordApp.invoke("Quit", new Variant[] {});
        } catch (Exception e) {
            log.error("解析word字数时异常:\n{}", e.getMessage());
            calcResult = false;
        }

        // 结束时间
        long endTime = System.currentTimeMillis();
        log.info("共使用" + (endTime - startTime) / 1000 + "秒");

        return calcResult;
    }

    // 计算文档大小
    private static String getFileSize(long size) {

        double ss = size;

        // 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (ss < 1024) {
            return String.valueOf(ss) + "B";
        } else {
            ss = ss / 1024;
        }
        // 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        // 因为还没有到达要使用另一个单位的时候
        // 接下去以此类推
        if (ss < 1024) {
            return String.format("%.2f", ss) + "KB";
        } else {
            ss = ss / 1024;
        }

        if (ss < 1024) {
            // 因为如果以MB为单位的话，要保留最后1位小数，
            // 因此，把此数乘以100之后再取余
            return String.format("%.2f", ss) + "MB";
        } else {
            // 否则如果要以GB为单位的，先除于1024再作同样的处理
            ss = ss / 1024;
            return String.format("%.2f", ss) + "GB";
        }
    }

    public static void main(String[] args) {
        ResponseTurnitinBean responseTurnitinBean = new ResponseTurnitinBean();
        calcWordCnt(responseTurnitinBean, "C:\\Users\\Administrator\\Desktop", "测试11-32658-3.2M.docx");
    }
}
