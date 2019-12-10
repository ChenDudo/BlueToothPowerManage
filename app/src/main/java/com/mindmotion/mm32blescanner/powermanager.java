package com.mindmotion.mm32blescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mindmotion.blelib.BleManager;
import com.mindmotion.blelib.callback.BleWriteCallback;
import com.mindmotion.blelib.data.BleDevice;
import com.mindmotion.blelib.exception.BleException;

public class powermanager extends AppCompatActivity {

    private static final String TAG = powermanager.class.getSimpleName();
    public static final String KEY_DATA = "key_data";
    private BleDevice bleDevice;

    final String WRITE_UUID = "00006a00-0000-1000-8000-00805f9b34fb";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_powermanager);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initData();
        initView();
    }

    private void initView(){
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        Button ledon = findViewById(R.id.button_confirm);
        ledon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = BleManager.getInstance().isConnected(bleDevice);
                if(isConnected){
                    Log.i(powermanager.TAG, "connect ok");
                    sendopencmd();
                }
                else {
                    Log.i(powermanager.TAG, "connect error");
//                    Intent intent = new Intent(powermanager.this, MainActivity.class);
//                    startActivity(intent);
                }
            }
        });
    }


    public static byte hexToByte(String inHex){
        return (byte)Integer.parseInt(inHex,16);
    }

    public static byte[] hexToByteArray(String inHex){
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1){
            hexlen ++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        }else {
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2){
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j ++;
        }
        return result;
    }

    public byte[] hexTobytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i = i + 2) {
            String subStr = hex.substring(i, i + 2);
            boolean flag = false;
            int intH = Integer.parseInt(subStr, 16);
            if (intH > 127) flag = true;
            if (intH == 128) {
                intH = -128;
            } else if (flag) {
                intH = 0 - (intH & 0x7F);
            }
            byte b = (byte) intH;
            bytes[i / 2] = b;
        }
        return bytes;
    }

    private void sendopencmd() {
        BleManager.getInstance().write(bleDevice, "6E400001-B5A3-F393-E0A9-E50E24DCCA9E",
                "6E400002-B5A3-F393-E0A9-E50E24DCCA9E",
                //hexToByteArray("61"),
                hexTobytes("61"),
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Toast.makeText(powermanager.this, "打开 已发送！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Toast.makeText(powermanager.this, "打开 发送失败！", Toast.LENGTH_SHORT).show();
                    }
                }
        );



    }

    private void initData() {
        bleDevice = getIntent().getParcelableExtra(KEY_DATA);
        if (bleDevice == null){
            finish();
            Intent intent = new Intent(powermanager.this, MainActivity.class);
            startActivity(intent);
        }

    }

}
