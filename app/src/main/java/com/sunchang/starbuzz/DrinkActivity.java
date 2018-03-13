package com.sunchang.starbuzz;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DrinkActivity extends AppCompatActivity {

    public static final String EXTRA_DRINKNO = "drinkNo";

    private ImageView photo;

    private TextView name;

    private TextView description;

    private CheckBox favorite;

    private int drinkNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);

        drinkNo = this.getIntent().getIntExtra(this.EXTRA_DRINKNO, 0);

        new ReadDrinkTask().execute(drinkNo);
    }

    public void onFavoriteClick(View view) {
        new UpdateDrinkTask().execute(drinkNo);
    }

    //读取数据库，显示饮品的详细信息
    private class ReadDrinkTask extends AsyncTask<Integer, Void, Boolean> {

        private String nameText;

        private String descriptionText;

        private int photoId;

        private boolean isFavorite;

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                photo = (ImageView) DrinkActivity.this.findViewById(R.id.photo);
                name = (TextView) DrinkActivity.this.findViewById(R.id.name);
                description = (TextView) DrinkActivity.this.findViewById(R.id.description);
                favorite = (CheckBox) DrinkActivity.this.findViewById(R.id.favorite);

                photo.setImageResource(photoId);
                photo.setContentDescription(nameText);
                name.setText(nameText);
                description.setText(descriptionText);
                favorite.setChecked(isFavorite);
            } else {
                Toast.makeText(DrinkActivity.this, "Database unavailable", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(Integer... drinks) {
            int drinkNo = drinks[0];
            try {
                SQLiteOpenHelper sqLiteOpenHelper = new StarbuzzDatabaseHelper(DrinkActivity.this);
                SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
                Cursor cursor = db.query("DRINK",
                        new String[]{"NAME", "DESCRIPTION", "IMAGE_RESOURCE_ID", "FAVORITE"},
                        "_id = ?",
                        new String[]{Integer.toString(drinkNo)},
                        null, null, null);
                if (cursor.moveToFirst()) {
                    nameText = cursor.getString(0);
                    descriptionText = cursor.getString(1);
                    photoId = cursor.getInt(2);
                    isFavorite = (cursor.getInt(3) == 1);
                }
                cursor.close();
                db.close();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    //读取数据库，更新DRINK表中的FAVORITE列
    private class UpdateDrinkTask extends AsyncTask<Integer, Void, Boolean> {

        private ContentValues drinkValues;

        @Override
        protected void onPreExecute() {
            favorite = (CheckBox) DrinkActivity.this.findViewById(R.id.favorite);
            drinkValues = new ContentValues();
            drinkValues.put("FAVORITE", favorite.isChecked());
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast.makeText(DrinkActivity.this, "Database unavailable", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(Integer... drinks) {
            int drinkNo = drinks[0];
            try {
                SQLiteOpenHelper sqLiteOpenHelper = new StarbuzzDatabaseHelper(DrinkActivity.this);
                SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
                db.update("DRINK", drinkValues, "_id = ?",
                        new String[]{Integer.toString(drinkNo)});
                db.close();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
    }

}
