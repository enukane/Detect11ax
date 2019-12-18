package org.glenda9.detect11ax;

import android.net.wifi.ScanResult;
import android.util.Log;

import java.lang.reflect.Field;

public class Dot11axInfo {
    private ScanResult scanResult;
    private Boolean has_he_cap = false;
    private Boolean has_he_op = false;

    public String bssid = "00:00:00:00:00:00";
    public String ssid = "SSID";

    public static final String LOGNAME="dot11axinfo";
    public static final String SR_MEMBER_IES="informationElements";
    public static final String SR_IE_MEMBER_ID="id";
    public static final String SR_IE_MEMBER_BYTES="bytes";
    public static final int IE_ID_EXTENSION = 255;
    public static final int EID_EXT_HE_CAPABILITY=35;
    public static final int EID_EXT_HE_OPERATION=36;

    public Dot11axInfo(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    public Boolean is11ax() throws NoSuchFieldException, IllegalAccessException {
        Field field;
        Object[] ieArray;

        this.ssid = scanResult.SSID;
        this.bssid = scanResult.BSSID;

        field = scanResult.getClass().getDeclaredField(SR_MEMBER_IES);
        field.setAccessible(true);
        ieArray = (Object[])field.get(this.scanResult);

        for (int i = 0; i < ieArray.length; i++) {
            Object obj = ieArray[i];
            int id;
            byte[] bytes;

            /* acquire IE id */
            field = obj.getClass().getDeclaredField(SR_IE_MEMBER_ID);
            field.setAccessible(true);
            id = (int)field.get(obj);

            /* acquire IE bytes */
            field = obj.getClass().getDeclaredField(SR_IE_MEMBER_BYTES);
            bytes = (byte[])field.get(obj);

            switch (id) {
                case IE_ID_EXTENSION:
                    int eid_ext_type = (int)bytes[0];
                    Log.i("Dot11axInfo", "Found Extenstion IE : type=" + String.valueOf(eid_ext_type));
                    switch (eid_ext_type) {
                        case EID_EXT_HE_CAPABILITY:
                            this.has_he_cap = true;
                            break;
                        case EID_EXT_HE_OPERATION:
                            this.has_he_op = true;
                            break;
                    }
            }
        }

        return this.has_he_cap || this.has_he_op;
    }
}
