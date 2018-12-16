package hcmus.mdsd.itusgallery;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TutorialActivity extends AppCompatActivity {
    ViewPager viewPager;
    MyViewPagerAdapter myViewPagerAdapter;
    LinearLayout dotsLayout;
    TextView[] dots;
    int[] layouts = new int[]{
            R.layout.tutorial_slide_1,
            R.layout.tutorial_slide_2,
            R.layout.tutorial_slide_3,
            R.layout.tutorial_slide_4,
            R.layout.tutorial_slide_5,
            R.layout.tutorial_slide_6};;
    Button btnSkip, btnNext;
    TextView txtHelp;
    MyPrefs myPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myPrefs = new MyPrefs(this);
        CheckFirstLaunch();
        setContentView(R.layout.activity_tutorial);
        myPrefs.isFirstLaunch(false);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);
        // adding bottom dots
        addBottomDots(0);
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchMainScreen();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    LaunchMainScreen();
                }
            }
        });
    }
    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];
        int colorsActive = getResources().getColor(R.color.dot_active);
        int colorsInactive = getResources().getColor(R.color.dot_inactive);
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive);
    }
    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }
    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText("GOT IT");
                btnSkip.setVisibility(View.GONE);
                txtHelp = (TextView)findViewById(R.id.txt_help);
                txtHelp.setPaintFlags(txtHelp.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            }
            else {
                // still pages are left
                btnNext.setText("NEXT");
                btnSkip.setVisibility(View.VISIBLE);
            }
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) { }
        @Override
        public void onPageScrollStateChanged(int arg0) { }
    };
    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        public MyViewPagerAdapter() {
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }
        @Override
        public int getCount() {
            return layouts.length;
        }
        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
    private void LaunchMainScreen() {
        startActivity(new Intent(TutorialActivity.this, MainActivity.class));
        finish();
    }
    public void ChangeToHelp(View v){
        startActivity(new Intent(getApplicationContext(),HelpActivity.class));
    }
    public void CheckFirstLaunch(){
        if (!myPrefs.loadIsFirstLaunch()){
            LaunchMainScreen();
        }
    }
}