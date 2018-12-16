package hcmus.mdsd.itusgallery;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

public class FavoriteActivity extends Fragment{
    View favorite;
    //MyPrefs
    MyPrefs myPrefs;
    public static ArrayList<String> favoriteImages = new ArrayList<>();
    public static FavoriteActivity newInstance() {
        return new FavoriteActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        favorite = inflater.inflate(R.layout.activity_favorite,container, false);
        //Khởi tạo myPrefs
        myPrefs = new MyPrefs(getContext());
        if (null != favoriteImages && !favoriteImages.isEmpty()) {
            GridView favoriteGallery = favorite.findViewById(R.id.favoriteGalleryGridView);
            favoriteGallery.setAdapter(new ImageAdapter(FavoriteActivity.super.getActivity(), favoriteImages));

            //Lựa chọn số cột để hiển thị, load từ myPrefs
            Integer[] columns = myPrefs.getNumberOfColumns();
            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

            if (screenWidth > screenHeight) {
                favoriteGallery.setNumColumns(columns[1]);
            } else {
                favoriteGallery.setNumColumns(columns[0]);
            }

            favoriteGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    if (null != favoriteImages && !favoriteImages.isEmpty()) {
                        Intent i = new Intent(FavoriteActivity.super.getActivity(), FullImageActivity.class);
                        i.putExtra("id", position);
                        i.putExtra("path", favoriteImages.get(position));
                        i.putExtra("allPath", favoriteImages);
                        startActivity(i);
                    }
                }
            });
        }
        return favorite;
    }
}