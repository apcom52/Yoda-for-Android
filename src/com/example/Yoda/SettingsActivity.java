package com.example.Yoda;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by apcom on 27.03.2016.
 */
public class SettingsActivity extends Activity {

    RadioGroup groupNumber;
    RadioButton firstGroup;
    RadioButton secondGroup;

    CheckBox hideCanceled;
    CheckBox markEnded;

    private static String SETTINGS_FILENAME = "settings";
    private static String SETTINGS_GROUP_NUMBER = "GROUP_NUMBER";
    private static String SETTINGS_HIDE_CANCELED_LESSONS = "HIDE_CANCELED_LESSONS";
    private static String SETTINGS_MARK_ENDED_LESSONS = "MARK_ENDED_LESSONS";

    private SharedPreferences settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        settings = this.getSharedPreferences(SETTINGS_FILENAME, MODE_PRIVATE);

        //SettingsSingleton settings = SettingsSingleton.getInstance();

        groupNumber = (RadioGroup) findViewById(R.id.groupNumber);
        firstGroup = (RadioButton) findViewById(R.id.firstGroup);
        secondGroup = (RadioButton) findViewById(R.id.secondGroup);

        hideCanceled = (CheckBox) findViewById(R.id.hideCanceled);
        markEnded = (CheckBox) findViewById(R.id.markEnded);

        if (!settings.contains(SETTINGS_GROUP_NUMBER))
            saveSettings(SETTINGS_GROUP_NUMBER, 1);
        if (!settings.contains(SETTINGS_HIDE_CANCELED_LESSONS))
            saveSettings(SETTINGS_HIDE_CANCELED_LESSONS, false);
        if (!settings.contains(SETTINGS_MARK_ENDED_LESSONS))
            saveSettings(SETTINGS_MARK_ENDED_LESSONS, false);

        int groupNum = settings.getInt(SETTINGS_GROUP_NUMBER, 1);

        if (groupNum == 1) {
            firstGroup.setChecked(true);
            secondGroup.setChecked(false);
        } else if (groupNum == 2) {
            firstGroup.setChecked(false);
            secondGroup.setChecked(true);
        }

        if (settings.getBoolean(SETTINGS_HIDE_CANCELED_LESSONS, false))
            hideCanceled.setChecked(true);

        if (settings.getBoolean(SETTINGS_MARK_ENDED_LESSONS, false))
            markEnded.setChecked(true);

        groupNumber.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.firstGroup) {
                    saveSettings(SETTINGS_GROUP_NUMBER, 1);
                } else if (checkedId == R.id.secondGroup) {
                    saveSettings(SETTINGS_GROUP_NUMBER, 2);
                }
            }
        });

        hideCanceled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveSettings(SETTINGS_HIDE_CANCELED_LESSONS, isChecked);
            }
        });

        markEnded.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveSettings(SETTINGS_MARK_ENDED_LESSONS, isChecked);
            }
        });
    }

    public void saveSettings(String key, int value) {
        Editor ed = settings.edit();
        ed.putInt(key, value);
        ed.commit();
    }

    public void saveSettings(String key, boolean value) {
        Editor ed = settings.edit();
        ed.putBoolean(key, value);
        ed.commit();
    }

    public void showSource(View view) {
        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/apcom52/Yoda-for-Android"));
        startActivity(browser);
    }
}