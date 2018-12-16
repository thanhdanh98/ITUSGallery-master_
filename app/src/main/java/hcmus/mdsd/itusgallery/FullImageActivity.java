package hcmus.mdsd.itusgallery;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.media.ExifInterface;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class FullImageActivity extends AppCompatActivity {
    //Firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mData;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
    //Thuộc tính ảnh được yêu thích hay không
    static boolean favoritedImage = false;
    static boolean lockedImage = false;
    //Toolbar
    Toolbar toolBar;
    //TextView hiển thị ngày chỉnh sửa cuối
    TextView txtDateModified;
    //Vị trí hiện tại của ảnh trong danh sách file
    int position;
    //Đường dẫn của file ảnh hiện tại
    String currentImage;
    //Bottom Navigation View, 4 nút Edit/Crop/Share/Delete
    BottomNavigationView mainNav;
    //View
    View decorView;
    //MyPrefs
    MyPrefs myPrefs;
    int hour, minute;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy HH:mm"); // Tạo format date để lưu Date
    String nameImage;
    MenuItem menuItem; // Nút favorite
    //Tọa độ trước và sau khi chạm màn hình
    private float x1, x2, y1, y2;
    private ViewPager viewPager;
    private FullScreenImageAdapter adapter;
    private Context context;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        context = FullImageActivity.this;
        //Khởi tạo myprefs
        myPrefs = new MyPrefs(this);
        //Màn hình fullscreen
        decorView = getWindow().getDecorView();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Set layout chính
        setContentView(R.layout.activity_full_image);
        //Set ActionBar
        toolBar = findViewById(R.id.nav_actionBar);
        setSupportActionBar(toolBar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Đưa màn hình vào chế dộ Immersive Sticky (ẩn toàn bộ thanh thông báo, thanh điều hướng chỉ hiện lên khi được vuốt lên)
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        //Gán mainNav bằng id của nav_bottom
        mainNav = findViewById(R.id.nav_bottom);
        //Gán txtDateModified bằng id của txtDateModified
        txtDateModified = findViewById(R.id.txtDateModified);
        if (PicturesActivity.hideToolbar == 0) {
            LeaveFullScreenView();
        } else {
            EnterFullScreenView();
        }
        Intent intent = getIntent();
        position = intent.getIntExtra("id", 0);
        currentImage = intent.getStringExtra("path");
        PicturesActivity.images = intent.getStringArrayListExtra("allPath");
        viewPager = findViewById(R.id.pager);
        adapter = new FullScreenImageAdapter(this, PicturesActivity.images);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }

            @Override
            public void onPageSelected(int i) {
                invalidateOptionsMenu();
                //Chuyển qua vị trí mới
                position = i;
                //Cập nhật lại file hiện tại
                currentImage = PicturesActivity.images.get(position);
                //Load thời gian của ảnh hiện tại vào textview
                File file = new File(currentImage);
                txtDateModified.setText(sdf.format(file.lastModified()));
//                if (null != FavoriteActivity.favoriteImages && !FavoriteActivity.favoriteImages.isEmpty()) {
//                    // Nếu ảnh đang chiếu có trong số ảnh được yêu thích thì chuyển tim sang màu đỏ
//                    if (FavoriteActivity.favoriteImages.contains(currentImage))
//                    {
//                        menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_favorite_24_clicked));
//                        favoritedImage = true;
//                    } else{
//                        menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_favorite_24));
//                        favoritedImage = false;
//                    }
//                } else {
//                    menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_favorite_24));
//                    favoritedImage = false;
//                }
            }
        });

        //Navigation bottom onClickListener
        mainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_edit: {
                        Intent editIntent = new Intent(Intent.ACTION_EDIT);
                        editIntent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(),
                                "hcmus.mdsd.itusgallery", new File(currentImage)), "image/*");
                        editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(editIntent, null));
                        return true;
                    }
                    case R.id.nav_crop: {
                        openCrop();
                        return true;
                    }
                    case R.id.nav_share: {
                        startActivity(Intent.createChooser(emailIntent(), "Share image using"));
                        return true;
                    }
                    case R.id.nav_delete: {
                        final File photoFile = new File(currentImage);

                        // Tạo biến builder để tạo dialog để xác nhận có xoá file hay không
                        AlertDialog builder;

                        Calendar c = Calendar.getInstance();
                        if (myPrefs.loadNightModeState() == 0) {
                            builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert).create();
                        } else if (myPrefs.loadNightModeState() == 1) {
                            builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert).create();
                        } else if (myPrefs.loadNightModeState() == 2) {
                            hour = c.get(Calendar.HOUR_OF_DAY);
                            if (6 <= hour && hour <= 17) {
                                builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert).create();
                            } else {
                                builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert).create();
                            }
                        } else {
                            hour = c.get(Calendar.HOUR_OF_DAY);
                            minute = c.get(Calendar.MINUTE);
                            boolean nightmode = CheckTime(hour, minute, myPrefs.loadStartHour(), myPrefs.loadStartMinute(), myPrefs.loadEndHour(), myPrefs.loadEndMinute());
                            if (nightmode) {
                                builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert).create();
                            } else {
                                builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert).create();
                            }
                        }

                        builder.setMessage("Are you sure you want to delete this item ?");
                        builder.setButton(Dialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //---- Dưới đây là bài hướng dẫn xoá ảnh sử dụng ContentResolver trên diễn đàn stackoverflow ----
                                // Nguồn: http://stackoverflow.com/a/20780472#1#L0

                                // Nếu là ảnh đã bị khoá thì phải gọi Broadcast để scan lại thì mới xoá được
                                if (lockedImage) {
                                    photoFile.delete();

                                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(currentImage))));
                                }

                                // Lấy thông tin đường dẫn
                                String selection = MediaStore.Images.Media.DATA + " = ?";
                                String[] selectionArgs = new String[]{photoFile.getAbsolutePath()};

                                ContentResolver contentResolver = getContentResolver();
                                Cursor c = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        new String[]{MediaStore.Images.Media._ID}, selection, selectionArgs, null);
                                if (c != null) {
                                    if (c.moveToFirst()) {
                                        // Tìm thấy ID. Xoá ảnh dựa nhờ content provider
                                        long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                                        Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                                        contentResolver.delete(deleteUri, null, null);
                                    }
                                    c.close();
                                }
                                Toast.makeText(context, "Item has been deleted", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                //PicturesActivity.images.remove(position);

                                // Nếu ảnh được yêu thích thì khi xoá ảnh phải xoá trong danh sách các ảnh được yêu thích luôn
                                if (favoritedImage) {
                                    FavoriteActivity.favoriteImages.remove(currentImage);
                                    SharedPreferences sharedPreferences = PreferenceManager
                                            .getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(FavoriteActivity.favoriteImages);
                                    editor.putString("savedFavoriteImages", json);
                                    editor.apply();
                                }
                                //Số ảnh còn lại trước khi xóa
                                int currentNumberOfPictures = PicturesActivity.images.size();
                                if (currentNumberOfPictures == 1) {
                                    adapter.removeItem(position);
                                    finish();
                                } else if (position == currentNumberOfPictures) {
                                    position--;
                                    adapter.removeItem(position);
                                    currentImage = PicturesActivity.images.get(position);
                                    viewPager.setCurrentItem(position);
                                } else {
                                    adapter.removeItem(position);
                                    currentImage = PicturesActivity.images.get(position);
                                    viewPager.setCurrentItem(position);
                                }
//                                if (null != FavoriteActivity.favoriteImages && !FavoriteActivity.favoriteImages.isEmpty()) {
//                                    // Nếu ảnh đang chiếu có trong số ảnh được yêu thích thì chuyển tim sang màu đỏ
//                                    if (FavoriteActivity.favoriteImages.contains(currentImage)) {
//                                        menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_favorite_24_clicked));
//                                        favoritedImage = true;
//                                    } else {
//                                        menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_favorite_24));
//                                        favoritedImage = false;
//                                    }
//                                } else {
//                                    menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_favorite_24));
//                                    favoritedImage = false;
//                                }

                            }
                        });
                        builder.setButton(Dialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builder.show();
                        return true;
                    }
                }
                return false;
            }
        });

        currentImage = PicturesActivity.images.get(position);
        File file = new File(currentImage);
        txtDateModified.setText(sdf.format(file.lastModified()));
    }

    //Vào chế độ ẩn toàn màn hình
    public void EnterFullScreenView() {
        Objects.requireNonNull(getSupportActionBar()).hide();
        mainNav.setVisibility(View.GONE);
        txtDateModified.setVisibility(View.GONE);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    //Thoát chế độ ẩn toàn màn hình
    public void LeaveFullScreenView() {
        mainNav.setVisibility(View.VISIBLE);
        txtDateModified.setVisibility(View.VISIBLE);
        Objects.requireNonNull(getSupportActionBar()).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        viewPager.setCurrentItem(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; add items to the action bar
        getMenuInflater().inflate(R.menu.image_main, menu);

        // ---------------- CHỈNH ICON FAVORITE -----------------
        favoritedImage = false;
        String current = currentImage;
        menuItem = menu.findItem(R.id.action_favorite);
        // Nếu tồn tại đường dẫn của ảnh trong favoriteImages
        if (null != FavoriteActivity.favoriteImages && !FavoriteActivity.favoriteImages.isEmpty()) {
            // Nếu ảnh đang chiếu có trong số ảnh được yêu thích thì chuyển tim sang màu đỏ
            if (FavoriteActivity.favoriteImages.contains(current)) {
                //MenuItem menuItem = menu.findItem(R.id.action_favorite);
                menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_favorite_24_clicked));
                favoritedImage = true; // Đánh dấu ảnh đang chiếu đã được yêu thích
            } else {
                menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_favorite_24));
                favoritedImage = false;
            }
        } else {
            menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_favorite_24));
            favoritedImage = false;
        }
        // --------------KẾT THÚC CHỈNH ICON FAVORITE ---------


        // ------------------ CHỈNH ICON LOCK -----------------
        Intent intent = getIntent();
        // Lấy thông tin từ activity trước (dùng để kiểm tra có phải từ PrivatePicturesActivity hay không)
        String From = intent.getStringExtra("From");

        if (From == null) {
            // Nếu không đến từ PrivatePicturesActivity thì không cần sửa
            lockedImage = false;
        }
        else // Nếu đến từ PrivatePicturesActivity
        {
            MenuItem ItemLock;
            ItemLock = menu.findItem(R.id.action_lock);

            // Sửa icon lock thành icon unlock
            ItemLock.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_lock_open_24));

            lockedImage = true;
        }
        // ---------------- KẾT THÚC CHỈNH ICON LOCK --------------

        return true;
    }

    // return a SHARED intent to deliver an email
    private Intent emailIntent() {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        final File photoFile = new File(currentImage);
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, "hcmus.mdsd.itusgallery", photoFile));
        return shareIntent;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // user clicked a menu-item from ActionBar

        int id = item.getItemId();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if (id == R.id.action_lock) {
            // Note: cần xét lần đầu nhấn lock ảnh để hướng dẫn sử dụng
            // Note: cần kiểm tra xem người dùng có muốn đặt pass hay k pass
            // Note: chức năng thêm ảnh từ Fragment privatePictures
            // Note: lock ảnh nhớ chặn favorite

            if (!lockedImage) // Nếu ảnh chưa được lock
            {
                // Nếu chưa có password sẽ được yêu cầu set password
                if (myPrefs.getPassword().equals("")) {
                    Toast.makeText(context, "You have to set password before locking an image", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(FullImageActivity.this, SetPasswordActivity.class);
                    intent.putExtra("From", "FullImageActivity"); // Lưu vị trí activity đến

                    // Lưu những thông tin khác để có thể trở về FullImageActivity
                    intent.putExtra("id", position);
                    intent.putExtra("path", currentImage);
                    intent.putExtra("allPath", PicturesActivity.images);
                    startActivity(intent);
                    finish();
                } else {
                    LockPicture();
                    Toast.makeText(context, "Successfully lock the image. The image has been moved to Private Pictures", Toast.LENGTH_LONG).show();
                }
            }
            else // Nếu ảnh đã được lock
            {
                // Note: cần hỏi người dùng có chắc là muốn unlock không (dialog)

                UnlockPicture();
                Toast.makeText(context, "Successfully unlock the image", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.action_favorite) {
            if (lockedImage) // Nếu ảnh đã bị lock thì không cho người dùng thích
            {
                Toast.makeText(context, "You can't love the locked image. It won't be protected by password.", Toast.LENGTH_LONG).show();

                return true; // rời khỏi hàm
            }

            if (favoritedImage) {
                MenuView.ItemView favorite_button;
                favorite_button = findViewById(R.id.action_favorite);
                favorite_button.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_favorite_24));
                FavoriteActivity.favoriteImages.remove(currentImage);
                favoritedImage = false;
            } else // Nếu ảnh chưa được yêu thích thì khi bấm vào nút Favorite, đổi tim thành màu đỏ và thêm ảnh vào danh sách ảnh được yêu thích
            {
                MenuView.ItemView favorite_button;
                favorite_button = findViewById(R.id.action_favorite);
                favorite_button.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_favorite_24_clicked));
                if (null != FavoriteActivity.favoriteImages && !FavoriteActivity.favoriteImages.isEmpty()) {
                    FavoriteActivity.favoriteImages.add(currentImage);
                } else {
                    FavoriteActivity.favoriteImages = new ArrayList<>();
                    FavoriteActivity.favoriteImages.add(currentImage);
                }
                favoritedImage = true;
            }
            // Cập nhật lại ảnh để lưu vào SharedPreferences
            // (Lưu vào SharedPreferences để có thể lấy được thông tin của những ảnh đã được yêu thích khi thoát ứng dụng và bật lại)
            // Nguồn: https://stackoverflow.com/questions/14981233/android-arraylist-of-custom-objects-save-to-sharedpreferences-serializable/40237149#40237149
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(this.getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(FavoriteActivity.favoriteImages);
            editor.putString("savedFavoriteImages", json);
            //editor.commit();
            editor.apply();

            return true;
        } else if (id == R.id.action_upload) {
            ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            assert conMgr != null;
            NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
            if (MainActivity._name_cloud.equals("")) {
                Toast.makeText(context, "Login to Cloud Storage to upload images", Toast.LENGTH_SHORT).show();
            } else {
                if (activeNetwork != null && activeNetwork.isConnected()) {

                    storageRef = storage.getReference(MainActivity._name_cloud);
                    mData = database.getReference(MainActivity._name_cloud);

                    Intent i = getIntent();
                    String filePath = Objects.requireNonNull(i.getExtras()).getString("path");
                    assert filePath != null;
                    final String imgName = new File(filePath).getName();

                    StorageReference mountainsRef = storageRef.child(imgName);
//
                    Bitmap bitmap = BitmapFactory.decodeFile(PicturesActivity.images.get(position));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = mountainsRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(context, "Lưu ảnh không thành công", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(context, "Lưu ảnh thành công", Toast.LENGTH_SHORT).show();

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            //while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();
                            CloudImage con = new CloudImage(imgName, downloadUrl.toString());
                            mData.push().setValue(con, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        Toast.makeText(context, "Lưu dữ liệu thành công", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Lưu dữ liệu không thành công", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(context, "Kiểm tra kết nối internet của bạn", Toast.LENGTH_SHORT).show();
                }
            }

            return true;
        } else if (id == R.id.action_slideshow) {
            // perform SLIDESHOW operations...
            Intent newIntentForSlideShowActivity = new Intent(context, SlideShowActivity.class);
            newIntentForSlideShowActivity.putExtra("id", position); // Lấy position id và truyền cho SlideShowActivity
            startActivity(newIntentForSlideShowActivity);

            return true;
        } else if (id == R.id.action_rotate) {
            //imageView.setRotation(imageView.getRotation() + 90);
            Toast.makeText(context, "This function is down", Toast.LENGTH_SHORT).show();

            return true;
        } else if (id == R.id.action_setAs) {
            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(),
                    "hcmus.mdsd.itusgallery", new File(currentImage)), "image/*");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra("mimeType", "image/*");
            this.startActivity(Intent.createChooser(intent, "Set as:"));

            return true;
        } else if (id == R.id.action_print) {
            Toast.makeText(context, "This feature is under development!", Toast.LENGTH_SHORT).show();

            return true;
        } else if (id == R.id.action_details) {
            String returnUri = currentImage;
            File file = new File(returnUri);

            final DecimalFormat format = new DecimalFormat("#.##"); // Tạo format cho size
            final double length = file.length();    // Lấy độ dài file
            String sLength;

            if (length > 1024 * 1024) {
                sLength = format.format(length / (1024 * 1024)) + " MB";
            } else {
                if (length > 1024) {
                    sLength = format.format(length / 1024) + " KB";
                } else {
                    sLength = format.format(length) + " B";
                }
            }

            try {
                ExifInterface exif = new ExifInterface(returnUri);
                String Details = ShowExif(exif);    // Lấy thông tin của ảnh

                Details = "Date: " + sdf.format(file.lastModified()) +
                        "\n\nSize: " + sLength +
                        "\n\nFile path: " + returnUri +
                        Details;

                // -----  Tạo dialog để xuất ra detail -----

                TextView title = new TextView(getApplicationContext());
                title.setPadding(46, 40, 0, 0);
                title.setText("Details");
                title.setTextSize(23.0f);
                title.setTypeface(null, Typeface.BOLD);
                AlertDialog dialog;

                Calendar c = Calendar.getInstance();
                if (myPrefs.loadNightModeState() == 0) {
                    title.setTextColor(Color.BLACK);
                    dialog = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert).create();
                } else if (myPrefs.loadNightModeState() == 1) {
                    title.setTextColor(Color.WHITE);
                    dialog = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert).create();
                } else if (myPrefs.loadNightModeState() == 2) {
                    hour = c.get(Calendar.HOUR_OF_DAY);
                    if (6 <= hour && hour <= 17) {
                        title.setTextColor(Color.BLACK);
                        dialog = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert).create();
                    } else {
                        title.setTextColor(Color.WHITE);
                        dialog = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert).create();
                    }
                } else {
                    hour = c.get(Calendar.HOUR_OF_DAY);
                    minute = c.get(Calendar.MINUTE);
                    boolean nightmode = CheckTime(hour, minute, myPrefs.loadStartHour(), myPrefs.loadStartMinute(), myPrefs.loadEndHour(), myPrefs.loadEndMinute());
                    if (nightmode) {
                        title.setTextColor(Color.WHITE);
                        dialog = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert).create();
                    } else {
                        title.setTextColor(Color.BLACK);
                        dialog = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert).create();
                    }
                }

                dialog.setCustomTitle(title);
                dialog.setMessage(Details);
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog.show();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    @SuppressLint("DefaultLocale")
    private String ShowExif(ExifInterface exif) {
        String myAttribute = "";

        if (exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0) == 0) {
            return myAttribute;
        } else {
            myAttribute += "\n\nResolution: " + exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH) +
                    "x" + exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);

            if (exif.getAttribute(ExifInterface.TAG_MODEL) == null) {
                return myAttribute;
            }
        }

        // Lấy aperture
        final DecimalFormat apertureFormat = new DecimalFormat("#.#"); // Tạo format cho aperture
        String aperture = exif.getAttribute(ExifInterface.TAG_F_NUMBER);
        if (aperture != null) {
            Double aperture_double = Double.parseDouble(aperture);
            apertureFormat.format(aperture_double);
            myAttribute += "\n\nAperture: f/" + aperture_double + "\n\n";
        } else {
            myAttribute += "\n\nAperture: unknown\n\n";
        }

        // Lấy exposure time
        String ExposureTime = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
        Double ExposureTime_double = Double.parseDouble(ExposureTime);
        Double Denominator = 1 / ExposureTime_double;

        ExposureTime = 1 + "/" + String.format("%.0f", Denominator);

        myAttribute += "Exposure Time: " + ExposureTime + "s\n\n";

        if (exif.getAttributeInt(ExifInterface.TAG_FLASH, 0) == 0) {
            myAttribute += "Flash: Off\n\n";
        } else {
            myAttribute += "Flash: On\n\n";
        }
        myAttribute += "Focal Length: " + exif.getAttributeDouble(ExifInterface.TAG_FOCAL_LENGTH, 0) + "mm\n\n";
        myAttribute += "ISO Value: " + exif.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS) + "\n\n";
        myAttribute += "Model: " + exif.getAttribute(ExifInterface.TAG_MODEL);

        return myAttribute;
    }

    ///crop
    public void openCrop() {
        String filePath = currentImage;
        Uri photoURI = Uri.fromFile(new File(filePath));
        nameImage = new File(filePath).getName();
        if (photoURI != null) {
            CropImage.activity(photoURI)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                Uri resultUri = result.getUri();
                Bitmap b = null;
                try {
                    b = MediaStore.Images.Media.getBitmap(getContentResolver(),resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                insertCroppedImageToMedia(getContentResolver(),b);
                Toast.makeText(context, "Image cropped completely", Toast.LENGTH_SHORT).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Thêm file bitmap sau khi cắt vào MediaStore
     * @param contentResolver Content Resolver
     * @param bitmap File bitmap sau khi crop
     */
    public void insertCroppedImageToMedia(ContentResolver contentResolver, Bitmap bitmap) {
        //Thêm file vào Media Store
        String photoUriStr = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "" , "");
        //Lấy URI của file vừa thêm vào
        Uri photoUri = Uri.parse(photoUriStr);
        //Lấy thời gian tại hiện tại
        long now = System.currentTimeMillis() / 1000;
        //Tạo ContentValues lưu các thông tin về thời gian
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_ADDED, now);
        values.put(MediaStore.Images.Media.DATE_MODIFIED, now);
        values.put(MediaStore.Images.Media.DATE_TAKEN, now);
        //Update nội dung của ContentValues
        contentResolver.update(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values,
                MediaStore.Images.Media._ID + "=?", new String [] { ContentUris.parseId(photoUri) + "" });
        //Scan lại file
        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, photoUri);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(scanFileIntent);
        adapter.callWhenDataChanged();
    }

    public boolean CheckTime(int curHour, int curMinute, int hourStart, int minuteStart, int hourEnd, int minuteEnd) {
        boolean nightmode = true;
        if (hourStart < hourEnd) {
            if (hourStart <= curHour && curHour <= hourEnd) {
                if (hourStart == curHour) {
                    if (minuteStart > curMinute) {
                        nightmode = false;
                    }
                }
                if (hourEnd == curHour) {
                    if (curMinute > minuteEnd) {
                        nightmode = false;
                    }
                }
            } else {
                nightmode = false;
            }
        } else if (hourStart == hourEnd) {
            if (hourStart == curHour) {
                if (minuteStart < minuteEnd) {
                    if (minuteStart > curMinute || curMinute > minuteEnd) {
                        nightmode = false;
                    }
                } else if (minuteStart > minuteEnd) {
                    if (minuteEnd <= curMinute && curMinute <= minuteStart) {
                        nightmode = false;
                    }
                }
            } else {
                nightmode = false;
            }
        } else {
            if (hourEnd >= curHour || curHour >= hourStart) {
                if (hourStart == curHour) {
                    if (minuteStart > curMinute) {
                        nightmode = false;
                    }
                }
                if (hourEnd == curHour) {
                    if (curMinute > minuteEnd) {
                        nightmode = false;
                    }
                }
            } else {
                nightmode = false;
            }
        }
        return nightmode;
    }

    private void MoveFile(File file, File dir) throws IOException {
        File newFile = new File(dir, file.getName());
        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            outputChannel = new FileOutputStream(newFile).getChannel();
            inputChannel = new FileInputStream(file).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
            outputChannel.close();

            // -------------- XOÁ ẢNH -----------------
            final File photoFile = new File(currentImage);

            // Nếu là ảnh đã bị khoá thì phải gọi Broadcast để scan lại thì mới xoá được
            if (lockedImage) {
                photoFile.delete();

                // cập nhật lại MediaStore
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(currentImage))));
            }
            else {
                String selection = MediaStore.Images.Media.DATA + " = ?";
                String[] selectionArgs = new String[]{photoFile.getAbsolutePath()};

                ContentResolver contentResolver = getContentResolver();
                Cursor c = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Media._ID}, selection, selectionArgs, null);
                if (c != null) {
                    if (c.moveToFirst()) {
                        // Tìm thấy ID. Xoá ảnh dựa nhờ content provider
                        long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                        Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        contentResolver.delete(deleteUri, null, null);
                    }
                    c.close();
                }
            }

            // Nếu ảnh được yêu thích thì khi xoá ảnh phải xoá trong danh sách các ảnh được yêu thích luôn
            if (favoritedImage) {
                FavoriteActivity.favoriteImages.remove(currentImage);
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(FavoriteActivity.favoriteImages);
                editor.putString("savedFavoriteImages", json);
                editor.apply();
            }

            //Số ảnh còn lại trước khi xóa
            int currentNumberOfPictures = PicturesActivity.images.size();
            if (currentNumberOfPictures == 1) {
                adapter.removeItem(position);
                finish();
            } else if (position == currentNumberOfPictures) {
                position--;
                adapter.removeItem(position);
                currentImage = PicturesActivity.images.get(position);
                viewPager.setCurrentItem(position);
            } else {
                adapter.removeItem(position);
                currentImage = PicturesActivity.images.get(position);
                viewPager.setCurrentItem(position);
            }
            // ------------------- KẾT THÚC XOÁ ẢNH -------------------

        } finally {
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
        }
    }

    private void LockPicture()
    {
        File lockPicture = new File(currentImage);
        File folder = new File(Environment.getExternalStorageDirectory(), "/.ITUSGallery");
        File nomedia = new File(folder.getAbsolutePath(),"/.nomedia");

        //boolean success = true;

        // Nếu folder k tồn tại thì tạo folder, nếu có rồi thì k cần tạo nữa
        if (!folder.exists()) {
            folder.mkdirs();
            // success = folder.mkdirs();
        }

        // Nếu .nomedia File k tồn tại thì tạo mới, nếu có rồi thì k cần tạo nữa
        try {
            nomedia.createNewFile();
        } catch (IOException ex) {
            // do something
        }

        // ----------- QUAN TRỌNG VỀ SAU (LƯU LẠI) -----------
//           if (success) {
//              // Nếu tạo folder ITUSGallery lần đầu
//           }
//           else {
//                //Toast.makeText(context, "Không", Toast.LENGTH_SHORT).show();
//           }

        try {
            // Lưu tên và đường dẫn cũ vào file txt
            File txt = new File(folder.getAbsolutePath(), "/don't delete this file or this folder.txt");

            String Buffer = "";

            // Nếu file đã tồn tại thì đọc nội dung file cũ
            if (txt.exists()) {
                BufferedReader myReader = new BufferedReader(new FileReader(txt));

                String line;

                while ((line = myReader.readLine()) != null) {
                    Buffer += line + '\n';
                }

                myReader.close();
            }

            // Tạo chuỗi dữ liệu để xuất vào file .txt
            OutputStreamWriter myOutWriter = new OutputStreamWriter(new FileOutputStream(txt));
            myOutWriter.append(Buffer); // Ghi nội dung cũ vào chuỗi dữ liệu dùng để xuất
            // Lưu tên vào file txt để dễ kiểm tra
            myOutWriter.append(lockPicture.getAbsolutePath()); // Lưu đường dẫn cũ
            myOutWriter.append('\n');
            myOutWriter.close();

            // Di chuyển file về thư mục .ITUSGallery
            MoveFile(lockPicture, folder);

        } catch(IOException ex) {
            Toast.makeText(context, "Your storage doesn't have enough space, clear your storage to continue!", Toast.LENGTH_LONG).show();
        }
    }

    private void UnlockPicture()
    {
        File unlockPicture = new File(currentImage);
        File folder = new File(Environment.getExternalStorageDirectory(), "/.ITUSGallery");

        try
        {
            File txt = new File(folder.getAbsolutePath(), "/don't delete this file or this folder.txt");

            String data = "";
            String Buffer = "";
            String name = "";

            // Nếu file có tồn tại thì đọc nội dung file
            if (txt.exists()) {
                BufferedReader myReader = new BufferedReader(new FileReader(txt));

                String line;

                while ((line = myReader.readLine()) != null) {
                    if (line.contains(unlockPicture.getName())) // Nếu kiếm được 1 dòng chứa tên của ảnh cần unlock
                    {
                        // Lấy đường dẫn cũ cắt đi tên để tạo ra đường dẫn thư mục cũ
                        data = line.substring(0, line.indexOf(unlockPicture.getName()));
                        name = unlockPicture.getName();
                    }
                    else
                    {
                        // Nếu không kiếm được tên của ảnh của unlock thì vẫn cập nhật Buffer như thường
                        // -> nếu kiếm được thì không thêm vào buffer -> sẽ xoá được đường dẫn cũ của ảnh khỏi Buffer
                        Buffer += line + '\n';
                    }
                }
                myReader.close();

                // Cập nhật lại file .txt sau khi xoá bớt dữ liệu
                OutputStreamWriter myOutWriter = new OutputStreamWriter(new FileOutputStream(txt));
                myOutWriter.append(Buffer);
                myOutWriter.close();

                if (data != "") // nếu data khác rỗng thì thực thi
                {
                    File StoredDirectory = new File(data); // Đường dẫn cũ của ảnh được lưu trong file .txt

                    // Di chuyển file về thư mục ban đầu chứa nó
                    MoveFile(unlockPicture, StoredDirectory);

                    // Sau khi di chuyển phải scan lại để album ảnh thấy
                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(new File(StoredDirectory.getAbsolutePath(), "/" + name))));
                }
                else // trường hợp data rỗng (có thể do lúc lock ảnh gặp lỗi)
                {
                    Toast.makeText(context, "Unable to unlock this image because of unknown errors!!!", Toast.LENGTH_LONG).show();
                }
            }
            else // Nếu file .txt k tồn tại thì báo lỗi
            {
                Toast.makeText(context, "Unable to unlock this image because of missing files!!!", Toast.LENGTH_LONG).show();
            }
        } catch (IOException ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
            //Toast.makeText(context, "Your storage doesn't have enough space, clear your storage to continue!", Toast.LENGTH_LONG).show();
        }
    }
}