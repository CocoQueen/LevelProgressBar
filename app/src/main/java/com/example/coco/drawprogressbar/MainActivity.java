package com.example.coco.drawprogressbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LevelProgressBar levelProgressBar;
    private Button mBtn1;
    private Button mBtn2;
    private Button mBtn3;
    private Button mBtn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initDate();

    }

    private void initDate() {
        levelProgressBar.setLevel(4);
        String [] texts ={"等级一","等级二","等级三","等级四"};
        levelProgressBar.setLevelTexts(texts);
//        levelProgressBar.setAnimInterval(10);
    }

    private void initView() {
        mBtn1 = findViewById(R.id.mBtn1);
        mBtn2 = findViewById(R.id.mBtn2);
        mBtn3 = findViewById(R.id.mBtn3);
        mBtn4 = findViewById(R.id.mBtn4);
        levelProgressBar = findViewById(R.id.mLevel);

        mBtn1.setOnClickListener(this);
        mBtn2.setOnClickListener(this);
        mBtn3.setOnClickListener(this);
        mBtn4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mBtn1:
                levelProgressBar.setCurrentLevel(1);
//                levelProgressBar.setAnimInterval(10);
                levelProgressBar.setAnimMaxTime(1000);
                break;
            case R.id.mBtn2:
                levelProgressBar.setCurrentLevel(2);
//                levelProgressBar.setAnimInterval(10);
                levelProgressBar.setAnimMaxTime(1000);

                break;
            case R.id.mBtn3:
                levelProgressBar.setCurrentLevel(3);
//                levelProgressBar.setAnimInterval(10);
                levelProgressBar.setAnimMaxTime(1000);
                break;
            case R.id.mBtn4:
                levelProgressBar.setCurrentLevel(4);
//                levelProgressBar.setAnimInterval(10);
                levelProgressBar.setAnimMaxTime(1000);
                break;
        }

    }
}
