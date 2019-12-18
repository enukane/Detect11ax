package org.glenda9.detect11ax;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOGNAME = "detect11ax";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void doScan(View view) {
        String ssid_filter;
        List<ScanResult> apList = scanAP();
        List<Dot11axInfo> axInfos;

        if (apList == null) {
            clearCountAndList();
            return;
        }

        /* get text input: to filter SSID */
        EditText et = findViewById(R.id.ssid_filter);
        ssid_filter = et.getText().toString();
        Log.i(LOGNAME, "ssidfilter => " + ssid_filter);

        if (apList.isEmpty()) {
            showToast("No AP found");
            clearCountAndList();
            return;
        }

        axInfos = parseScanResultsTo11axInfos(apList, ssid_filter);
        Log.i(LOGNAME, "filtered 11ax AP Count => " + String.valueOf(axInfos.size()));

        TextView tv = findViewById(R.id.ap_count);
        tv.setText(String.valueOf(axInfos.size()) + " ");

        ListView lv = findViewById(R.id.scan_listview);
        Dot11axInfoAdapter adapter = new Dot11axInfoAdapter(this, R.layout.scanlist_item, axInfos);
        lv.setAdapter(adapter);
    }

    private List<ScanResult> scanAP() {
        Log.i(LOGNAME, "start scanning");
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1001);
        }

        WifiManager manager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (manager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            showToast("Wi-Fi is not enabled");
            return null;
        }

        manager.startScan();
        return manager.getScanResults();
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    private void clearCountAndList() {
        List<Dot11axInfo> emptyDisplayList = new ArrayList<>();

        TextView tv = findViewById(R.id.ap_count);
        tv.setText(String.valueOf(0) + " ");

        /* update list */
        ListView lv = findViewById(R.id.scan_listview);
        Dot11axInfoAdapter adapter = new Dot11axInfoAdapter(this, R.layout.scanlist_item, emptyDisplayList);
        lv.setAdapter(adapter);
    }

    private List<Dot11axInfo> parseScanResultsTo11axInfos(List<ScanResult> scanResults, String ssid_filter) {
        List<Dot11axInfo> axInfos = new ArrayList<>();

        Log.i(LOGNAME, "Total AP count is " + String.valueOf(scanResults.size()));

        for (int i = 0; i < scanResults.size(); i++) {
            ScanResult scanResult = scanResults.get(i);
            Dot11axInfo axInfo = new Dot11axInfo(scanResult);

            if (ssid_filter != null && !ssid_filter.equals("") && !scanResult.SSID.contains(ssid_filter)) {
                continue;
            }

            try {
                if (axInfo.is11ax()) {
                    axInfos.add(axInfo);
                }
            } catch (NoSuchFieldException e) {
                Log.i(LOGNAME, "NoSuchFieldException, failed to parse IE");

            } catch (IllegalAccessException e) {
                Log.i(LOGNAME, "IllegalAccessException, failed to parse IE");
            }
        }

        Log.i(LOGNAME, "Found 802.11ax AP count is " + String.valueOf(axInfos.size()));
        showToast("Total AP count is " + String.valueOf(scanResults.size()) + "\n802.11ax AP count is " + String.valueOf(axInfos.size()));
        return axInfos;
    }

}
