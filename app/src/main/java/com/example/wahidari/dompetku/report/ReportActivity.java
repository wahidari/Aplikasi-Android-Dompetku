package com.example.wahidari.dompetku.report;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wahidari.dompetku.database.DataHelper;
import com.example.wahidari.dompetku.DompetActivity;
import com.example.wahidari.dompetku.KategoriPendapatan;
import com.example.wahidari.dompetku.KategoriPengeluaran;
import com.example.wahidari.dompetku.MainActivity;
import com.example.wahidari.dompetku.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by @wahidari on 13/06/18.
 */

public class ReportActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Initialize Variable
     */

    // For Collect Data
    String[] IdKategoriPendapatan, TotalDataPerKategoriPendapatan, JumlahPerKategoriPendapatan, JenisKategoriPendapatan;
    String[] IdKategoriPengeluaran, TotalDataPerKategoriPengeluaran, JumlahPerKategoriPengeluaran, JenishKategoriPengeluaran;
    int AdaDataPendapatan, AdaDataPengeluaran;
    int TotalPengeluaran, TotalPendapatan = 0;

    // Database
    protected Cursor cursor;
    DataHelper dataHelper;

    // Spinner
    int nowYear;
    String newMonth;
    MaterialSpinner spinMonth;

    // MPChart
    PieChart mChartRevenue, mChartExpense;
    List<PieEntry> chartValueRevenue, chartValueExpense;
    List<Integer> chartColorRevenue, chartColorExpense;
    String[] arrayColorRevenue, arrayColorExpense;
    ImageButton saveChartRevenue, saveChartExpense;

    LinearLayout contentlayout;
    CardView cardViewRevenue, cardViewExpense;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Spinner Month - Year
        spinMonth = findViewById(R.id.bulantahun);
        contentlayout = findViewById(R.id.contentlayout);
        // Revenue
        cardViewRevenue = findViewById(R.id.card_view_revenue);
        arrayColorRevenue = getResources().getStringArray(R.array.chart_color_revenue);
        mChartRevenue = findViewById(R.id.chartRevenue);
        saveChartRevenue = findViewById(R.id.savebtnrevenue);
        saveChartRevenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionStatus("Pendapatan");
            }
        });
        // Expense
        cardViewExpense = findViewById(R.id.card_view_expense);
        arrayColorExpense = getResources().getStringArray(R.array.chart_color_expense);
        mChartExpense = findViewById(R.id.chartExpense);
        saveChartExpense = findViewById(R.id.savebtnexpense);
        saveChartExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionStatus("Pengeluaran");
            }
        });
        // Setting Chart
        settingUpchart();
        // Load Spinner Month
        loadMonth();

        dataHelper = new DataHelper(this);
    }

    /**
     * Cek Permission Write External Storage
     */
    public void permissionStatus(String tipe){
        if (ActivityCompat.checkSelfPermission(ReportActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ReportActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(ReportActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(ReportActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }
        } else {
            //You already have the permission, just go ahead.
            if (tipe == "Pendapatan"){
                toSaveChartRevenue();
            } else {
                toSaveChartExpense();
            }
        }
    }

    /**
     * Settup Chart MPCHART
     */
    public void settingUpchart() {
        Description description = new Description();
        description.setTextColor(000);
        description.setText("Chart Data");
        // Revenue
        mChartRevenue.setDescription(description);
        mChartRevenue.setRotationEnabled(true);
        mChartRevenue.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e == null)
                    return;
                PieEntry pe = (PieEntry) e;
                Toast.makeText(getApplicationContext(),
                        String.valueOf(Math.round(pe.getValue())) + " Pendapatan Di Kategori " +
                                pe.getLabel(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {
            }
        });
        // Expense
        mChartExpense.setDescription(description);
        mChartExpense.setRotationEnabled(true);
        mChartExpense.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e == null)
                    return;
                PieEntry pe = (PieEntry) e;
                Toast.makeText(getApplicationContext(),
                        String.valueOf(Math.round(pe.getValue())) + " Pengeluaran Di Kategori " +
                                pe.getLabel(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {
            }
        });

    }

    /**
     * Spinner For Add Month And Action Selected Month - Year
     */
    public void loadMonth() {
        final String[] Bulan = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        List<String> tospinner = new ArrayList<>();
        nowYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 12; i++) {
            tospinner.add(Bulan[i] + " - " + String.valueOf(nowYear));
        }
        tospinner.add(0, "Pilih Bulan - Tahun");
        spinMonth.setItems(tospinner);
        spinMonth.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if (position != 0) {
                    newMonth = String.format("%02d", position);
                    Toast.makeText(getApplicationContext(), "Laporan Bulan "+Bulan[position-1], Toast.LENGTH_SHORT).show();
                    getDataRevenueByMonth(newMonth);
                    getDataExpenseByMonth(newMonth);
                    setDataRevenueToChart();
                    setDataExpenseToChart();
                } else {
                    contentlayout.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Set Data Revenue To Chart
     */
    public void setDataRevenueToChart(){
        if (AdaDataPendapatan != 0) {
            cardViewRevenue.setVisibility(View.VISIBLE);
            chartValueRevenue = new ArrayList<PieEntry>();
            chartColorRevenue = new ArrayList<Integer>();
            int total = 0;
            for (int i = 0; i < AdaDataPendapatan; i++) {
                total += Integer.parseInt(TotalDataPerKategoriPendapatan[i]);
                chartValueRevenue.add(new PieEntry(Integer.parseInt(TotalDataPerKategoriPendapatan[i]), JenisKategoriPendapatan[i]));
                chartColorRevenue.add(Color.parseColor(arrayColorRevenue[i]));
            }

            PieDataSet dataSet = new PieDataSet(chartValueRevenue, "");
            dataSet.setSliceSpace(3);
            dataSet.setSelectionShift(5);
            dataSet.setColors(chartColorRevenue);
            dataSet.setValueFormatter(new MyValueFormatter());

            Legend l = mChartRevenue.getLegend();
            l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
            l.setWordWrapEnabled(true);
            l.setXEntrySpace(9);
            l.setYEntrySpace(5);

            PieData data = new PieData(dataSet);
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.WHITE);
            mChartRevenue.setData(data);
            String center = String.valueOf(total) + " Pendapatan \nDengan Total :\nRp. " + String.valueOf(TotalPendapatan);
            mChartRevenue.setCenterText(center);

            mChartRevenue.highlightValues(null);
            mChartRevenue.invalidate();
            mChartRevenue.animateXY(1400, 1400);

        } else {
            Toast.makeText(getApplicationContext(), "Tidak Ada Data Pendapatan", Toast.LENGTH_SHORT).show();
            cardViewRevenue.setVisibility(View.GONE);
        }
    }

    /**
     * Set Data Expense To Chart
     */
    public void setDataExpenseToChart(){
        if (AdaDataPengeluaran != 0) {
            cardViewExpense.setVisibility(View.VISIBLE);
            chartValueExpense = new ArrayList<PieEntry>();
            chartColorExpense = new ArrayList<Integer>();
            int total = 0;
            for (int i = 0; i < AdaDataPengeluaran; i++) {
                total += Integer.parseInt(TotalDataPerKategoriPengeluaran[i]);
                chartValueExpense.add(new PieEntry(Integer.parseInt(TotalDataPerKategoriPengeluaran[i]), JenishKategoriPengeluaran[i]));
                chartColorExpense.add(Color.parseColor(arrayColorExpense[i]));
            }

            PieDataSet dataSet = new PieDataSet(chartValueExpense, "");
            dataSet.setSliceSpace(3);
            dataSet.setSelectionShift(5);
            dataSet.setColors(chartColorExpense);
            dataSet.setValueFormatter(new MyValueFormatter());

            Legend l = mChartExpense.getLegend();
            l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
            l.setWordWrapEnabled(true);
            l.setXEntrySpace(9);
            l.setYEntrySpace(5);

            PieData data = new PieData(dataSet);
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.WHITE);
            mChartExpense.setData(data);
            String center = String.valueOf(total) + " Pengeluaran \nDengan Total :\nRp. " + String.valueOf(TotalPengeluaran);
            mChartExpense.setCenterText(center);

            mChartExpense.highlightValues(null);
            mChartExpense.invalidate();
            mChartExpense.animateXY(1400, 1400);

        } else {
            Toast.makeText(getApplicationContext(), "Tidak Ada Data Pengeluaran", Toast.LENGTH_SHORT).show();
            cardViewExpense.setVisibility(View.GONE);
        }
    }

    /**
     * Get Data Revenue By Month
     */
    public void getDataRevenueByMonth(String month){
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT p.idkategori, count(p.idkategori), sum(p.jumlah), k.jenis FROM pendapatan p LEFT JOIN kategori_pendapatan k ON p.idkategori=k.id where strftime('%m', tanggal) = '"+month+"' and strftime('%Y', tanggal) = '"+nowYear+"' group by idkategori", null);
        AdaDataPendapatan = cursor.getCount();

        // Initial Array Size
        int totalData = cursor.getCount();
        IdKategoriPendapatan           = new String[totalData];
        TotalDataPerKategoriPendapatan = new String[totalData];
        JumlahPerKategoriPendapatan    = new String[totalData];
        JenisKategoriPendapatan        = new String[totalData];

        cursor.moveToFirst();
        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            // Get Item Detail
            IdKategoriPendapatan[cc]           = cursor.getString(0).toString();
            TotalDataPerKategoriPendapatan[cc] = cursor.getString(1).toString();
            JumlahPerKategoriPendapatan[cc]    = cursor.getString(2).toString();
            JenisKategoriPendapatan[cc]        = cursor.getString(3).toString();
            TotalPendapatan  += Integer.parseInt(cursor.getString(2).toString());
        }
    }

    /**
     * Get Data Revenue By Month
     */
    public void getDataExpenseByMonth(String month){
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT p.idkategori, count(p.idkategori), sum(p.jumlah), k.jenis FROM pengeluaran p LEFT JOIN kategori_pengeluaran k ON p.idkategori=k.id where strftime('%m', tanggal) = '"+month+"' and strftime('%Y', tanggal) = '"+nowYear+"' group by idkategori", null);
        AdaDataPengeluaran = cursor.getCount();

        // Initial Array Size
        int totalData = cursor.getCount();
        IdKategoriPengeluaran           = new String[totalData];
        TotalDataPerKategoriPengeluaran = new String[totalData];
        JumlahPerKategoriPengeluaran    = new String[totalData];
        JenishKategoriPengeluaran       = new String[totalData];

        cursor.moveToFirst();
        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            // Get Item Detail
            IdKategoriPengeluaran[cc]           = cursor.getString(0).toString();
            TotalDataPerKategoriPengeluaran[cc] = cursor.getString(1).toString();
            JumlahPerKategoriPengeluaran[cc]    = cursor.getString(2).toString();
            JenishKategoriPengeluaran[cc]       = cursor.getString(3).toString();
            TotalPengeluaran  += Integer.parseInt(cursor.getString(2).toString());
        }
    }

    /**
     * Change Decimal Format Of Chart Data
     */
    public class MyValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0"); // use one decimal if needed
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            return mFormat.format(value) + ""; // e.g. append a dollar-sign
        }
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
            Intent intent = new Intent(ReportActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_dompet) {
            Intent intent = new Intent(ReportActivity.this, DompetActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_kategori_pendapatan) {
            Intent intent = new Intent(ReportActivity.this, KategoriPendapatan.class);
            startActivity(intent);
        } else if (id == R.id.nav_kategori_pengeluaran) {
            Intent intent = new Intent(ReportActivity.this, KategoriPengeluaran.class);
            startActivity(intent);
        } else if (id == R.id.nav_monthly) {
            Toast.makeText(this, "Laporan", Toast.LENGTH_SHORT).show();
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

    /**
     * To Save Chart Revenue Into Gallery
     */
    private void toSaveChartRevenue() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        String name = "Laporan_Pendapatan_" + timeStamp;
        try {
            mChartRevenue.saveToGallery(name, 100);
            Toast.makeText(getApplicationContext(), "Berhasil Menyimpan Laporan Pendapatan", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            System.out.println("Gagal Save "+e);
        }
    }

    /**
     * To Save Chart Expense Into Gallery
     */
    private void toSaveChartExpense() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        String name = "Laporan_Pengeluaran_" + timeStamp;
        try {
            mChartExpense.saveToGallery(name, 100);
            Toast.makeText(getApplicationContext(), "Berhasil Menyimpan Laporan Pengeluaran", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            System.out.println("Gagal Save "+e);
        }
    }
}
