package com.qiqi8226.qidict;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


public class SetColorAty extends Activity
        implements View.OnClickListener {
    public static final String SELECTED_RESULT = "selected_result";

    private ImageButton mImgBtn1;
    private ImageButton mImgBtn2;
    private ImageButton mImgBtn3;
    private ImageButton mImgBtn4;
    private ImageButton mImgBtn5;
    private ImageButton mImgBtn6;
    private ImageButton mImgBtn7;
    private ImageButton mImgBtn8;
    private ImageButton mImgBtn9;
    private ImageButton mImgBtn10;
    private ImageButton mImgBtn11;
    private ImageButton mImgBtn12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_color);

        mImgBtn1 = (ImageButton) findViewById(R.id.img_btn1);
        mImgBtn2 = (ImageButton) findViewById(R.id.img_btn2);
        mImgBtn3 = (ImageButton) findViewById(R.id.img_btn3);
        mImgBtn4 = (ImageButton) findViewById(R.id.img_btn4);
        mImgBtn5 = (ImageButton) findViewById(R.id.img_btn5);
        mImgBtn6 = (ImageButton) findViewById(R.id.img_btn6);
        mImgBtn7 = (ImageButton) findViewById(R.id.img_btn7);
        mImgBtn8 = (ImageButton) findViewById(R.id.img_btn8);
        mImgBtn9 = (ImageButton) findViewById(R.id.img_btn9);
        mImgBtn10 = (ImageButton) findViewById(R.id.img_btn10);
        mImgBtn11 = (ImageButton) findViewById(R.id.img_btn11);
        mImgBtn12 = (ImageButton) findViewById(R.id.img_btn12);

        mImgBtn1.setOnClickListener(this);
        mImgBtn2.setOnClickListener(this);
        mImgBtn3.setOnClickListener(this);
        mImgBtn4.setOnClickListener(this);
        mImgBtn5.setOnClickListener(this);
        mImgBtn6.setOnClickListener(this);
        mImgBtn7.setOnClickListener(this);
        mImgBtn8.setOnClickListener(this);
        mImgBtn9.setOnClickListener(this);
        mImgBtn10.setOnClickListener(this);
        mImgBtn11.setOnClickListener(this);
        mImgBtn12.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.img_btn1: intent.putExtra(SELECTED_RESULT, 1); break;
            case R.id.img_btn2: intent.putExtra(SELECTED_RESULT, 2); break;
            case R.id.img_btn3: intent.putExtra(SELECTED_RESULT, 3); break;
            case R.id.img_btn4: intent.putExtra(SELECTED_RESULT, 4); break;
            case R.id.img_btn5: intent.putExtra(SELECTED_RESULT, 5); break;
            case R.id.img_btn6: intent.putExtra(SELECTED_RESULT, 6); break;
            case R.id.img_btn7: intent.putExtra(SELECTED_RESULT, 7); break;
            case R.id.img_btn8: intent.putExtra(SELECTED_RESULT, 8); break;
            case R.id.img_btn9: intent.putExtra(SELECTED_RESULT, 9); break;
            case R.id.img_btn10: intent.putExtra(SELECTED_RESULT, 10); break;
            case R.id.img_btn11: intent.putExtra(SELECTED_RESULT, 11); break;
            case R.id.img_btn12: intent.putExtra(SELECTED_RESULT, 12); break;
        }
        setResult(RESULT_OK, intent);
        finish();
    }
}
