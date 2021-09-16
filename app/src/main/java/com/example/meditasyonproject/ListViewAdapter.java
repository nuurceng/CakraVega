package com.example.meditasyonproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {//temel adapter mantığından beslencek

    private Context context;//activity temeli
    private String[] adi;
    private String[] link;
    private LayoutInflater inflater; //menusatir.xml den görselleri çekecek


    public ListViewAdapter(Context m_context, String[] m_adi, String[] m_link) {

        this.context = m_context;
        this.adi = m_adi;
        this.link = m_link;


    }

    @Override
    public int getCount() {
        return adi.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {//parent üst öğe
        TextView adi_textview;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.menusatir, parent, false);
        adi_textview = (TextView) itemView.findViewById(R.id.txtmenuadi);
        adi_textview.setText(adi[position]);//position her bir elemana uygun olan array içindeki elemanı getirip yazıya atama yapcak..

        return itemView;

    }
}