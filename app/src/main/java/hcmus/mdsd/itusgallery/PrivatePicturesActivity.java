package hcmus.mdsd.itusgallery;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class PrivatePicturesActivity extends Fragment {
    View private_pictures;
    MyPrefs myPrefs;
    private static ArrayList<String> privatePictures = new ArrayList<>();

    public static PrivatePicturesActivity newInstance() {
        return new PrivatePicturesActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        private_pictures = (View) inflater.inflate(R.layout.activity_private_pictures, container, false);
        FloatingActionButton floatingActionButton = private_pictures.findViewById(R.id.floating_action_button);

        myPrefs = new MyPrefs(getContext());

        File folder = new File(Environment.getExternalStorageDirectory() + "/.ITUSGallery");

        // Nếu folder có tồn tại
        if (folder.exists()) {
            String[] fileName;

            fileName = folder.list();
            privatePictures.clear();

            for (int i = 0; i < fileName.length; i++) {
                if (fileName[i].equals(".nomedia") || fileName[i].equals("don't delete this file or this folder.txt"))
                {
                    // Nếu tên file là .nomedia hoặc file .txt thì k thêm vào privatePictures
                }
                else
                {
                    privatePictures.add(folder.getAbsolutePath() + "/" + fileName[i]);
                }
            }
        }

        if (null != privatePictures && !privatePictures.isEmpty()) {
            GridView privatePic = private_pictures.findViewById(R.id.privatePicturesGridView);
            privatePic.setAdapter(new ImageAdapter(PrivatePicturesActivity.super.getActivity(), privatePictures));

            //Lựa chọn số cột để hiển thị, load từ myPrefs
            Integer[] columns = myPrefs.getNumberOfColumns();
            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

            if (screenWidth > screenHeight) {
                privatePic.setNumColumns(columns[1]);
            } else {
                privatePic.setNumColumns(columns[0]);
            }

            privatePic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    if (null != privatePictures && !privatePictures.isEmpty()) {
                        Intent i = new Intent(PrivatePicturesActivity.super.getActivity(), FullImageActivity.class);
                        i.putExtra("From", "PrivatePicturesActivity");
                        i.putExtra("id", position);
                        i.putExtra("path", privatePictures.get(position));
                        i.putExtra("allPath", privatePictures);
                        startActivity(i);
                    }
                }
            });
        }

        // Nhấn vào để thêm ảnh vào privatePictures
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(PrivatePicturesActivity.super.getActivity(), MainActivity.class);
//                intent.putExtra("From", "PrivatePicturesActivity");
//                startActivity(intent);
                Toast.makeText(getActivity(), "Add image", Toast.LENGTH_SHORT).show();
            }
        });

        return private_pictures;
    }
}