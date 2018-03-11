package com.sunchang.starbuzz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DrinkActivity extends AppCompatActivity {

    public static final String EXTRA_DRINKNO = "drinkNo";

    private ImageView photo;

    private TextView name;

    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);

        int drinkNo = this.getIntent().getIntExtra(this.EXTRA_DRINKNO, 0);
        Drink drink = Drink.drinks[drinkNo];

        photo = (ImageView) this.findViewById(R.id.photo);
        name = (TextView) this.findViewById(R.id.name);
        description = (TextView) this.findViewById(R.id.description);

        photo.setImageResource(drink.getImageResourceId());
        photo.setContentDescription(drink.getName());
        name.setText(drink.getName());
        description.setText(drink.getDescription());
    }
}
