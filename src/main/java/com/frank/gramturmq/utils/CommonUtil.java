package com.frank.gramturmq.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * Copyright(C) ShanDongYinFang 2019.
 * <p>
 * 公共方法类.
 *
 * @author 张孝党 2019/06/03.
 * @version V0.0.2.
 * <p>
 * 更新履历： V0.0.1 2019/06/03 张孝党 创建.
 */
public class CommonUtil {

    /**
     * 获取UUID.
     *
     * @return 32位uuid.
     */
    public static String getUUid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 读取报文.
     */
    public static byte[] readBytes(InputStream in, long length) throws Exception {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read = 0;
        while (read < length) {
            int cur = in.read(buffer, 0, (int) Math.min(1024, length - read));
            if (cur < 0) {
                break;
            }
            read += cur;
            bo.write(buffer, 0, cur);
        }
        return bo.toByteArray();
    }
}
