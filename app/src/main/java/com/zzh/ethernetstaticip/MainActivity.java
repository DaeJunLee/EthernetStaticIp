package com.zzh.ethernetstaticip;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.input.InputManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.zzh.ethernetstaticip.databinding.ActivityMainBinding;
import com.zzh.ethernetstaticip.receiver.MainFragmentReceiver;
import com.zzh.ethernetstaticip.utils.IpGetUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private MainFragmentReceiver fragReceiver;

    ActivityMainBinding binding;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        fragReceiver = new MainFragmentReceiver();
        registerBroadcastReceiver();
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        binding.btnSetDhcpIp.setOnClickListener(this);
        binding.btnSetStaticIp.setOnClickListener(this);
        String ip = IpGetUtil.getIpAddress(MainActivity.this);

        binding.tvIp.setText(getString(R.string.ip_address, ip));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();

        binding.tvIp.setText(getString(R.string.ip_address, IpGetUtil.getIpAddress(this)));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_set_dhcp_ip:
                boolean success = IpGetUtil.setEthernetIP(MainActivity.this, "DHCP",
                        "", "", "", "", "");
                if (success)
                    rebootSystem();
                break;
            case R.id.btn_set_static_ip:
                String ip_address_1 = binding.ipAddress1.getText().toString();
                String ip_address_2 = binding.ipAddress2.getText().toString();
                String ip_address_3 = binding.ipAddress3.getText().toString();
                String ip_address_4 = binding.ipAddress4.getText().toString();

                if (TextUtils.isEmpty(ip_address_1) || TextUtils.isEmpty(ip_address_2) || TextUtils.isEmpty(ip_address_3) || TextUtils.isEmpty(ip_address_4)) {
                    Toast.makeText(this, "Please check IpAddress", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String ipAddress = String.format(String.valueOf(R.string.address_format), ip_address_1, ip_address_2, ip_address_3, ip_address_4);

                String net_mask_1 = binding.netMask1.getText().toString();
                String net_mask_2 = binding.netMask2.getText().toString();
                String net_mask_3 = binding.netMask3.getText().toString();
                String net_mask_4 = binding.netMask4.getText().toString();
                if (TextUtils.isEmpty(net_mask_1) || TextUtils.isEmpty(net_mask_2) || TextUtils.isEmpty(net_mask_3) || TextUtils.isEmpty(net_mask_4)) {
                    Toast.makeText(this, "Please check netmask", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String netMask = String.format(String.valueOf(R.string.address_format), net_mask_1, net_mask_2, net_mask_3, net_mask_4);

                String gateway_1 = binding.gateWay1.getText().toString();
                String gateway_2 = binding.gateWay2.getText().toString();
                String gateway_3 = binding.gateWay3.getText().toString();
                String gateway_4 = binding.gateWay4.getText().toString();
                if (TextUtils.isEmpty(gateway_1) || TextUtils.isEmpty(gateway_2) || TextUtils.isEmpty(gateway_3) || TextUtils.isEmpty(gateway_4)) {
                    Toast.makeText(this, "Please check gateway", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String gateway = String.format(String.valueOf(R.string.address_format), gateway_1, gateway_2, gateway_3, gateway_4);

                String dnsone_1 = binding.dnsOne1.getText().toString();
                String dnsone_2 = binding.dnsOne2.getText().toString();
                String dnsone_3 = binding.dnsOne3.getText().toString();
                String dnsone_4 = binding.dnsOne4.getText().toString();
                if (TextUtils.isEmpty(dnsone_1) || TextUtils.isEmpty(dnsone_2) || TextUtils.isEmpty(dnsone_3) || TextUtils.isEmpty(dnsone_4)) {
                    Toast.makeText(this, "Please check dns1", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String dns1 = String.format(String.valueOf(R.string.address_format), dnsone_1, dnsone_2, dnsone_3, dnsone_4);

                String dnstwo_1 = binding.dnsTwo1.getText().toString();
                String dnstwo_2 = binding.dnsTwo2.getText().toString();
                String dnstwo_3 = binding.dnsTwo3.getText().toString();
                String dnstwo_4 = binding.dnsTwo4.getText().toString();
                if (TextUtils.isEmpty(dnstwo_1) || TextUtils.isEmpty(dnstwo_2) || TextUtils.isEmpty(dnstwo_3) || TextUtils.isEmpty(dnstwo_4)) {
                    Toast.makeText(this, "Please check dns2", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String dns2 = String.format(String.valueOf(R.string.address_format), dnstwo_1, dnstwo_2, dnstwo_3, dnstwo_4);

                boolean success2 = IpGetUtil.setEthernetIP(MainActivity.this, "STATIC",
                        ipAddress, netMask,
                        gateway, dns1, dns2);

                InputMethodManager imForTalkCamera = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imForTalkCamera.isActive()) {
                    imForTalkCamera.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                if (success2) {
                    rebootSystem();
                }
                break;
        }
    }

    public void rebootSystem() {
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        pm.reboot(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getApplicationContext().unregisterReceiver(fragReceiver);
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        // for net state changed
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        if (fragReceiver != null) {
            getApplicationContext().registerReceiver(fragReceiver, intentFilter);
            setBroadcastReceiverListener();
            Log.i(TAG, "registerBroadcastReceiver finished");
        }
    }

    private void setBroadcastReceiverListener() {
        fragReceiver.setFragmentListener(new MainFragmentReceiver.FragmentListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void getNetState(int state) {
                Log.i(TAG, "getNetState state=" + state);
                if (state > MainFragmentReceiver.NETSTATUS_INAVAILABLE) {
                    String ip = IpGetUtil.getIpAddress(MainActivity.this);
                    binding.tvIp.setText(getString(R.string.ip_address, ip));
                } else {
                    binding.tvIp.setText(getString(R.string.ip_address, "没有网络"));
                }
            }
        });
    }
}
