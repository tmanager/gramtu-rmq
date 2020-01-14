package com.frank.gramturmq.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Socket客户端.
 *
 * @author 张孝党 2018/07/24.
 * @version V1.00.
 * <p>
 * 更新履历： V1.00 2018/07/24 张孝党 创建.
 */
@Slf4j
public class SocketClient {

    /**
     * 调用平台.
     *
     * @return Server返回的报文.
     */
    public static String callServer(String pstrIp, String pintPort, String pstrYfsdbw) {

        // 返回值
        String strResult = "";

        try {
            // 中心IP
            String strFesbIP = pstrIp;
            // 金融中心SocketServer端口号
            int intPort = Integer.valueOf(pintPort);

            // 建立客户端socket连接，指定服务器位置及端口
            Socket client = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(strFesbIP, intPort);
            client.connect(socketAddress, 30 * 10 * 1000);
            // 得到socket读写流
            OutputStream dos = client.getOutputStream();
            // 输入流
            InputStream is = client.getInputStream();
            DataInputStream in = new DataInputStream(client.getInputStream());

            // 转换长度
            String strHeader = Integer.toString(pstrYfsdbw.getBytes("utf-8").length);
            log.info("发送的报文长度为：" + strHeader);
            strHeader = StringUtils.leftPad(strHeader, 8, "0");
            log.info("补充后的长度为：" + strHeader);
            // 要发送报文
            String strBuf = strHeader + pstrYfsdbw;
            log.info("发送给turnitin-api的报文为：" + strBuf);
            dos.write(strBuf.getBytes("UTF-8"));
            dos.flush();

            // 读取长度
            byte[] blength = CommonUtil.readBytes(in, 8);
            int intLength = Integer.valueOf(new String(blength));
            log.info(">>>>>>>>>接收到的报文长度为：" + intLength);

            // 读取数据
            byte[] byteData = new byte[intLength];
            byteData = CommonUtil.readBytes(in, intLength);
            strResult = new String(byteData, "utf-8");
            log.info(">>>>>>>>>接收到的报文为：" + strResult);

            in.close();
            is.close();
            dos.close();
            client.close();
        } catch (Exception e) {
            log.error("调用turnitin-api时异常:\n" + e.getMessage());
        }

        // 返回
        return strResult;
    }
}
