package hcmus.mdsd.itusgallery;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    MyPrefs myPrefs;

    //Activity
    private Activity context;

    /**
     * Constructor 1 tham số
     * @param localContext activity
     */
    ImageAdapter(Activity localContext) {
        context = localContext;
        PicturesActivity.images = getAllShownImagesPath(context);

    }
    /**
     * Constructor 2 tham số
     * @param localContext activity
     * @param arrayList danh sách file
     */
    ImageAdapter(Activity localContext, ArrayList<String> arrayList){
        context = localContext;
        PicturesActivity.images = arrayList;

    }
    //Hàm lấy số lượng ảnh
    public int getCount() { return PicturesActivity.images.size(); }
    public Object getItem(int position) { return position; }
    public long getItemId(int position) { return position; }

    public View getView(final int position, View convertView, ViewGroup parent) {

        //ImageView
        ImageView picturesView;
        if (convertView == null) {

            myPrefs = new MyPrefs(context);
            //Đoạn code kiểm tra tình trạng màn hình để hiển thị số cột ảnh
            //Nếu màn hình portrait thì hiển thị 4 cột (mặc định trong layout xml), nếu màn hình landscape hiển thị 6 cột
            final Integer[] columns = myPrefs.getNumberOfColumns();
            final int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            final int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
            int column = columns[1];
            if (screenWidth < screenHeight) {
                column = columns[0];
            }
            //Kích thước ảnh hiển thị
            int sizeOfImage = (screenWidth - (column + 1) * 8) / column;
            //Kích thước ảnh = kích thước mỗi cột của gridView, nhưng ảnh đầu tiên không được tối ưu
            //sizeOfImage = gridView.getColumnWidth();
            //Khởi tạo picturesView
            picturesView = new ImageView(context);
            //Đặt thuộc tính hiển thị FIT_CENTER
            picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            //Đặt tham số param
            picturesView.setLayoutParams(new GridView.LayoutParams(sizeOfImage, sizeOfImage));
        } else {
            picturesView = (ImageView) convertView;
        }
        //Dùng Glide để load ảnh lên gridview
        Glide.with(context).load(PicturesActivity.images.get(position))
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(new RequestOptions().placeholder(null).centerCrop())
                .into(picturesView);
        return picturesView;
    }

    /**
     * Hàm load tất cả các file ảnh trong Media
     * @param activity activity
     * @return mảng file
     */
    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        //Khởi tạo mảng
        ArrayList<String> listOfAllImages = new ArrayList<>();
        //Khởi tạo con trỏ đọc các file trong Media, lấy dữ liệu về DATA, sắp xếp theo ngày chỉnh sửa giảm dần
        Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.MediaColumns.DATA}, null, null, "DATE_MODIFIED DESC");
        //Nếu con trỏ không rỗng
        if (cursor!=null) {
            //Duyệt qua từng file
            while (cursor.moveToNext()) {
                //Thêm file vào mảng
                listOfAllImages.add(cursor.getString(0));
            }
            cursor.close();
        }
        return listOfAllImages;
    }
}