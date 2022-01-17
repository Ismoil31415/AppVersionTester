package com.example.simcountry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simcountry.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String currentVersion;
    TextView textView1;
    TextView textView2;
    Button button;
    String versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 = findViewById(R.id.current_version);
        textView2 = findViewById(R.id.playmarket_version);
        button = findViewById(R.id.update);
        versionName = BuildConfig.VERSION_NAME;

//        try {
//          currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//          e.printStackTrace();
//        }

        new GetVersionCode().execute();

    }


    class GetVersionCode extends AsyncTask<Void, String, String> {

        @Override

        protected String doInBackground(Void... voids) {

            String newVersion = null;

            try {
                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=com.nestia.sg.transport&hl=en&gl=US")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();
                if (document != null) {
                    Elements element = document.getElementsContainingOwnText("Current Version"); //получить элементы, содержащие собственный текст
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (Element sibElemet : sibElemets) {
                                newVersion = sibElemet.text();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newVersion;

        }


        @Override

        protected void onPostExecute(String onlineVersion) {

            super.onPostExecute(onlineVersion);

            if (onlineVersion != null && !onlineVersion.isEmpty()) {

                Log.d("update", "Current version " + BuildConfig.VERSION_NAME + "playstore version " + onlineVersion);
                textView1.setText("Current version: " + BuildConfig.VERSION_NAME);
                textView2.setText("Latest version: " + onlineVersion);
                String[] currentVersion = BuildConfig.VERSION_NAME.split("\\.");
                String[] latestVersion = onlineVersion.split("\\.");

                // Aslida ">","<" belgisi teskari qo`yilishi kerak, chunki biz yozgan "current version",
                // internetga yuklaganimizdan mantiq jihatdan yangi hisoblanadi va
                // bizga aniqlab berilgan "online version"ni biz hozir yozgan "current version" ga "update" qilish kerak bo`ladi!!!
                Log.d("@@@",currentVersion + " " +latestVersion);
                if(Integer.valueOf(currentVersion[0]) > Integer.valueOf(latestVersion[0]) || Integer.valueOf(currentVersion[1]) > Integer.valueOf(latestVersion[1])) {
                    button.setText("UPDATE");
                    Toast.makeText(getBaseContext(), "Update", Toast.LENGTH_LONG).show();
                } else {
                    button.setVisibility(View.GONE);
                    Toast.makeText(getBaseContext(), "No Update", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
