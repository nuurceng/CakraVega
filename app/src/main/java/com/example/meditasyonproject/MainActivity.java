package com.example.meditasyonproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Downloader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Meditasyon> liste = new ArrayList<>();
    private RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    DrawerLayout drawer;
    Button menuButton, yenileButton;
    ListViewAdapter adapter;
    TextView description;


    String url = "http://mistikyol.com/mistikmobil/mobiljson.php";
    RequestQueue queue;
    SQLiteHandler veritabani;
    public static String chislo ;
    ListView listView;
    String[] adlar = new String[]{
            "Son Eklenenler",
            "Favorilerim",
            "İyi Bir Uyku",
            "Kişisel Gelişim",
            "Çakra Vega",
            "Olumlamalar",
            "Çekim Yasası",
            "Motivasyon",
            "Ruhsal Farkındalık"
    };

    String[] linkler = new String[]{
            "0",
            "00",
            "1",
            "2",
            "3",
            "4",
            "7",
            "10",
            "11"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Koyduğumuz buton'u gerçekliyoruz.
        Button dugme = (Button) findViewById(R.id.iletisim);

        //Buton'a basılma interrupt'ını oluşturuyoruz.
        dugme.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Bir email intent'i oluşturuyoruz.
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"nurkaraca0638@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "Hakkımızda Hata Raporu Gönder");
                email.putExtra(Intent.EXTRA_TEXT, "şikayetlerinizi bildirebilirsiniz");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Gönderme yolunuzu seçiniz :"));
            }
        });

        queue = NetworkController.getInstance(this).getRequestQueue();
        queue.add(new JsonObjectRequest(0, url, null, new listener(), new error()));

        veritabani = new SQLiteHandler(getApplicationContext());

        drawer = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menuBtn);
        yenileButton = findViewById(R.id.yenilebtn);
        listView = findViewById(R.id.left_drawer);//
        description=findViewById(R.id.description);

        yenileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // finish();
                if(chislo!=null)
                {verileriGoster(chislo);}
                else{
                    verileriGoster("0");
                }
                //startActivity(getIntent());
                //veritabani.verileriSil();
                //queue.add(new JsonObjectRequest(0, url, null, new listener(), new error()));
                Toast.makeText(MainActivity.this, "sayfa yenileniyor", Toast.LENGTH_SHORT).show();
            }
        });

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                menuButton.setBackgroundResource(R.drawable.menus);
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                menuButton.setBackgroundResource(R.drawable.menu);
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });

        adapter = new ListViewAdapter(this, adlar, linkler);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                drawer.closeDrawer(GravityCompat.START);

                verileriGoster(linkler[position]);
                chislo=linkler[position];
            }
        });


        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        recyclerView = findViewById(R.id.recyler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);

        verileriGoster("0");

    }

    private class listener implements Response.Listener<JSONObject> {
        public void onResponse(JSONObject response) {

            try {

                JSONArray meditasyonlar = response.getJSONArray("meditasyonlar");
                int length = meditasyonlar.length();

                for (int i = 0; i < length; i++) {
                    try {

                        JSONObject meditasyon = meditasyonlar.getJSONObject(i);

                        Cursor kayitlar = veritabani.getWritableDatabase().rawQuery("SELECT count(*) FROM veriler WHERE anahtar = '"
                                + meditasyon.getString("id") + "'", null);
                        kayitlar.moveToFirst();
                        int sayi = kayitlar.getInt(0);

                        if (sayi == 0) {
                            veritabani.veriEkle(meditasyon.getString("baslik"), meditasyon.getString("aciklama"),
                                    meditasyon.getString("thumbnail"), meditasyon.getString("sesdosyasi"),
                                    meditasyon.getString("tarih"), meditasyon.getString("kategori"), meditasyon.getString("id"));
                        }

                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                verileriGoster("0");

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            Log.i("Gelen Cevap", response.toString());
        }
    }

    public void verileriGoster(String kate) {

        liste.clear();

        Cursor kayitlar = null;

        switch (kate) {
            case "0":
                kayitlar = veritabani.getWritableDatabase().rawQuery("SELECT * FROM veriler ORDER BY anahtar DESC", null);
                description.setText("---> Hoşgeldiniz!" +
                        " Meditasyon, bilinci ikna etmek ve zihni eğitmek veya iç gücü arttırmak için bir uygulamadır. Stresi azaltmak ve her durumda bile aklı kontrol altında tutmak çok yararlıdır.İyi dinlemeler... \n");
                break;
            case "00":
                kayitlar = veritabani.getWritableDatabase().rawQuery("SELECT * FROM veriler WHERE favori = 1 ORDER BY anahtar DESC", null);
                description.setText("---> Başkalarına yardımcı olmak için elinize her zaman büyük fırsatlar geçmez, ama küçük fırsatlar her gün çıkar.Bir değişim, bize gelişme fırsatını sağlayacak olan bir sonraki değişime yol açar.");
                break;
            case "1":
                kayitlar = veritabani.getWritableDatabase().rawQuery("SELECT * FROM veriler WHERE kategori = " + kate + " ORDER BY anahtar DESC", null);
                description.setText("---> Güne sağlıklı ve keyifli bir başlangıç yapabilmek için en önemli koşuldur. Sosyal yaşamı olumsuz yönde etkileyen uyku sorunları için ilaçlar yerine pratik önlemler almak çok daha olumlu sonuçlar verebiliyor.");
                break;
            case "2":
                kayitlar = veritabani.getWritableDatabase().rawQuery("SELECT * FROM veriler WHERE kategori = " + kate + " ORDER BY anahtar DESC", null);
                description.setText("---> İnsanın şuanki hal ve durumunu yeterli bulmaması ya da daha iyisini olabileceğini düşünmesi, hissetmesi ve istemesiyle başlar. Kişi, kendini biraz daha öteye taşımak ister.İşte bu tetiklenme anıdır!");
                break;
            case "3":
                kayitlar = veritabani.getWritableDatabase().rawQuery("SELECT * FROM veriler WHERE kategori = " + kate + " ORDER BY anahtar DESC", null);
                description.setText("---> İnsan vücudunda fiziksel veya ruhsal bir enerji noktası olan yedi ana çakra vardır. Her çakranın kendi titreşim frekansı, rengi vardır ve özel fonksiyonları yönetir.Bu şekilde fiziksel bedenin uyumlu çalışması desteklenir.");
                break;
            case "4":
                kayitlar = veritabani.getWritableDatabase().rawQuery("SELECT * FROM veriler WHERE kategori = " + kate + " ORDER BY anahtar DESC", null);
                description.setText("---> Çoğu uzman olumlamaların meditasyonla daha çok işe yaradığını ve sonuç verdiğini söylüyorlar. Sizinle paylaşacağımız bu tekniği günde sadece 10-15 dakikanızı ayırarak yapabilirsiniz.");
                break;
            case "7":
                kayitlar = veritabani.getWritableDatabase().rawQuery("SELECT * FROM veriler WHERE kategori = " + kate + " ORDER BY anahtar DESC", null);
                description.setText("---> Düşünceleriniz ve sözleriniz çekim yasası üzerinde etkili. Düşünceleriniz ya da sözleriniz aracılığıyla evrene gönderdiğiniz enerji, evrende aynı frekanstaki enerjilerle bütünleşip yeniden size gelir. ");
                break;
            case "10":
                kayitlar = veritabani.getWritableDatabase().rawQuery("SELECT * FROM veriler WHERE kategori = " + kate + " ORDER BY anahtar DESC", null);
                description.setText("---> Bizi bir gerçekten diğerine, bulunduğumuz yerden istediğimiz yere götürür. Belli bir amaç, katkı ve kimlik anlayışı sağlanarak gerçekleştirildiğinde, motivasyonumuz desteklenmiş olur.");
                break;
            case "11":
                kayitlar = veritabani.getWritableDatabase().rawQuery("SELECT * FROM veriler WHERE kategori = " + kate + " ORDER BY anahtar DESC", null);
                description.setText("---> Uyanık bir zihin ve dik bir omurga ile farkındalıkla oturmaktır. Otururken, sadece doğal nefesi takip etmek, duyguları, düşünceleri farkında olmaktır.Bu meditasyonda, ne düşünceleri durdurmak, ne de bastırmaktır.");
                break;
            default:
                kayitlar = veritabani.getWritableDatabase().rawQuery("SELECT * FROM veriler WHERE kategori = " + kate + " ORDER BY anahtar DESC", null);
                break;
        }


        while (kayitlar.moveToNext()) {
            String id = kayitlar.getString(kayitlar.getColumnIndex("id"));
            String baslik = kayitlar.getString(kayitlar.getColumnIndex("baslik"));
            String aciklama = kayitlar.getString(kayitlar.getColumnIndex("aciklama"));
            //String aciklamalar = kayitlar.getString(kayitlar.getColumnIndex("aciklama"));
            String resim = "http://mistikyol.com/mistikmobil/thumbnails/" + kayitlar.getString(kayitlar.getColumnIndex("resim"));
            String ses = "http://mistikyol.com/mistikmobil/audios/" + kayitlar.getString(kayitlar.getColumnIndex("ses"));
            String favori = kayitlar.getString(kayitlar.getColumnIndex("favori"));
            String tarih = kayitlar.getString(kayitlar.getColumnIndex("tarih"));
            String kategori = kayitlar.getString(kayitlar.getColumnIndex("kategori"));

            liste.add(new Meditasyon(id,baslik,aciklama,resim,ses,favori,tarih,kategori));
        }

        recyclerAdapter = new RecyclerAdapter(liste);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(MainActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view1, int position) {

                Intent gecis = new Intent(MainActivity.this, DetayActivity.class);
                gecis.putExtra("anahtarDegeri", liste.get(position).getId());
                startActivity(gecis);

            }
        }));

        runOnUiThread(new Runnable() { //gecikmesini engellemek veya yeni eklendiği zaman karışmasını engeller-veriler değişti..
            @Override
            public void run() {
                recyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    private class error implements Response.ErrorListener {
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(MainActivity.this, "Hata Oluştu: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Uygulamadan Çıkmak İstediğinize Emin Misiniz ?");
        builder.setCancelable(true);
        builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton("Kapat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


       /* if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
    }
}
