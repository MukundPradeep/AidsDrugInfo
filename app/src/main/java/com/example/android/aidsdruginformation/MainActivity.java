package com.example.android.aidsdruginformation;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private DrugAdapter todoAdapter;
    private static final int DRUG_LOADER = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getSupportLoaderManager().destroyLoader(DRUG_LOADER);
        getSupportLoaderManager().initLoader(DRUG_LOADER, null, this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if(isNetworkAvailable()) {
            Intent drugServiceIntent = new Intent(this, GetDrugsService.class);
            startService(drugServiceIntent);
        }else{
            Toast.makeText(this, "This application requires an active internet connection to update its data",
                    Toast.LENGTH_LONG).show();
        }

        DrugDbHelper helper = new DrugDbHelper(this);

        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor todoCursor = db.rawQuery("SELECT  * FROM drugs", null);

        Log.v("Im here", "to do this");

        ListView lvItems = (ListView) findViewById(R.id.listView);
// Setup cursor adapter using cursor from last step
        todoAdapter = new DrugAdapter(this, todoCursor);
// Attach cursor adapter to the ListView
        lvItems.setAdapter(todoAdapter);
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                String id= cursor.getString(0);
                String drug_id= cursor.getString(1);
                String approval_status= cursor.getString(2);
                String drug_class= cursor.getString(3);
                String name= cursor.getString(4);
                String company= cursor.getString(5);
                String image_url= cursor.getString(6);
                String approved_use= cursor.getString(7);

                DrugData dg = new DrugData();
                dg.id = id;
                dg.drug_id = drug_id;
                dg.approval_status = approval_status;
                dg.drug_class = drug_class;
                dg.name = name;
                dg.company = company;
                dg.image_url = image_url;
                dg.approved_use = approved_use;

                if (cursor != null) {

                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
//                            .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                    intent.putExtra("MyClass", dg);
                    startActivity(intent);
                }
            }
        });

    }

    public void blahblah(View v){
        Intent downloadIntent = new Intent(getApplicationContext(), DetailActivity.class);
        startActivity(downloadIntent);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        switch (id) {
//            case DRUG_LOADER:
//                // Returns a new CursorLoader

                        CursorLoader c =new CursorLoader(
                        this,   // Parent activity context
                        DrugsContract.DrugsEntry.CONTENT_URI,        // Table to query
                        null,     // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            return c;
//            default:
//                // An invalid id was passed in
//                return null;
//        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        todoAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        todoAdapter.swapCursor(null);
    }
}
