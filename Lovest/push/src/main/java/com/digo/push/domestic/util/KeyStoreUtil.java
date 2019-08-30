package com.digo.push.domestic.util;

import android.content.Context;

import com.digo.platform.logan.mine.Logz;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KeyStoreUtil {
    public static String getMetaValue(Context context, String metaKey) {
        Properties properties = new Properties();
        try {
            InputStream in = context.getAssets().open("quick_in_config.properties");
            properties.load(in);
            in.close();
        } catch (IOException e) {
            Logz.e(e.toString());
        }

        return properties.getProperty(metaKey);
    }
}
