package com.frank.gramturmq.rmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.frank.gramturmq.bean.RequestTurnitinBean;
import com.frank.gramturmq.bean.ResponseTurnitinBean;
import com.frank.gramturmq.bean.TurnitinConst;
import com.frank.gramturmq.fdfs.FdfsUtil;
import com.frank.gramturmq.redis.RedisService;
import com.frank.gramturmq.utils.FileUtils;
import com.frank.gramturmq.utils.SocketClient;
import com.frank.gramturmq.utils.WordAnalyzeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 中继服务.
 *
 * @author 张孝党 2020/01/13.
 * @version V1.00.
 * <p>
 * 更新履历： V1.00 2020/01/13. 张孝党 创建.
 */
@Slf4j
@Component
public class RmqConsumer {

    @Autowired
    private FdfsUtil fdfsUtil;

    @Autowired
    private RedisService redisService;

    @RabbitListener(
            queues = RmqConst.TOPIC_QUEUE,
            containerFactory = "pointTaskContainerFactory")
    public String receiveTopic(String message) {

        try {
            // 加载国际版账号信息
            RequestTurnitinBean turnBean = JSONObject.parseObject(this.redisService.getStringValue(TurnitinConst.TURN_IN_KEY),
                    RequestTurnitinBean.class);
            // 加载UK版账号信息
            RequestTurnitinBean turnBeanUK = JSONObject.parseObject(this.redisService.getStringValue(TurnitinConst.TURN_IN_KEY),
                    RequestTurnitinBean.class);

            log.info("接收的报文为：{}", message);
            String msgHeader = message.substring(0, 2);

            RequestTurnitinBean msgBean = null;
            // 国际版
            if (msgHeader.equals("02") || msgHeader.equals("04")) {
                msgBean = JSONObject.parseObject(message.substring(2),
                        new TypeReference<RequestTurnitinBean>() {
                        });
                FileUtils.downloadFromHttpUrl(
                        msgBean.getOriginalurl(),
                        msgBean.getThesisVpnPath(),
                        msgBean.getThesisName());
            } else if (msgHeader.equals("12") || msgHeader.equals("14")) {
                // UK版
                msgBean = JSONObject.parseObject(message.substring(2),
                        new TypeReference<RequestTurnitinBean>() {
                        });
                FileUtils.downloadFromHttpUrl(
                        msgBean.getOriginalurl(),
                        msgBean.getThesisVpnPath(),
                        msgBean.getThesisName());
            }

            // ADD BY zhangxd ON 20200309 START
            // 论文大小转为本地计算
            ResponseTurnitinBean responseTurnitinBean = null;
            String repMsg = "";
            // 计算论文大小时
            if (msgHeader.equals("04") || msgHeader.equals("14")) {
                responseTurnitinBean = new ResponseTurnitinBean();

                boolean analyzeResult = WordAnalyzeUtil.calcWordCnt(
                        responseTurnitinBean,
                        msgBean.getThesisVpnPath(),
                        msgBean.getThesisName());
                if (!analyzeResult) {
                    responseTurnitinBean.setRetcode("9999");
                    responseTurnitinBean.setRetmsg("解析字数时异常，请联系客服!");
                }
            } else {
                // 调用Socket服务
                repMsg = SocketClient.callServer(
                        TurnitinConst.SOCKET_SERVER,
                        TurnitinConst.SOCKET_PORT,
                        message);
                responseTurnitinBean = JSONObject.parseObject(repMsg, ResponseTurnitinBean.class);
                log.info("调用Socket Server返回的结果为：\n{}", JSON.toJSONString(responseTurnitinBean, SerializerFeature.PrettyFormat));
            }
            // ADD BY zhangxd on 20200309 END

            // 成功时
            if (responseTurnitinBean.getRetcode().equals("0000")) {
                String thesisId = responseTurnitinBean.getThesisId();
                // 下载报告时
                if (msgHeader.equals("03")) {
                    // html报告路径
                    String htmlReport = turnBean.getReportVpnPath() + File.separator + thesisId + ".html";
                    // pdf报告路径
                    String pdfReport = turnBean.getReportVpnPath() + File.separator + thesisId + ".pdf";

                    // 上传HTML报告至FDFS
                    responseTurnitinBean.setHtmlReportUrl(this.fdfsUtil.uploadLocalFile(new File(htmlReport)));
                    // 上传PDF报告至FDFS
                    responseTurnitinBean.setPdfReportUrl(this.fdfsUtil.uploadLocalFile(new File(pdfReport)));
                } else if (msgHeader.equals("13")) {
                    // html报告路径
                    String htmlReport = turnBeanUK.getReportVpnPath() + File.separator + thesisId + ".html";
                    // pdf报告路径
                    String pdfReport = turnBeanUK.getReportVpnPath() + File.separator + thesisId + ".pdf";

                    // 上传HTML报告至FDFS
                    responseTurnitinBean.setHtmlReportUrl(this.fdfsUtil.uploadLocalFile(new File(htmlReport)));
                    // 上传PDF报告至FDFS
                    responseTurnitinBean.setPdfReportUrl(this.fdfsUtil.uploadLocalFile(new File(pdfReport)));
                }
                // 返回
                return JSON.toJSONString(responseTurnitinBean);
            } else {
                // 失败时
                return repMsg;
            }
        } catch (Exception ex) {
            log.info("有异常发生>>>>>>>>>>>>>>>>>:\n{}", ex.getMessage());
            ResponseTurnitinBean rsp = new ResponseTurnitinBean();
            rsp.setRetcode("9999");
            rsp.setRetmsg("有异常发生！");
            return JSON.toJSONString(rsp);
        }
    }
}
