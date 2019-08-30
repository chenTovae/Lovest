package com.digo.push.domestic.util;

/**
 * author: JackSong .
 * describe:系统工具类
 * on 2017/12/13.
 */

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.digo.platform.logan.mine.Logz;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * android.os.Build.BOARD：获取设备基板名称
 * android.os.Build.BOOTLOADER:获取设备引导程序版本号
 * android.os.Build.BRAND：获取设备品牌
 * android.os.Build.DEVICE：获取设备驱动名称
 * android.os.Build.HARDWARE：设备硬件名称,一般和基板名称一样（BOARD）
 * android.os.Build.ID:设备版本号。
 * android.os.Build.MODEL ：获取手机的型号 设备名称。
 * android.os.Build.MANUFACTURER:获取设备制造商
 * android:os.Build.PRODUCT：整个产品的名称
 * android.os.Build.TAGS：设备标签。如release-keys 或测试的 test-keys
 * android.os.Build.VERSION.RELEASE：获取系统版本字符串。如4.1.2 或2.2 或2.3等
 * android.os.Build.VERSION.CODENAME：设备当前的系统开发代号，一般使用REL代替
 */
public class SystemUtil {
    public static final String TAG = "SystemUtil";

    private static String getSimpleDate() {
        return new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date());
    }

    /**
     * @return :获取设备制造商
     */
    public static String getSystemManufacturer() {
        return android.os.Build.MANUFACTURER;
    }

    /**
     * @return :整个产品的名称
     */
    public static String getSystemProduct() {
        return android.os.Build.PRODUCT;
    }

    /**
     * @return :获取设备基板名称
     */
    public static String getSystemBoard() {
        return android.os.Build.BOARD;
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return 手机IMEI
     */
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getDeviceId();
        }
        return null;
    }

    public static void toLogString() {
        Logz.d(TAG + "\n" + getSystemManufacturer() + "\n" + getDeviceBrand() + "\n" + getSystemModel() + "\n" + getSystemVersion() + "\n" + getSystemBoard() + "\n"
                + getSystemProduct());
    }
}