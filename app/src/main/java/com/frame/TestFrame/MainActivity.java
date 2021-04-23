package com.frame.TestFrame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.frame.TestFrame.View.Test_view;

public class MainActivity extends AppCompatActivity {
    Test_view test_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test_view = findViewById(R.id.test_view);
        test_view.setID(R.raw.ic_595_view);
        test_view.custom();
        test_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = "你好";
                if (test_view.getOnclick_name().contains(name)) {
//                  ...
                } else {
//                  ...
                }
            }
        });

    }
}