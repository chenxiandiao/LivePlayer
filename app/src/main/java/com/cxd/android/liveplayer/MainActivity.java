package com.cxd.android.liveplayer;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cxd.android.liveplayer.view.HomeMainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction =  getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_content, new HomeMainFragment());
        transaction.commit();
    }
}
