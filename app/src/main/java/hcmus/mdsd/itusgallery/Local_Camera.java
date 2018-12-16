package hcmus.mdsd.itusgallery;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Local_Camera extends AppCompatActivity {
    int REQUEST_IMAGE_CAPTURE = 123;
    ImageView imageView;
    String photoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local__camera);
        imageView=findViewById(R.id.imgPreView);
        //tao intent su dung cho chup anh
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null){
            try {
                File photoFile=CreateImageFile();
                if(photoFile!=null){
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public File CreateImageFile() throws IOException {
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName="JPEG_"+timeStamp+"_";
        File storageDir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image=File.createTempFile(imageFileName,".jpg",storageDir);
        photoPath=image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CAPTURE){
            setPic();
        }
    }

    private void setPic(){
        int targetW=imageView.getWidth();
        int targetH=imageView.getHeight();
        BitmapFactory.Options options=new BitmapFactory.Options();
        BitmapFactory.decodeFile(photoPath, options);
        int photoW=options.outWidth;
        int photoH=options.outHeight;
        int scaleFactor=Math.min(photoH/targetH,photoW/targetW);
        options.inSampleSize=scaleFactor;
        Bitmap bitmap=BitmapFactory.decodeFile(photoPath,options);
        imageView.setImageBitmap(bitmap);
    }
}

