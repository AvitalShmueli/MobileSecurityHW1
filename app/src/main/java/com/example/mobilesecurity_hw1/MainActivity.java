package com.example.mobilesecurity_hw1;

import android.content.Context;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;


public class MainActivity extends AppCompatActivity {
    private TextInputEditText main_TXT_password;
    private MaterialButton main_BTN_enter;
    private MaterialTextView main_LBL_guess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        main_LBL_guess.setVisibility(View.INVISIBLE);

        main_TXT_password.setOnFocusChangeListener((v, hasFocus) -> {
            hideKeyboard(v);
            main_LBL_guess.setVisibility(View.INVISIBLE);
        });

        main_BTN_enter.setOnClickListener(v -> {
            // get battery level - and create a password
            main_TXT_password.clearFocus();
            BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
            int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            StringBuilder battery3times = new StringBuilder();
            battery3times.append(batLevel).append(batLevel).append(batLevel);

            // get screen brightness level
            int oldBrightness;
            try {
                oldBrightness = Settings.System.getInt(getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException e) {
                throw new RuntimeException(e);
            }
            int roundedBrightnessLvl = (int) ((double) oldBrightness / 255 * 100);

            // get device audio mode
            AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            if(am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)
                main_LBL_guess.setText(String.format("MODE : " + roundedBrightnessLvl));

            // check if device is connected to wifi
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

            Log.d("CHECK PASSWORD","ringtone mode "+(am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE));
            Log.d("CHECK PASSWORD","rounded BrightnessLvl "+(roundedBrightnessLvl == 100));
            Log.d("CHECK PASSWORD","text entered "+(main_TXT_password.getEditableText().length() > 0));
            Log.d("CHECK PASSWORD", "is wifi connected?" +(wifiManager.isWifiEnabled() ? "yes" : "no") );

            if(am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE &&
                    roundedBrightnessLvl == 100 &&
                    main_TXT_password.getEditableText().length() > 0 &&
                    wifiManager.isWifiEnabled()){
                String strPassword = main_TXT_password.getEditableText().toString();
                if (strPassword.equals(battery3times.toString())) {
                    main_LBL_guess.setText(R.string.success);
                    main_LBL_guess.setVisibility(View.VISIBLE);
                    Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
                }
                else {
                    main_LBL_guess.setText(R.string.try_again);
                    main_LBL_guess.setVisibility(View.VISIBLE);
                    Toast.makeText(this, R.string.try_again, Toast.LENGTH_SHORT).show();
                }
            } else {
                main_LBL_guess.setText(R.string.try_again);
                main_LBL_guess.setVisibility(View.VISIBLE);
                Toast.makeText(this, R.string.try_again, Toast.LENGTH_SHORT).show();
            }

            hideKeyboard(v);
        });
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void findViews() {
        main_TXT_password = findViewById(R.id.main_TXT_password);
        main_BTN_enter = findViewById(R.id.main_BTN_enter);
        main_LBL_guess = findViewById(R.id.main_LBL_guess);
    }

}