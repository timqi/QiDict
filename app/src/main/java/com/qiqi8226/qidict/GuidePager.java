package com.qiqi8226.qidict;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiqi on 2014/12/27.
 */
public class GuidePager extends Activity implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private List<View> mViews;
    private Button mButton;
    private View mView;
    private ImageView[] mImageDots;
    private int[] mImageDotIds = new int[]{R.id.img_nav1, R.id.img_nav2, R.id.img_nav3, R.id.img_nav4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_pager);
        initViews();
        initDots();
        mImageDots[0].setImageResource(R.drawable.login_point_selected);
        mImageDots[1].setImageResource(R.drawable.login_point);
        mImageDots[2].setImageResource(R.drawable.login_point);
        mImageDots[3].setImageResource(R.drawable.login_point);
    }

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);
        mViews = new ArrayList<View>();
        ImageView mImageView;

        mImageView = (ImageView) inflater.inflate(R.layout.guide_item, null);
        ImageSize imageSize = new ImageSize(mImageView.getWidth(), mImageView.getHeight());
        String imgUri = "drawable://" + R.drawable.pager1;
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imgUri, imageSize);
        mImageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
        mViews.add(mImageView);

        mImageView = (ImageView) inflater.inflate(R.layout.guide_item, null);
        imgUri = "drawable://" + R.drawable.pager2;
        bitmap = ImageLoader.getInstance().loadImageSync(imgUri, imageSize);
        mImageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
        mViews.add(mImageView);

        mImageView = (ImageView) inflater.inflate(R.layout.guide_item, null);
        imgUri = "drawable://" + R.drawable.pager3;
        bitmap = ImageLoader.getInstance().loadImageSync(imgUri, imageSize);
        mImageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
        mViews.add(mImageView);

        mView = inflater.inflate(R.layout.guide_item_button, null);
        mImageView = (ImageView) mView.findViewById(R.id.img_guide_item);
        imgUri = "drawable://" + R.drawable.pager4;
        bitmap = ImageLoader.getInstance().loadImageSync(imgUri, imageSize);
        mImageView.setBackgroundDrawable(new BitmapDrawable(bitmap));

        mButton = (Button) mView.findViewById(R.id.img_btn_guide);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mViews.add(mView);

        mViewPagerAdapter = new ViewPagerAdapter(mViews, this);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOnPageChangeListener(this);
    }

    private void initDots() {
        mImageDots = new ImageView[mViews.size()];
        for (int i = 0; i < mViews.size(); i++) {
            mImageDots[i] = (ImageView) findViewById(mImageDotIds[i]);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < mImageDots.length; i++) {
            if (position == i)
                mImageDots[i].setImageResource(R.drawable.login_point_selected);
            else
                mImageDots[i].setImageResource(R.drawable.login_point);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class ViewPagerAdapter extends PagerAdapter {
        private List<View> mViews;
        private Context mContext;

        public ViewPagerAdapter(List<View> views, Context context) {
            this.mViews = views;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return (view == o);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(mViews.get(position));
            return mViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(mViews.get(position));
        }
    }
}
