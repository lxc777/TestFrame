package com.frame.TestFrame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.frame.TestFrame.Bean.ProvinceItem;
import com.frame.TestFrame.View.Test_view;

import java.util.ArrayList;
import java.util.List;

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
                if (test_view.getOnclick_name()!=null) {
                    test_view.setColor_change(test_view.getChang_ID(),1);
                    Toast.makeText(getApplicationContext(),""+test_view.getChang_ID(),Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext()," 失败 ",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}