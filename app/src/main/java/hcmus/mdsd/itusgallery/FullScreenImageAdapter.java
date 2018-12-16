package hcmus.mdsd.itusgallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

@SuppressWarnings("ALL")
public class FullScreenImageAdapter extends PagerAdapter {

    private FullImageActivity context;
    private ArrayList<String> _imagePaths;
    private LayoutInflater inflater;
    public PhotoView imgDisplay;

    final int position;

    // constructor
    public FullScreenImageAdapter(FullImageActivity activity,
                                  ArrayList<String> imagePaths) {
        this.context = activity;
        this._imagePaths = imagePaths;
        Intent i = context.getIntent();
        position = i.getIntExtra("id", 0);
    }

    @Override
    public int getCount() {
        return this._imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.activity_fullscreen_image, container,
                false);

        imgDisplay = (PhotoView) viewLayout.findViewById(R.id.fullImageView);


        Glide.with(context).load(_imagePaths.get(position))
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(new RequestOptions().placeholder(null).fitCenter())
                .into(imgDisplay);

        PhotoViewAttacher mAttacher = new PhotoViewAttacher(imgDisplay);
        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                PicturesActivity.hideToolbar = (PicturesActivity.hideToolbar + 1) % 2;
                if (PicturesActivity.hideToolbar == 1) {
                    context.EnterFullScreenView();
                } else {
                    context.LeaveFullScreenView();
                }
            }
        });



        ((ViewPager) container).addView(viewLayout);
        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    public void removeItem(int index){
        _imagePaths.remove(index);
        notifyDataSetChanged();
    }

    public void callWhenDataChanged(){
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        if (_imagePaths.contains(object)) {
            return _imagePaths.indexOf(object);
        } else {
            return POSITION_NONE;
        }
    }
}