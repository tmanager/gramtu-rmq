package com.frank.gramturmq.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Copyright(C) ShanDongYinFang 2019.
 * <p>
 * 日期时间格式化类.
 *
 * @author 张孝党 2019/06/03.
 * @version V0.0.2.
 * <p>
 * 更新履历： V0.0.1 2019/06/03 张孝党 创建.
 */
public class DateTimeUtil {

    /**
     * 日期格式化.
     */
    public final static String FORMAT_YYYYMMDD = "yyyyMMdd";

    /**
     * 时间格式化.
     */
    public final static String FORMAT_HHMMSS = "HHmmss";

    /**
     *  保存时间格式化.
     */
    public final static String FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String getCurrentDate() {

        Date currentDate = new Date();
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_YYYYMMDD);
        return df.format(currentDate);
    }

    public static String getCurrentTime() {

        Date currentDate = new Date();
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_HHMMSS);
        return df.format(currentDate);
    }

    public static String getTimeformat() {

        Date currentDate = new Date();
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_YYYYMMDDHHMMSS);
        return df.format(currentDate);
    }

}
