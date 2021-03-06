package com.sunchang.starbuzz;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class DrinkCategoryActivity extends ListActivity {

    private SQLiteDatabase db;

    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new ReadDrinkTask().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, DrinkActivity.class);
        intent.putExtra(DrinkActivity.EXTRA_DRINKNO, (int)id);
        this.startActivity(intent);
    }

    private class ReadDrinkTask extends AsyncTask<Void, Void, Boolean> {

        private ListView listDrinks;

        @Override
        protected void onPreExecute() {
            listDrinks = DrinkCategoryActivity.this.getListView();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast.makeText(DrinkCategoryActivity.this, "Database unavailable",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                SQLiteOpenHelper sqLiteOpenHelper = new StarbuzzDatabaseHelper(DrinkCategoryActivity.this);
                db = sqLiteOpenHelper.getReadableDatabase();
                cursor = db.query("DRINK", new String[]{"_id", "NAME"},
                        null, null, null, null, null);
                CursorAdapter listAdapter = new SimpleCursorAdapter(DrinkCategoryActivity.this,
                        android.R.layout.simple_list_item_1,
                        cursor,
                        new String[]{"NAME"},
                        new int[]{android.R.id.text1},
                        0);
                listDrinks.setAdapter(listAdapter);
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
    }
}
