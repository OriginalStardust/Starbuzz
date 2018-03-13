package com.sunchang.starbuzz;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class TopLevelActivity extends Activity {

    private ListView listView;

    private SQLiteDatabase db;

    private Cursor favoritesCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_level);

        listView = (ListView) this.findViewById(R.id.list_options);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Intent intent = new Intent(TopLevelActivity.this, DrinkCategoryActivity.class);
                    TopLevelActivity.this.startActivity(intent);
                }
            }
        });

        new ReadFavoritesTask().execute();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            SQLiteOpenHelper sqLiteOpenHelper = new StarbuzzDatabaseHelper(this);
            db = sqLiteOpenHelper.getReadableDatabase();
            Cursor newCursor = db.query("DRINK", new String[]{"_id", "NAME"}, "FAVORITE = 1",
                    null, null, null, null);
            ListView listFavorites = (ListView) this.findViewById(R.id.list_favorites);
            CursorAdapter adapter = (CursorAdapter) listFavorites.getAdapter();
            adapter.changeCursor(newCursor);
            favoritesCursor = newCursor;
        } catch (SQLException e) {
            Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        favoritesCursor.close();
        db.close();
    }

    private class ReadFavoritesTask extends AsyncTask<Void, Void, Boolean> {

        ListView listFavorites;

        @Override
        protected void onPreExecute() {
            listFavorites = (ListView) TopLevelActivity.this.findViewById(R.id.list_favorites);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                listFavorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(TopLevelActivity.this, DrinkActivity.class);
                        intent.putExtra(DrinkActivity.EXTRA_DRINKNO, (int) l);
                        TopLevelActivity.this.startActivity(intent);
                    }
                });
            } else {
                Toast.makeText(TopLevelActivity.this, "Database unavailable", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                SQLiteOpenHelper sqLiteOpenHelper = new StarbuzzDatabaseHelper(TopLevelActivity.this);
                db = sqLiteOpenHelper.getReadableDatabase();
                favoritesCursor = db.query("DRINK",
                        new String[]{"_id", "NAME"},
                        "FAVORITE = 1",
                        null, null, null, null);
                CursorAdapter favoriteAdapter = new SimpleCursorAdapter(TopLevelActivity.this,
                        android.R.layout.simple_list_item_1,
                        favoritesCursor,
                        new String[]{"NAME"},
                        new int[]{android.R.id.text1},
                        0);
                listFavorites.setAdapter(favoriteAdapter);
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
    }

}
