package com.kuanquan.ijk_test;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_one).setOnClickListener(this);
        findViewById(R.id.btn_two).setOnClickListener(this);
        findViewById(R.id.btn_three).setOnClickListener(this);
        findViewById(R.id.btn_four).setOnClickListener(this);
        findViewById(R.id.btn_five).setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_one:  // 占据三分之一屏幕播放器
                startActivity(OneThirdActivity.class);
                break;
            case R.id.btn_two:  // 竖屏播放器
                startActivity(PlayerActivity.class);
                break;
            case R.id.btn_three: // 竖屏直播播放器
                startActivity(PlayerLiveActivity.class);
                break;
            case R.id.btn_four:   // ijkplayer原生的播放器
                startActivity(VerticalScreenPlayerActivity.class);
                break;
            case R.id.btn_five:   // 完美播放例子
                startActivity(PerfectActivity.class);
                break;
        }
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(MainActivity.this, cls);
        startActivity(intent);
    }
}
