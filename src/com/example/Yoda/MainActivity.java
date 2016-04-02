package com.example.Yoda;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
    Calendar targetDay = new GregorianCalendar();
    TextView dayLabel;
    RequestQueue queue;
    ListView timetableList;
    LinearLayout dayOff;

    private static String SETTINGS_FILENAME = "settings";
    private static String SETTINGS_HIDE_CANCELED_LESSONS = "HIDE_CANCELED_LESSONS";
    private static String SETTINGS_MARK_ENDED_LESSONS = "MARK_ENDED_LESSONS";
    private SharedPreferences settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dayLabel = (TextView)findViewById(R.id.dayLabel);
        queue = Volley.newRequestQueue(this);

        timetableList = (ListView)findViewById(R.id.timetableView);
        dayOff = (LinearLayout) findViewById(R.id.dayOff);

        settings = this.getSharedPreferences(SETTINGS_FILENAME, Context.MODE_PRIVATE);

        registerForContextMenu(timetableList);

        loadTimetable();
    }

    public void loadTimetable() {
        SimpleDateFormat dateformat = new SimpleDateFormat("dd MMMM yyyy");
        dayLabel.setText(dateformat.format(targetDay.getTime()));
        Log.w("YodaAJAX", "start loading");

        String url = "http://www.vsu-it.ru/api/timetable?day=" + parseDate(targetDay) + "&group=2";

        ArrayList<String> lessons = new ArrayList<String>();
        Context context = this;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(URLDecoder.decode(URLEncoder.encode(response, "iso8859-1"),"UTF-8"));
                    ArrayList<Lesson> lessons = new ArrayList<Lesson>();

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject obj = array.getJSONObject(i);
                        Lesson current = new Lesson(obj.getString("title"),
                                obj.getString("teacher"),
                                obj.getString("place"),
                                obj.getString("time"),
                                obj.getString("type"),
                                obj.getString("type_css")
                                );

                        if (!obj.getString("homework").equals("null"))
                            current.setHomework(obj.getString("homework"));

                        if (!obj.getString("control").equals("null"))
                            current.setControl(obj.getString("control"));

                        if (obj.getBoolean("is_canceled"))
                            current.setCanceled();

                        if (obj.getBoolean("is_ended"))
                            current.setEnded();

                        if (!settings.contains(SETTINGS_HIDE_CANCELED_LESSONS))
                            saveSettings(SETTINGS_HIDE_CANCELED_LESSONS, false);

                        if (!(settings.getBoolean(SETTINGS_HIDE_CANCELED_LESSONS, false) && current.is_canceled))
                            lessons.add(current);
                    }

                    if (lessons.size() > 0) {
                        dayOff.setVisibility(View.GONE);
                        timetableList.setVisibility(View.VISIBLE);
                        LessonAdapter adapter = new LessonAdapter(context, lessons);
                        timetableList.setAdapter(adapter);
                    } else {
                        dayOff.setVisibility(View.VISIBLE);
                        timetableList.setVisibility(View.GONE);
                    }


                } catch (UnsupportedEncodingException e) {
                    Toast.makeText(MainActivity.this, "Возникла ошибка при обработке данных", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Возникла ошибка при обработке данных", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //WebView web = (WebView) findViewById(R.id.webView);
                //web.loadDataWithBaseURL(null, new String(error.networkResponse.data,"UTF-8"), "text/html", "utf-8", null);
                Toast.makeText(MainActivity.this, "Не удалость загрузить данные", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

    public String parseDate(Calendar date) {
        String result = "";
        int day = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH) + 1;

        if (day < 10) result += "0";
        result += Integer.toString(day);

        if (month < 10) result += "0";
        result += Integer.toString(month);

        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    public void openSettings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void showPrevDay(MenuItem item) {
        targetDay.add(Calendar.DAY_OF_YEAR, -1);
        loadTimetable();
    }

    public void openCalendar(MenuItem item) {
        DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                targetDay.set(Calendar.YEAR, year);
                targetDay.set(Calendar.MONTH, monthOfYear);
                targetDay.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                loadTimetable();
            }
        };

        new DatePickerDialog(MainActivity.this, datePicker, targetDay.get(Calendar.YEAR), targetDay.get(Calendar.MONTH), targetDay.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void showNextDay(MenuItem item) {
        targetDay.add(Calendar.DAY_OF_YEAR, 1);
        loadTimetable();
    }

    public void showNextDay(View view) {
        targetDay.add(Calendar.DAY_OF_YEAR, 1);
        loadTimetable();
    }

    public void saveSettings(String key, boolean value) {
        SharedPreferences.Editor ed = settings.edit();
        ed.putBoolean(key, value);
        ed.commit();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == timetableList.getId()) {
            menu.add("Добавить домашнее задание");
            menu.add("Добавить контрольную работу");
            menu.add("Изменить аудиторию");
            menu.add("Перенести пару");
            menu.add("Отменить пару");
        }
    }
}
