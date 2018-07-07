package com.example.wahidari.dompetku;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wahidari.dompetku.database.DataHelper;
import com.example.wahidari.dompetku.report.ReportActivity;
import com.jaredrummler.materialspinner.MaterialSpinner;

/**
 * Created by @wahidari on 13/06/18.
 */

public class DompetActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Initialize Variable
     */
    MaterialSpinner spinCategory;
    String[] IdPengeluaran, IdKategoriPengeluaran, KategoriPengeluaran, DeskripsiPengeluaran, JumlahPengeluaran, TanggalPengeluaran;
    String[] IdPendapatan, IdKategoriPendapatan, KategoriPendapatan, DeskripsiPendapatan, JumlahPendapatan, TanggalPendapatan;
    ListView ListViewPengeluaran, ListViewPendapatan;
    protected Cursor cursor;
    DataHelper dataHelper;
    int TotalPengeluaran, TotalPendapatan = 0;
    int AdaDataPendapatan, AdaDataPengeluaran;
    TextView tvValuePengeluaran, tvValuePendapatan;
    LinearLayout linearLayout, footer , tvPendapatan , tvPengeluaran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dompet);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tvPendapatan = findViewById(R.id.tvPendapatan);
        tvPengeluaran = findViewById(R.id.tvPengeluaran);
        tvValuePendapatan = findViewById(R.id.tvValuePendapatan);
        tvValuePengeluaran = findViewById(R.id.tvValuePengeluaran);
        spinCategory = findViewById(R.id.kategori);
        linearLayout = findViewById(R.id.emptyview);
        footer = findViewById(R.id.footer);

        // Initial ListView Pengeluarann
        ListViewPengeluaran = findViewById(R.id.ListPengeluaran);
        // Initial ListView Pendapatan
        ListViewPendapatan = findViewById(R.id.ListPendapatan);

        loadKategori();
        dataHelper = new DataHelper(this);
        new MyAsynch().execute();
    }

    /**
     * start asyntask onCreate and Data Changed
     */
    private class MyAsynch extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... Strings) { // run time intensive task in separate thread
            GetDataExpense();
            GetDataRevenue();
            return null;
        }

        protected void onPostExecute(String result) {
            // Give the data to you adapter from here,instead of the place where you gave it earlier
            LoadListView();
        }
    }

    /**
     * Load Data From Database Category
     */
    public void GetDataRevenue (){
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        cursor = db.rawQuery("select * from pendapatan inner join kategori_pendapatan where pendapatan.idkategori = kategori_pendapatan.id", null);
        AdaDataPendapatan = cursor.getCount();

        // Initial Array Size
        int totalData = cursor.getCount();
        IdPendapatan         = new String[totalData];
        IdKategoriPendapatan = new String[totalData];
        KategoriPendapatan   = new String[totalData];
        DeskripsiPendapatan  = new String[totalData];
        JumlahPendapatan     = new String[totalData];
        TanggalPendapatan    = new String[totalData];

        cursor.moveToFirst();
        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            // Get Item Detail
            IdPendapatan[cc]         = cursor.getString(0).toString();
            IdPendapatan[cc] = cursor.getString(1).toString();
            DeskripsiPendapatan[cc]  = cursor.getString(2).toString();
            JumlahPendapatan[cc]     = cursor.getString(3).toString();
            TanggalPendapatan[cc]    = cursor.getString(4).toString();
            KategoriPendapatan[cc]   = cursor.getString(6).toString();
            TotalPendapatan += Integer.parseInt(cursor.getString(3).toString());
        }
    }

    /**
     * Load Data From Database Category
     */
    public void GetDataExpense (){
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        cursor = db.rawQuery("select * from pengeluaran inner join kategori_pengeluaran where pengeluaran.idkategori = kategori_pengeluaran.id", null);
        AdaDataPengeluaran = cursor.getCount();;
        // Initial Array Size
        int totalData = cursor.getCount();
        IdPengeluaran         = new String[totalData];
        IdKategoriPengeluaran = new String[totalData];
        KategoriPengeluaran   = new String[totalData];
        DeskripsiPengeluaran  = new String[totalData];
        JumlahPengeluaran     = new String[totalData];
        TanggalPengeluaran    = new String[totalData];

        cursor.moveToFirst();
        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            // Get Item Detail
            IdPengeluaran[cc]         = cursor.getString(0).toString();
            IdKategoriPengeluaran[cc] = cursor.getString(1).toString();
            DeskripsiPengeluaran[cc]  = cursor.getString(2).toString();
            JumlahPengeluaran[cc]     = cursor.getString(3).toString();
            TanggalPengeluaran[cc]    = cursor.getString(4).toString();
            KategoriPengeluaran[cc]   = cursor.getString(6).toString();
            TotalPengeluaran += Integer.parseInt(cursor.getString(3).toString());
        }
    }

    public void LoadListView() {
        // membuat sebuah adapter yang berfungsi untuk menampung data sementara sebelum di tampilkan ke dalam list view
        MyAdapterRevenue adapterPendapatan = new MyAdapterRevenue(this, DeskripsiPendapatan, KategoriPendapatan, JumlahPendapatan, TanggalPendapatan);
        //menampilkan / memasukan adapter ke dalam ListView
        ListViewPendapatan.setAdapter(adapterPendapatan);

        // membuat sebuah adapter yang berfungsi untuk menampung data sementara sebelum di tampilkan ke dalam list view
        MyAdapterExpense adapterPengeluaran = new MyAdapterExpense(this, DeskripsiPengeluaran, KategoriPengeluaran, JumlahPengeluaran, TanggalPengeluaran);
        //menampilkan / memasukan adapter ke dalam ListView
        ListViewPengeluaran.setAdapter(adapterPengeluaran);

        // Cek Ada Data Pengeluaran dan Pendapatan atau Tidak
        // Set Layout
        if (AdaDataPendapatan < 1 && AdaDataPengeluaran < 1){
            // Tampilkan Layout Tidak ada Data
            linearLayout.setVisibility(View.VISIBLE);
            ListViewPendapatan.setVisibility(View.GONE);
            ListViewPengeluaran.setVisibility(View.GONE);
            footer.setVisibility(View.GONE);
        } else if (AdaDataPendapatan > 0 && AdaDataPengeluaran < 1){
            // Tampilkan Ada Data Pendapatan
            ListViewPendapatan.setVisibility(View.VISIBLE);
            ListViewPengeluaran.setVisibility(View.GONE);
            footer.setVisibility(View.VISIBLE);
            tvPendapatan.setVisibility(View.VISIBLE);
            tvPengeluaran.setVisibility(View.GONE);
        } else if (AdaDataPengeluaran > 0 && AdaDataPendapatan < 1){
            // Tampilkan Ada Data Pengeluaran
            ListViewPengeluaran.setVisibility(View.VISIBLE);
            ListViewPendapatan.setVisibility(View.GONE);
            footer.setVisibility(View.VISIBLE);
            tvPendapatan.setVisibility(View.GONE);
            tvPengeluaran.setVisibility(View.VISIBLE);
        } else {
            // Hilangkan Layout Tidak ada Data
            // Tampilkan Ada Data Pengeluaran & Pendapatan
            tvPendapatan.setVisibility(View.VISIBLE);
            tvPengeluaran.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            ListViewPendapatan.setVisibility(View.VISIBLE);
            ListViewPengeluaran.setVisibility(View.VISIBLE);
        }

        // Set Pendapatan dan Pengeluaran Total
        tvValuePendapatan.setText("Rp. " + String.valueOf(TotalPendapatan));
        tvValuePengeluaran.setText("Rp. " + String.valueOf(TotalPengeluaran));

        // Set 2 ListView Agar bisa di Scroll
        ListUtils.setDynamicHeight(ListViewPendapatan);
        ListUtils.setDynamicHeight(ListViewPengeluaran);

        ((ArrayAdapter) ListViewPengeluaran.getAdapter()).notifyDataSetInvalidated();
        ((ArrayAdapter) ListViewPendapatan.getAdapter()).notifyDataSetInvalidated();
    }

    /**
     * Adapter for Set Data ListView as CardView Revenue
     */
    private class MyAdapterRevenue extends ArrayAdapter {
        String list_deskripsi[];
        String list_kategori[];
        String list_jumlah[];
        String list_tanggal[];
        Activity activity;

        //konstruktor
        public MyAdapterRevenue(DompetActivity dompetActivity, String[] list_deskripsi, String list_kategori[], String list_jumlah[], String list_tanggal[]) {
            super(dompetActivity, R.layout.item_pendapatan, list_deskripsi);
            activity = dompetActivity;
            this.list_deskripsi = list_deskripsi;
            this.list_kategori  = list_kategori;
            this.list_jumlah    = list_jumlah;
            this.list_tanggal    = list_tanggal;
        }

        //menthode yang digunakan untuk memanggil layout listview dan mengenalkan widgetnya
        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // panggil layout item_kategori_pengeluaran
            LayoutInflater inflater = (LayoutInflater) activity.getLayoutInflater();
            View v = inflater.inflate(R.layout.item_pendapatan, null);
            // kenalkan widget yang ada pada item_kategori_pengeluaran
            TextView deskripsi;
            TextView kategori;
            TextView jumlah;
            TextView tanggal;
            ImageView imageView;

            //casting widget id
            deskripsi = v.findViewById(R.id.deskrip);
            kategori = v.findViewById(R.id.namakategori);
            jumlah = v.findViewById(R.id.jumlah);
            tanggal = v.findViewById(R.id.tanggal);
            imageView = v.findViewById(R.id.list_avatar);

            // set data kedalam cardview
            deskripsi.setText(list_deskripsi[position]);
            kategori.setText(list_kategori[position]);
            jumlah.setText("Rp. "+list_jumlah[position]);
            tanggal.setText(list_tanggal[position]);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.add_circular_32));

            return v;
        }
    }

    /**
     * Adapter for Set Data ListView as CardView Revenue
     */
    private class MyAdapterExpense extends ArrayAdapter {
        String list_deskripsi[];
        String list_kategori[];
        String list_jumlah[];
        String list_tanggal[];
        Activity activity;

        //konstruktor
        public MyAdapterExpense(DompetActivity dompetActivity, String[] list_deskripsi, String list_kategori[], String list_jumlah[], String list_tanggal[]) {
            super(dompetActivity, R.layout.item_pengeluaran, list_deskripsi);
            activity = dompetActivity;
            this.list_deskripsi = list_deskripsi;
            this.list_kategori  = list_kategori;
            this.list_jumlah    = list_jumlah;
            this.list_tanggal   = list_tanggal;
        }

        //menthode yang digunakan untuk memanggil layout listview dan mengenalkan widgetnya
        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // panggil layout item_kategori_pengeluaran
            LayoutInflater inflater = (LayoutInflater) activity.getLayoutInflater();
            View v = inflater.inflate(R.layout.item_pengeluaran, null);
            // kenalkan widget yang ada pada item_kategori_pengeluaran
            TextView deskripsi;
            TextView kategori;
            TextView jumlah;
            TextView tanggal;
            ImageView imageView;

            //casting widget id
            deskripsi = v.findViewById(R.id.deskrip);
            kategori = v.findViewById(R.id.namakategori);
            jumlah = v.findViewById(R.id.jumlah);
            tanggal = v.findViewById(R.id.tanggal);
            imageView = v.findViewById(R.id.list_avatar);

            // set data kedalam cardview
            deskripsi.setText(list_deskripsi[position]);
            kategori.setText(list_kategori[position]);
            jumlah.setText("Rp. "+list_jumlah[position]);
            tanggal.setText(list_tanggal[position]);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.minus_circular_32));

            return v;
        }
    }

    /**
     * Fungsi Untuk membuat 2 listview dalam scrollview
     */
    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }

    /**
     * Load Kategori and set into Spinner
     */
    public void loadKategori() {
        String[] ITEMS = {"Semua Kategori", "Pendapatan", "Pengeluaran"};
        spinCategory.setItems(ITEMS);
        spinCategory.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                // Spinner Semua Kategoori
                if (position == 0) {
                    linearLayout.setVisibility(View.GONE);
                    // Tidak Ada Data Pendapatan Dan Pengeluaran
                    if (AdaDataPendapatan < 1 && AdaDataPengeluaran < 1){
                        linearLayout.setVisibility(View.VISIBLE);
                        ListViewPendapatan.setVisibility(View.GONE);
                        ListViewPengeluaran.setVisibility(View.GONE);
                        footer.setVisibility(View.GONE);
                    }
                    // Ada Data Pendapatan & Tidak ada Data Pengeluaran
                    else if (AdaDataPendapatan > 0 && AdaDataPengeluaran < 1){
                        ListViewPendapatan.setVisibility(View.VISIBLE);
                        ListViewPengeluaran.setVisibility(View.GONE);
                        footer.setVisibility(View.VISIBLE);
                        tvPendapatan.setVisibility(View.VISIBLE);
                        tvPengeluaran.setVisibility(View.GONE);
                    }
                    // Ada Data Pengeluaran & Tidak ada Data Pendapatan
                    else if (AdaDataPengeluaran > 0 && AdaDataPendapatan < 1){
                        ListViewPengeluaran.setVisibility(View.VISIBLE);
                        ListViewPendapatan.setVisibility(View.GONE);
                        footer.setVisibility(View.VISIBLE);
                        tvPendapatan.setVisibility(View.GONE);
                        tvPengeluaran.setVisibility(View.VISIBLE);
                    }
                    // Ada Data Pendapatan Dan Pengeluaran
                    else {
                        footer.setVisibility(View.VISIBLE);
                        tvPendapatan.setVisibility(View.VISIBLE);
                        tvPengeluaran.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                        ListViewPendapatan.setVisibility(View.VISIBLE);
                        ListViewPengeluaran.setVisibility(View.VISIBLE);
                    }
                }
                // Spinner Kategoori Pendapatan
                else if (position == 1) {
                    // Hilangkan ListView Pendapatan
                    ListViewPengeluaran.setVisibility(View.GONE);
                    tvPengeluaran.setVisibility(View.GONE);
                    // Ada Data Pendapatan
                    if (AdaDataPendapatan > 0){
                        ListViewPendapatan.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                        footer.setVisibility(View.VISIBLE);
                        tvPendapatan.setVisibility(View.VISIBLE);
                    }
                    // Tidak ada Data Pendapatan
                    else {
                        footer.setVisibility(View.GONE);
                        ListViewPendapatan.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                    }

                }
                // Spinner Kategoori Pengeluaran
                else {
                    // Hilangkan ListView Pengeluaran
                    ListViewPendapatan.setVisibility(View.GONE);
                    tvPendapatan.setVisibility(View.GONE);
                    // Ada Data Pengeluaran
                    if (AdaDataPengeluaran > 0){
                        ListViewPengeluaran.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                        footer.setVisibility(View.VISIBLE);
                        tvPengeluaran.setVisibility(View.VISIBLE);
                    }
                    // Tidak ada Data Pengeluaran
                    else {
                        footer.setVisibility(View.GONE);
                        ListViewPengeluaran.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            getApplicationContext().deleteDatabase("dompetku.db");
            finish();
            startActivity(getIntent());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(DompetActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_dompet) {
            Toast.makeText(this, "DompetKu", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_kategori_pendapatan) {
            Intent intent = new Intent(DompetActivity.this, KategoriPendapatan.class);
            startActivity(intent);
        } else if (id == R.id.nav_kategori_pengeluaran) {
            Intent intent = new Intent(DompetActivity.this, KategoriPengeluaran.class);
            startActivity(intent);
        } else if (id == R.id.nav_monthly) {
            Intent intent = new Intent(DompetActivity.this, ReportActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            showAboutDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Show About Dialog
     */
    private void showAboutDialog() {
        View mView = getLayoutInflater().inflate(R.layout.dialog_about, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(R.string.app_name);
        builder.setView(mView);
        builder.create();
        TextView textView = mView.findViewById(R.id.about_link_credits);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        TextView textView2 = mView.findViewById(R.id.about_link_credits2);
        textView2.setMovementMethod(LinkMovementMethod.getInstance());
        TextView textView3 = mView.findViewById(R.id.about_link_credits3);
        textView3.setMovementMethod(LinkMovementMethod.getInstance());
        builder.show();
    }
}
