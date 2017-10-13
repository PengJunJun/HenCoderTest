package com.hencodertest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private MintView mintView;
    private TextView bodyWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bodyWeight = (TextView) findViewById(R.id.tv_weight);
        mintView = (MintView) findViewById(R.id.mintView);
        mintView.setOnSizeChangeListener(new MintView.OnSizeChangeListener() {
            @Override
            public void onSizeChange(String weight) {
                bodyWeight.setText(weight);
            }
        });
    }
}
