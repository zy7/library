package com.zy.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zy.library.widget.CountdownTextView;
import com.zy.library.widget.TitleBar;

public class MainActivity extends AppCompatActivity {

    private TitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitleBar = findViewById(R.id.title);
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "点击", Toast.LENGTH_SHORT).show();
            }
        });
        mTitleBar.addAction(new TitleBar.ImageAction(R.mipmap.ic_back) {
            @Override
            public void performAction(View view) {
                Toast.makeText(MainActivity.this, "图片", Toast.LENGTH_SHORT).show();
            }
        });
        mTitleBar.addAction(new TitleBar.TextAction("文字") {
            @Override
            public void performAction(View view) {
                Toast.makeText(MainActivity.this, "文字", Toast.LENGTH_SHORT).show();
            }
        });


        final CountdownTextView tv = findViewById(R.id.textView);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv.start();
            }
        });
        tv.setOnCountDownListener(new CountdownTextView.OnCountDownListener() {

            @Override
            public void onCountDown(CountdownTextView view, int leftCountDown) {
                view.setText("重新发送" + leftCountDown + " s");
            }

            @Override
            public void onCountFinish(CountdownTextView view) {

            }
        });
    }

    public void onDo(View v) {
        mTitleBar.removeActionAt(0);
    }
}
