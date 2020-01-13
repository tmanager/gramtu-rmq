package com.frank.gramturmq.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回类.
 *
 * @author 张孝党 2019/09/10.
 * @version V1.00.
 * <p>
 * 更新履历： V1.00 2019/09/10 张孝党 创建.
 */
@Getter
@Setter
public class ResponseTurnitinBean {

    // 返回码
    private String retcode = "0000";

    // 返回信息
    private String retmsg = "正常完成";

    // 日志列表
    private List<String> logList = new ArrayList<String>();

    // 论文ID
    private String thesisId = "";

    // 文档大小
    private String fileSize = "";

    // 字数
    private String wordCount = "";

    // 字符数
    private String charCount = "";

    // 页数
    private String pageCount = "";

    // 重复率
    private String rate = "";

    // pdf路径
    private String pdfReportUrl = "";

    // html路径
    private String htmlReportUrl = "";

    /**
     * 构造方法1.
     */
    public ResponseTurnitinBean() {

    }

    /**
     * 构造方法2.
     */
    public ResponseTurnitinBean(String retCode, String retMsg) {
        this.retcode = retCode;
        this.retmsg = retMsg;
    }

}
