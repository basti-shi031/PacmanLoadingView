package com.basti.loadingview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.basti.loadingviewlib.PacManView;

public class MainActivity extends AppCompatActivity {

    private PacManView pacManView;
    private Button Bt_start, Bt_finish, Bt_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initEvent();
    }

    private void initEvent() {

        Bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pacManView.startLoading();

            }
        });

        Bt_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pacManView.finishLoading();
            }
        });

        Bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pacManView.cancelLoading();
            }
        });

    }

    private void initView() {
        pacManView = (PacManView) findViewById(R.id.loadingview1);
        Bt_start = (Button) findViewById(R.id.loading);
        Bt_finish = (Button) findViewById(R.id.finish);
        Bt_cancel = (Button) findViewById(R.id.cancel);
    }
}
