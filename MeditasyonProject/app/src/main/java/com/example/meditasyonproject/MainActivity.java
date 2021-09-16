package com.example.meditasyonproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DownloadManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawer;
    Button menuButton;
    ListViewAdapter adapter;

    String url ="http://www.json-generator.com/api/json/get/celpmqrSDC?indent=2";
    RequestQueue queue;

    ListView listView;
    String[] adlar = new String[]{
            "Son Eklenenler",
            "Favorilerim",
            "İyi Bir Uyku",
            "Kişisel Gelişim",
            "Çakra Vega",
            "Olumlamalar",
            "Motivasyon",
            "Çakra Bilgileri",
            "Çekim Yasası",
            "Astroloji"
    };

    String[] linkler = new String[]{
            "0",
            "00",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = NetworkController.getInstance(this).getRequestQueue();
        queue.add(new JsonObjectRequest(0, url, null, new listener(), new error()));

        drawer = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menuBtn);
        listView = findViewById(R.id.left_drawer);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                menuButton.setBackgroundResource(R.drawable.menus);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                menuButton.setBackgroundResource(R.drawable.menu);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        adapter = new ListViewAdapter(this, adlar, linkler);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                drawer.closeDrawer(GravityCompat.START);

                Toast.makeText(MainActivity.this, linkler[position] + "Numaralı menü öğesine tıkladınız", Toast.LENGTH_SHORT).show();

            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

    }
        private class listener implements Response.Listener<JSONObject> {
            public void onResponse(JSONObject response) {
                Log.i("Gelen Cevap", response.toString());
            }
        }

        private class error implements Response.ErrorListener {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Hata Oluştu:" +error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

























