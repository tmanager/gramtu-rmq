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
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class RmqConsumer {

    @Autowired
    private FdfsUtil fdfsUtil;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RmqConst.TOPIC_QUEUE)
    public String receiveTopic(String message) throws Exception {

        // 加载国际版账号信息
        RequestTurnitinBean turnBean = JSONObject.parseObject(this.redisService.getStringValue(TurnitinConst.TURN_IN_KEY),
                RequestTurnitinBean.class);
        // 加载UK版账号信息
        RequestTurnitinBean turnBeanUK = JSONObject.parseObject(this.redisService.getStringValue(TurnitinConst.TURN_IN_KEY),
                RequestTurnitinBean.class);

        log.info("接收的报文为：{}", message);
        String msgHeader = message.substring(0, 2);

        // 国际版
        if (msgHeader.equals("02") || msgHeader.equals("04")) {
            RequestTurnitinBean msgBean = JSONObject.parseObject(message.substring(2),
                    new TypeReference<RequestTurnitinBean>() {
                    });
            FileUtils.downloadFromHttpUrl(
                    msgBean.getOriginalurl(),
                    msgBean.getThesisVpnPath(),
                    msgBean.getThesisName());
        } else if (msgHeader.equals("12") || msgHeader.equals("14")) {
            // UK版
            RequestTurnitinBean msgBean = JSONObject.parseObject(message.substring(2),
                    new TypeReference<RequestTurnitinBean>() {
                    });
            FileUtils.downloadFromHttpUrl(
                    msgBean.getOriginalurl(),
                    msgBean.getThesisVpnPath(),
                    msgBean.getThesisName());
        }

        // 调用Socket服务
        String repMsg = SocketClient.callServer(
                TurnitinConst.SOCKET_SERVER,
                TurnitinConst.SOCKET_PORT,
                message);
        ResponseTurnitinBean responseTurnitinBean = JSONObject.parseObject(repMsg, ResponseTurnitinBean.class);
        log.info("调用Socket Server返回的结果为：\n{}", JSON.toJSONString(responseTurnitinBean, SerializerFeature.PrettyFormat));

        // 成功时
        if (responseTurnitinBean.getRetcode().equals("0000")) {
            String thesisId = responseTurnitinBean.getThesisId();
            // 下载报告时
            if (msgHeader.equals("03")) {
                // html报告路径
                String htmlReport = turnBean.getReportVpnPath() + File.separator + thesisId + ".html";
                // pdf报告路径
                String pdfReport = turnBean.getReportVpnPath() + File.separator + thesisId + ".pdf";

                // 上传至FDFS
                responseTurnitinBean.setHtmlReportUrl(this.fdfsUtil.uploadLocalFile(new File(htmlReport)));
                responseTurnitinBean.setPdfReportUrl(this.fdfsUtil.uploadLocalFile(new File(pdfReport)));
            } else if (msgHeader.equals("13")) {
                // html报告路径
                String htmlReport = turnBeanUK.getReportVpnPath() + File.separator + thesisId + ".html";
                // pdf报告路径
                String pdfReport = turnBeanUK.getReportVpnPath() + File.separator + thesisId + ".pdf";

                // 上传至FDFS
                responseTurnitinBean.setHtmlReportUrl(this.fdfsUtil.uploadLocalFile(new File(htmlReport)));
                responseTurnitinBean.setPdfReportUrl(this.fdfsUtil.uploadLocalFile(new File(pdfReport)));
            }
            // 返回
            return JSON.toJSONString(responseTurnitinBean);
        } else {
            // 失败时
            return repMsg;
        }
    }
}
