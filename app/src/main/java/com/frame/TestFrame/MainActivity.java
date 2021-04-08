package com.frame.TestFrame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.frame.TestFrame.View.Test_view;

public class MainActivity extends AppCompatActivity {
    Test_view test_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test_view=findViewById(R.id.test);
        test_view.setID(R.raw.ic__138);

    }
    public void Test(){
        System.out.println("测试成功");
    }
}