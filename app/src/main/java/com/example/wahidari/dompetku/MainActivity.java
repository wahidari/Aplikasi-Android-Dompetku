package com.example.wahidari.dompetku;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wahidari.dompetku.database.DataHelper;
import com.example.wahidari.dompetku.report.ReportActivity;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by @wahidari on 13/06/18.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // For ListView Data
    String[] IdPendapatan, IdKategoriPendapatan, KategoriPendapatan, DeskripsiPendapatan, JumlahPendapatan;
    String[] IdPengeluaran, IdKategoriPengeluaran, KategoriPengeluaran, DeskripsiPengeluaran, JumlahPengeluaran;
    ListView ListViewPendapatan, ListViewPengeluaran;

    // For Saldo Data
    String[] JumlahPendapatanSaldo, JumlahPengeluaranSaldo;

    // For Spinner Data
    Spinner spinKategoriPendapatan, spinKategoriPengeluaran;
    String[] SpinIdKategoriPendapatan, SpinNamaKategoriPendapatan, SpinIdKategoriPengeluaran, SpinNamaKategoriPengeluaran;
    int SELECTED_KATEGORI_ID_PENDAPATAN = 0;
    int SELECTED_KATEGORI_ID_PENGELUARAN = 0;
    
    // FAB
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2;

    // Database
    protected Cursor cursor, cursorExp, cursorRev;
    DataHelper dataHelper;
    
    // Get Today Date
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    String date = df.format(Calendar.getInstance().getTime());

    int AdaDataPendapatan, AdaDataPengeluaran;

    TextView tvValuePengeluaran, tvValuePendapatan, tvSaldo;
    LinearLayout linearLayout, footer , tvPendapatan , tvPengeluaran;

    public static MainActivity mainActivity;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        materialDesignFAM = findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton1 = findViewById(R.id.material_design_floating_action_menu_item1);
        floatingActionButton2 = findViewById(R.id.material_design_floating_action_menu_item2);

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked
                showNewRevenueDialog();
            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                showNewExpenseDialog();
            }
        });

        tvPendapatan = findViewById(R.id.tvPendapatan);
        tvPengeluaran = findViewById(R.id.tvPengeluaran);
        tvValuePendapatan = findViewById(R.id.tvValuePendapatan);
        tvValuePengeluaran = findViewById(R.id.tvValuePengeluaran);
        linearLayout = findViewById(R.id.emptyview);
        footer = findViewById(R.id.footer);
        tvSaldo = findViewById(R.id.currentSaldo);

        // Initial ListView Pengeluarann
        ListViewPengeluaran = findViewById(R.id.ListPengeluaran);
        // Initial ListView Pendapatan
        ListViewPendapatan = findViewById(R.id.ListPendapatan);

        mainActivity = this;
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
     * Load Data From Database Expense And Revenue
     */
    public void GetSaldo (){
        int TotalPendapatanSaldo=0, TotalPengeluaranSaldo = 0, Saldo = 0;
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        cursorRev = db.rawQuery("select * from pendapatan", null);
        cursorExp = db.rawQuery("select * from pengeluaran", null);

        cursorRev.moveToFirst();
        for (int cc = 0; cc < cursorRev.getCount(); cc++) {
            cursorRev.moveToPosition(cc);
            // Get Item Detail
            TotalPendapatanSaldo += Integer.parseInt(cursorRev.getString(3).toString());
        }

        cursorExp.moveToFirst();
        for (int cc = 0; cc < cursorExp.getCount(); cc++) {
            cursorExp.moveToPosition(cc);
            // Get Item Detail
            TotalPengeluaranSaldo += Integer.parseInt(cursorExp.getString(3).toString());
        }

        Saldo = TotalPendapatanSaldo - TotalPengeluaranSaldo;
        tvSaldo.setText("Rp. "+String.valueOf(Saldo));
        // Set Pendapatan dan Pengeluaran Total
        tvValuePendapatan.setText("Rp. " + String.valueOf(TotalPendapatanSaldo));
        tvValuePengeluaran.setText("Rp. " + String.valueOf(TotalPengeluaranSaldo));
    }

    /**
     * Load ListView as CardView Expense and Revenue Set Update And Delete when User Clicked Each Item
     */
    public void LoadListView() {
        GetSaldo();
        // membuat sebuah adapter yang berfungsi untuk menampung data sementara sebelum di tampilkan ke dalam list view
        MyAdapterRevenue adapterPendapatan = new MyAdapterRevenue(this, DeskripsiPendapatan, KategoriPendapatan, JumlahPendapatan);
        //menampilkan / memasukan adapter ke dalam ListView
        ListViewPendapatan.setAdapter(adapterPendapatan);
        // Set On ClickListener on each Item
        ListViewPendapatan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get item clicked ID
                final String idPendapatan = IdPendapatan[position];
                // Get item clicked Desc
                final String deskripsiPendapatan = DeskripsiPendapatan[position];
                // Get item clicked Amount
                final String jumlahPendapatan = JumlahPendapatan[position];
                // Get item clicked Category
                final String kategoriPendapatan = KategoriPendapatan[position];
                // Set Option when item clicked
                final CharSequence[] dialogitem = {"Edit Pendapatan", "Hapus Pendapatan"};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Pilihan");
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                // User clicked the Update Option
                                showEditDialogRevenue(idPendapatan, kategoriPendapatan, deskripsiPendapatan, jumlahPendapatan);
                                break;
                            case 1:
                                // User clicked the Delete Option
                                SQLiteDatabase dbWrite = dataHelper.getWritableDatabase();
                                dbWrite.execSQL("delete from pendapatan where id = '"+idPendapatan+"'");
                                new MyAsynch().execute();
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });

        // membuat sebuah adapter yang berfungsi untuk menampung data sementara sebelum di tampilkan ke dalam list view
        MyAdapterExpense adapterPengeluaran = new MyAdapterExpense(this, DeskripsiPengeluaran, KategoriPengeluaran, JumlahPengeluaran);
        //menampilkan / memasukan adapter ke dalam ListView
        ListViewPengeluaran.setAdapter(adapterPengeluaran);
        // Set On ClickListener on each Item
        ListViewPengeluaran.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get item clicked ID
                final String idPengeluaran = IdPengeluaran[position];
                // Get item clicked Desc
                final String deskripsiPengeluaran = DeskripsiPengeluaran[position];
                // Get item clicked Amount
                final String jumlahPengeluaran = JumlahPengeluaran[position];
                // Get item clicked Category
                final String kategoriPengeluaran = KategoriPengeluaran[position];
                // Set Option when item clicked
                final CharSequence[] dialogitem = {"Edit Pengeluaran", "Hapus Pengeluaran"};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Pilihan");
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                // User clicked the Update Option
                                showEditDialogExpense(idPengeluaran, kategoriPengeluaran, deskripsiPengeluaran, jumlahPengeluaran);
                                break;
                            case 1:
                                SQLiteDatabase dbWrite = dataHelper.getWritableDatabase();
                                dbWrite.execSQL("delete from pengeluaran where id = '"+idPengeluaran+"'");
                                new MyAsynch().execute();
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });

        // Cek Ada Data Pengeluaran dan Pendapatan atau Tidak
        // Set Layout
        linearLayout.setVisibility(View.GONE);
        if (AdaDataPendapatan < 1 && AdaDataPengeluaran < 1){
            // Tampilkan Layout Tidak ada Data
            linearLayout.setVisibility(View.VISIBLE);
            ListViewPendapatan.setVisibility(View.GONE);
            ListViewPengeluaran.setVisibility(View.GONE);
//            footer.setVisibility(View.GONE);
        } else if (AdaDataPendapatan > 0 && AdaDataPengeluaran < 1){
            // Tampilkan Ada Data Pendapatan
            linearLayout.setVisibility(View.GONE);
            ListViewPendapatan.setVisibility(View.VISIBLE);
            ListViewPengeluaran.setVisibility(View.GONE);
//            footer.setVisibility(View.VISIBLE);
//            tvPendapatan.setVisibility(View.VISIBLE);
//            tvPengeluaran.setVisibility(View.GONE);
        } else if (AdaDataPengeluaran > 0 && AdaDataPendapatan < 1){
            // Tampilkan Ada Data Pengeluaran
            linearLayout.setVisibility(View.GONE);
            ListViewPengeluaran.setVisibility(View.VISIBLE);
            ListViewPendapatan.setVisibility(View.GONE);
//            footer.setVisibility(View.VISIBLE);
//            tvPendapatan.setVisibility(View.GONE);
//            tvPengeluaran.setVisibility(View.VISIBLE);
        } else {
            // Hilangkan Layout Tidak ada Data
            // Tampilkan Ada Data Pengeluaran & Pendapatan
//            tvPendapatan.setVisibility(View.VISIBLE);
//            tvPengeluaran.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            ListViewPendapatan.setVisibility(View.VISIBLE);
            ListViewPengeluaran.setVisibility(View.VISIBLE);
        }

        // Set 2 ListView Agar bisa di Scroll
        ListUtils.setDynamicHeight(ListViewPendapatan);
        ListUtils.setDynamicHeight(ListViewPengeluaran);

        ((ArrayAdapter) ListViewPengeluaran.getAdapter()).notifyDataSetInvalidated();
        ((ArrayAdapter) ListViewPendapatan.getAdapter()).notifyDataSetInvalidated();
    }

    /**
     * Form Dialog for Edit Revenue
     */
    private void showEditDialogRevenue(final String idPendapatan, String kategoriPendapatan, String deskripsiPendapatan, String jumlahPendapatan){
        // Initial Layout to Inflate the dialogbox
        LayoutInflater mainLayout = LayoutInflater.from(MainActivity.this);
        View mView = mainLayout.inflate(R.layout.dialog_edit_pendapatan, null);
        final EditText jumlah = mView.findViewById(R.id.inputJumlah);
        final EditText catatan = mView.findViewById(R.id.inputDesk);
        final TextView kategori = mView.findViewById(R.id.kategori);

        jumlah.setText(jumlahPendapatan);
        catatan.setText(deskripsiPendapatan);
        kategori.setText(kategoriPendapatan);
        // Build the Dialog
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(mView);
        alertDialogBuilderUserInput
                .setCancelable(false)
                // User Click the Save Button
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // Empty Field
                        if (jumlah.getText().toString().isEmpty() || catatan.getText().toString().isEmpty()){
                            Toast.makeText(getApplicationContext(), "Jumlah dan Deskripsi Harus Diisi !", Toast.LENGTH_LONG).show();
                        }
                        // Not Empty Field
                        else {
                            boolean amIValid = false;
                            try {
                                Integer.parseInt(jumlah.getText().toString());
                                //is a valid integer!
                                amIValid = true;
                            } catch (NumberFormatException e) {

                            }
                            if (amIValid){
                                SQLiteDatabase dbWrite = dataHelper.getWritableDatabase();
                                dbWrite.execSQL("UPDATE 'pendapatan' SET deskripsi = '"+catatan.getText().toString()+"' , jumlah = '"+jumlah.getText().toString()+"' where id = '"+idPendapatan+"'");
                                Toast.makeText(getApplicationContext(), "Berhasil Memperbarui Pendapatan", Toast.LENGTH_LONG).show();
                                new MyAsynch().execute();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Gagal Memperbarui Pendapatan", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                // User Click the Cancel Button
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.cancel();
                    }
                });
        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        // Show the Dialog
        alertDialogAndroid.show();
    }

    /**
     * Form Dialog for Edit Revenue
     */
    private void showEditDialogExpense(final String idPengeluaran, String kategoriPengeluaran, String deskripsiPengeluaran, String jumlahPengeluaran){
        // Initial Layout to Inflate the dialogbox
        LayoutInflater mainLayout = LayoutInflater.from(MainActivity.this);
        View mView = mainLayout.inflate(R.layout.dialog_edit_pengeluaran, null);
        final EditText jumlah = mView.findViewById(R.id.inputJumlah);
        final EditText catatan = mView.findViewById(R.id.inputDesk);
        final TextView kategori = mView.findViewById(R.id.kategori);

        jumlah.setText(jumlahPengeluaran);
        catatan.setText(deskripsiPengeluaran);
        kategori.setText(kategoriPengeluaran);
        // Build the Dialog
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(mView);
        alertDialogBuilderUserInput
                .setCancelable(false)
                // User Click the Save Button
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // Empty Field
                        if (jumlah.getText().toString().isEmpty() || catatan.getText().toString().isEmpty()){
                            Toast.makeText(getApplicationContext(), "Jumlah dan Deskripsi Harus Diisi !", Toast.LENGTH_LONG).show();
                        }
                        // Not Empty Field
                        else {
                            boolean amIValid = false;
                            try {
                                Integer.parseInt(jumlah.getText().toString());
                                //is a valid integer!
                                amIValid = true;
                            } catch (NumberFormatException e) {

                            }
                            if (amIValid){
                                SQLiteDatabase dbWrite = dataHelper.getWritableDatabase();
                                dbWrite.execSQL("UPDATE 'pengeluaran' SET deskripsi = '"+catatan.getText().toString()+"' , jumlah = '"+jumlah.getText().toString()+"' where id = '"+idPengeluaran+"'");
                                Toast.makeText(getApplicationContext(), "Berhasil Memperbarui Pengeluaran", Toast.LENGTH_LONG).show();
                                new MyAsynch().execute();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Gagal Memperbarui Pengeluaran", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                // User Click the Cancel Button
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.cancel();
                    }
                });
        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        // Show the Dialog
        alertDialogAndroid.show();
    }

    /**
     * Adapter for Set Data ListView as CardView Revenue
     */
    private class MyAdapterRevenue extends ArrayAdapter {
        String list_deskripsi[];
        String list_kategori[];
        String list_jumlah[];
        Activity activity;

        //konstruktor
        public MyAdapterRevenue(MainActivity mainActivity, String[] list_deskripsi, String list_kategori[], String list_jumlah[]) {
            super(mainActivity, R.layout.item_pendapatan, list_deskripsi);
            activity = mainActivity;
            this.list_deskripsi = list_deskripsi;
            this.list_kategori  = list_kategori;
            this.list_jumlah    = list_jumlah;
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
            tanggal.setText(date);
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
        Activity activity;

        //konstruktor
        public MyAdapterExpense(MainActivity mainActivity, String[] list_deskripsi, String list_kategori[], String list_jumlah[]) {
            super(mainActivity, R.layout.item_pengeluaran, list_deskripsi);
            activity = mainActivity;
            this.list_deskripsi = list_deskripsi;
            this.list_kategori  = list_kategori;
            this.list_jumlah    = list_jumlah;
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
            tanggal.setText(date);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.minus_circular_32));

            return v;
        }
    }

    /**
     * Load Data From Database Expense
     */
    public void GetDataExpense (){
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        cursor = db.rawQuery("select * from pengeluaran inner join kategori_pengeluaran where pengeluaran.idkategori = kategori_pengeluaran.id  and pengeluaran.tanggal = '"+date+"'", null);
        AdaDataPengeluaran = cursor.getCount();;
        // Initial Array Size
        int totalData = cursor.getCount();
        IdPengeluaran         = new String[totalData];
        IdKategoriPengeluaran = new String[totalData];
        KategoriPengeluaran   = new String[totalData];
        DeskripsiPengeluaran  = new String[totalData];
        JumlahPengeluaran     = new String[totalData];

        cursor.moveToFirst();
        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            // Get Item Detail
            IdPengeluaran[cc]         = cursor.getString(0).toString();
            IdKategoriPengeluaran[cc] = cursor.getString(1).toString();
            DeskripsiPengeluaran[cc]  = cursor.getString(2).toString();
            JumlahPengeluaran[cc]     = cursor.getString(3).toString();
            KategoriPengeluaran[cc]   = cursor.getString(6).toString();
        }
    }

    /**
     * Load Data From Database Revenue
     */
    public void GetDataRevenue (){
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        cursor = db.rawQuery("select * from pendapatan inner join kategori_pendapatan where pendapatan.idkategori = kategori_pendapatan.id and pendapatan.tanggal = '"+date+"'", null);
        AdaDataPendapatan = cursor.getCount();

        // Initial Array Size
        int totalData = cursor.getCount();
        IdPendapatan         = new String[totalData];
        IdKategoriPendapatan = new String[totalData];
        KategoriPendapatan   = new String[totalData];
        DeskripsiPendapatan  = new String[totalData];
        JumlahPendapatan     = new String[totalData];

        cursor.moveToFirst();
        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            // Get Item Detail
            IdPendapatan[cc]         = cursor.getString(0).toString();
            IdKategoriPendapatan[cc] = cursor.getString(1).toString();
            DeskripsiPendapatan[cc]  = cursor.getString(2).toString();
            JumlahPendapatan[cc]     = cursor.getString(3).toString();
            KategoriPendapatan[cc]   = cursor.getString(6).toString();
        }
    }

    /**
     * Load Data From Database Expense Category
     */
    public void GetCategoryExpense (){
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM kategori_pengeluaran", null);
        // Initial Array Size
        SpinIdKategoriPengeluaran   = new String[cursor.getCount()];
        SpinNamaKategoriPengeluaran = new String[cursor.getCount()];
        cursor.moveToFirst();
        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            // Get Id
            SpinIdKategoriPengeluaran[cc] = cursor.getString(0).toString();
            // Get Item Name
            SpinNamaKategoriPengeluaran[cc] = cursor.getString(1).toString();
        }
    }

    /**
     * Load Data From Database Revenue Category
     */
    public void GetCategoryRevenue (){
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM kategori_pendapatan", null);
        // Initial Array Size
        SpinIdKategoriPendapatan   = new String[cursor.getCount()];
        SpinNamaKategoriPendapatan = new String[cursor.getCount()];
        cursor.moveToFirst();
        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            // Get Id
            SpinIdKategoriPendapatan[cc] = cursor.getString(0).toString();
            // Get Item Name
            SpinNamaKategoriPendapatan[cc] = cursor.getString(1).toString();
        }
    }

    /**
     * Custom Adapater For
     * Inflate Kategori Expense To Spinner
     */
    public void loadKategoriExpense() {
        GetCategoryExpense();
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, SpinNamaKategoriPengeluaran);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinKategoriPengeluaran.setAdapter(dataAdapter);
        spinKategoriPengeluaran.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SELECTED_KATEGORI_ID_PENGELUARAN = Integer.parseInt(SpinIdKategoriPengeluaran[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Custom Adapater For
     * Inflate Kategori Revenue To Spinner
     */
    public void loadKategoriRevenue() {
        GetCategoryRevenue();
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, SpinNamaKategoriPendapatan);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinKategoriPendapatan.setAdapter(dataAdapter);
        spinKategoriPendapatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SELECTED_KATEGORI_ID_PENDAPATAN = Integer.parseInt(SpinIdKategoriPendapatan[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Form Dialog for New Expense
     */
    public void showNewExpenseDialog() {
        // Initial Layout to Inflate the dialogbox
        LayoutInflater mainLayout = LayoutInflater.from(MainActivity.this);
        View mView = mainLayout.inflate(R.layout.dialog_tambah_pengeluaran, null);
        final EditText jumlah = mView.findViewById(R.id.inputJumlah);
        final EditText catatan = mView.findViewById(R.id.inputDesk);
        spinKategoriPengeluaran = mView.findViewById(R.id.spinner);
        loadKategoriExpense();
        // Build the Dialog
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(mView);
        alertDialogBuilderUserInput
                .setCancelable(false)
                // User Click the Save Button
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // Empty Field
                        if (jumlah.getText().toString().isEmpty() || catatan.getText().toString().isEmpty()){
                            Toast.makeText(getApplicationContext(), "Jumlah dan Deskripsi Harus Diisi !", Toast.LENGTH_LONG).show();
                        }
                        // Not Empty Field
                        else {
                            boolean amIValid = false;
                            try {
                                Integer.parseInt(jumlah.getText().toString());
                                //is a valid integer!
                                amIValid = true;
                            } catch (NumberFormatException e) {

                            }
                            if (amIValid){
                                SQLiteDatabase dbWrite = dataHelper.getWritableDatabase();
                                dbWrite.execSQL("INSERT INTO 'pengeluaran' ('idkategori', 'deskripsi', 'jumlah', 'tanggal') VALUES ('"+SELECTED_KATEGORI_ID_PENGELUARAN+"', '"+catatan.getText().toString()+"', '"+jumlah.getText().toString()+"', '"+date+"') ");
                                Toast.makeText(getApplicationContext(), "Berhasil Menambah Pengeluaran", Toast.LENGTH_LONG).show();
                                new MyAsynch().execute();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Gagal Menambah Pengeluaran", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                // User Click the Cancel Button
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.cancel();
                    }
                });
        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        // Show the Dialog
        alertDialogAndroid.show();
    }

    /**
     * Form Dialog for New Revenue
     */
    public void showNewRevenueDialog() {
        // Initial Layout to Inflate the dialogbox
        LayoutInflater mainLayout = LayoutInflater.from(MainActivity.this);
        View mView = mainLayout.inflate(R.layout.dialog_tambah_pendapatan, null);
        final EditText jumlah = mView.findViewById(R.id.inputJumlah);
        final EditText catatan = mView.findViewById(R.id.inputDesk);
        spinKategoriPendapatan = mView.findViewById(R.id.spinnerKatgoriPendapatan);
        loadKategoriRevenue();
        // Build the Dialog
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(mView);
        alertDialogBuilderUserInput
                .setCancelable(false)
                // User Click the Save Button
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // Empty Field
                        if (jumlah.getText().toString().isEmpty() || catatan.getText().toString().isEmpty()){
                            Toast.makeText(getApplicationContext(), "Jumlah dan Deskripsi Harus Diisi !", Toast.LENGTH_LONG).show();
                        }
                        // Not Empty Field
                        else {
                            boolean amIValid = false;
                            try {
                                Integer.parseInt(jumlah.getText().toString());
                                //is a valid integer!
                                amIValid = true;
                            } catch (NumberFormatException e) {

                            }
                            if (amIValid){
                                SQLiteDatabase dbWrite = dataHelper.getWritableDatabase();
                                dbWrite.execSQL("INSERT INTO 'pendapatan' ('idkategori', 'deskripsi', 'jumlah', 'tanggal') VALUES ('"+SELECTED_KATEGORI_ID_PENDAPATAN+"', '"+catatan.getText().toString()+"', '"+jumlah.getText().toString()+"', '"+date+"') ");
                                Toast.makeText(getApplicationContext(), "Berhasil Menambah Pendapatan", Toast.LENGTH_LONG).show();
                                new MyAsynch().execute();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Gagal Menambah Pendapatan", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                // User Click the Cancel Button
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.cancel();
                    }
                });
        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        // Show the Dialog
        alertDialogAndroid.show();
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
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
            // Handle the camera action
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_dompet) {
            Intent intent = new Intent(MainActivity.this, DompetActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_kategori_pendapatan) {
            Intent intent = new Intent(MainActivity.this, KategoriPendapatan.class);
            startActivity(intent);
        } else if (id == R.id.nav_kategori_pengeluaran) {
            Intent intent = new Intent(MainActivity.this, KategoriPengeluaran.class);
            startActivity(intent);
        } else if (id == R.id.nav_monthly) {
            Intent intent = new Intent(MainActivity.this, ReportActivity.class);
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
        builder.setIcon(R.mipmap.ic_launcher_foreground);
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
