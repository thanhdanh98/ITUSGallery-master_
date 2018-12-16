package hcmus.mdsd.itusgallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class SlideShowActivity extends AppCompatActivity {
    Intent intentFromFullImageActivity;
    int position;
    ArrayList<String> images;
    String returnUri;
    ViewFlipper flipper;
    View decorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        decorView = getWindow().getDecorView();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_slideshow);
        flipper = findViewById(R.id.flipperView);

        intentFromFullImageActivity = getIntent(); // Lấy intent
        position = Objects.requireNonNull(intentFromFullImageActivity.getExtras()).getInt("id");
        images = PicturesActivity.images;

        // Lấy thông tin ảnh lưu vào flipper từ vị trí ảnh nhấn slideshow đến ảnh cuối cùng
        for (int i = position; i < PicturesActivity.images.size(); i++)
        {
            returnUri = images.get(i);
            flipperImage(returnUri);
        }

        flipper.getInAnimation().setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {}
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationEnd(Animation animation) {
                int displayedChild = flipper.getDisplayedChild(); // Đếm số lượng ảnh đã show
                int childCount = flipper.getChildCount();   // Đếm số lượng ảnh tổng cộng

                // Nếu đang show ảnh cuối thì dừng không show nữa
                if (displayedChild == childCount - 1) {
                    flipper.stopFlipping();
                    finish();
                }
            }
        });
    }


    public void flipperImage(String filePath){
        ImageView ImageFromUri  = new ImageView(this);

        Uri path = Uri.fromFile(new File(filePath));

        // Nhờ Glide load bitmap giùm
        Glide.with(getApplicationContext())
                .load(path)
                .into(ImageFromUri);

        flipper.addView(ImageFromUri);
        flipper.setFlipInterval(3000);
        flipper.setAutoStart(true);
        flipper.setInAnimation(this , android.R.anim.slide_in_left);
        flipper.setOutAnimation(this , android.R.anim.slide_out_right);
    }
}