package com.qiqi8226.qidict;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.umeng.analytics.MobclickAgent;


public class MainActivity extends Activity
        implements View.OnClickListener, AsyncSearch.OnQueryComplete {

    public static final String SP = "com.qiqi8226.qidict";
    public static final String SP_BACKGROUND = "background";
    public static final String SP_IS_SIMPLE_MODE = "is_simple_mode";
    public static final String SP_IS_EN_TO_CN = "is_en_to_cn";
    public static final String SP_FIRST_RUN = "first_run";
    private static SharedPreferences mSharedPreferences;

    private RelativeLayout mRelativeLayout;
    private EditText mEtSearchBar;
    private ImageButton mImgBtnSearch;
    private TextView mTvColor;
    private TextView mTvSimpleMode;
    private TextView mTvLanguage;
    private TextView mTvAbout;
    private ProgressBar mProgressBar;
    private TextView mTvShowResult;

    private AsyncSearch task = null;
    private int defaultTextColor;
    private int redTextColor = 0xFF8F343D;
    private boolean isSimpleMode = false;
    private boolean isEnToZh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = this.getSharedPreferences(SP, Context.MODE_PRIVATE);

        mRelativeLayout = (RelativeLayout) findViewById(R.id.main_layout);
        mEtSearchBar = (EditText) findViewById(R.id.et_search_bar);
        mImgBtnSearch = (ImageButton) findViewById(R.id.img_btn_search);
        mTvColor = (TextView) findViewById(R.id.tv_set_color);
        mTvSimpleMode = (TextView) findViewById(R.id.tv_set_simple_mode);
        mTvLanguage = (TextView) findViewById(R.id.tv_set_language);
        mTvAbout = (TextView) findViewById(R.id.tv_about);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mTvShowResult = (TextView) findViewById(R.id.tv_show_result);
        mProgressBar.setIndeterminateDrawable(new CirclesProgress.Builder(this).build());
        mProgressBar.setVisibility(View.GONE);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(config);

        defaultTextColor = mTvAbout.getTextColors().getDefaultColor();
        if ((isSimpleMode = mSharedPreferences.getBoolean(SP_IS_SIMPLE_MODE, false)) == true)
            mTvSimpleMode.setTextColor(redTextColor);
        if ((isEnToZh = mSharedPreferences.getBoolean(SP_IS_EN_TO_CN, true)) == false) {
            mTvLanguage.setText("汉外翻译");
            mTvLanguage.setTextColor(redTextColor);
        }
        setBackgroundColor(mSharedPreferences.getInt(SP_BACKGROUND, 5));

        mEtSearchBar.setOnKeyListener(onKeyListener);
        mImgBtnSearch.setOnClickListener(this);
        mTvColor.setOnClickListener(this);
        mTvSimpleMode.setOnClickListener(this);
        mTvLanguage.setOnClickListener(this);
        mTvAbout.setOnClickListener(this);
        mEtSearchBar.setOnClickListener(this);

        boolean isFirstRun = true;
        if ( (isFirstRun = getSharedPreferences().getBoolean(SP_FIRST_RUN, true)) == true ) {
            startActivity(new Intent(this, GuidePager.class));
            getSharedPreferences().edit().putBoolean(SP_FIRST_RUN, false).commit();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_btn_search:
                clickedImgBtnSearch();
                break;
            case R.id.tv_set_color:
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                startActivityForResult(new Intent(this, SetColorAty.class), 1);
                break;
            case R.id.tv_set_simple_mode:
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                if (isSimpleMode) {
                    isSimpleMode = false;
                    mTvSimpleMode.setTextColor(defaultTextColor);
                } else {
                    isSimpleMode = true;
                    mTvSimpleMode.setTextColor(redTextColor);
                }
                mSharedPreferences.edit().putBoolean(SP_IS_SIMPLE_MODE, isSimpleMode).commit();
                break;
            case R.id.tv_set_language:
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                if (isEnToZh) {
                    isEnToZh = false;
                    mTvLanguage.setText("汉外翻译");
                    mTvLanguage.setTextColor(redTextColor);
                } else {
                    isEnToZh = true;
                    mTvLanguage.setText("英汉翻译");
                    mTvLanguage.setTextColor(defaultTextColor);
                }
                mSharedPreferences.edit().putBoolean(SP_IS_EN_TO_CN, isEnToZh).commit();
                break;
            case R.id.tv_about:
                startActivity(new Intent(this, GuidePager.class));
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        MobclickAgent.onKillProcess(this);
        super.onSaveInstanceState(outState);
    }

    public void clickedImgBtnSearch() {
        MobclickAgent.onEvent(this, "ON_QUERY");
        mEtSearchBar.setSelection(0, mEtSearchBar.getText().toString().length());
        mEtSearchBar.setSelection(0);
        mEtSearchBar.selectAll();
        mImgBtnSearch.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.search_btn_clicked));

        mTvShowResult.setText("");
        if (isNetworkEnable()) {
            if (task != null && task.getStatus() == AsyncTask.Status.RUNNING)
                task.cancel(true);
            mProgressBar.setVisibility(View.VISIBLE);
//            mProgressBar.startAnimation(
//                    AnimationUtils.loadAnimation(this, R.anim.progressbar_fadein));
            task = new AsyncSearch(this, isSimpleMode, isEnToZh);
            task.execute(mEtSearchBar.getText().toString());
        } else {
            forResult(AsyncSearch.RESULT_ERROR, "网络错误！请连接网络！");

        }
    }

    @Override
    public void forResult(int resultCode, String result) {
        mProgressBar.setVisibility(View.GONE);
        mTvShowResult.setText(result);
        mTvShowResult.setAnimation(AnimationUtils.loadAnimation(this, R.anim.list_item_anim));
    }

    private boolean isNetworkEnable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        return false;
    }

    private OnKeyListener onKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                clickedImgBtnSearch();
            }
            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            int tmp = data.getIntExtra(SetColorAty.SELECTED_RESULT, 5);
            mSharedPreferences.edit().putInt(SP_BACKGROUND, tmp).commit();
            setBackgroundColor(tmp);
        }
    }

    private void setBackgroundColor(int bgNum) {
        int[] bgArr = new int[]{0, R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, R.drawable.bg4,
                R.drawable.bg5, R.drawable.bg6, R.drawable.bg7, R.drawable.bg8, R.drawable.bg9,
                R.drawable.bg10, R.drawable.bg11, R.drawable.bg12,};
        String imgUri = "drawable://" + bgArr[bgNum];
        ImageSize imageSize = new ImageSize(mRelativeLayout.getWidth(), mRelativeLayout.getHeight());
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imgUri, imageSize);
        mRelativeLayout.setBackgroundDrawable(new BitmapDrawable(bitmap));
    }

    public static SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }
}
