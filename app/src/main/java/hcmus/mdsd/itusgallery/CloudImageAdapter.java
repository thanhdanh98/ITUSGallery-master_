package hcmus.mdsd.itusgallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class CloudImageAdapter extends BaseAdapter {
        Context context;
        int layout;
        private List<CloudImage> cloudImageList;

    CloudImageAdapter(Context context, int layout, List<CloudImage> cloudImageList) {
        this.context = context;
        this.layout = layout;
        this.cloudImageList = cloudImageList;
    }

    @Override
    public int getCount() {
        return cloudImageList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView img;
        TextView txt;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowview=convertView;
        ViewHolder holder= new ViewHolder();
        if(rowview==null){
            assert inflater != null;
            rowview=inflater.inflate(layout,null);
            holder.txt= rowview.findViewById(R.id.text_view_name);
            holder.img= rowview.findViewById(R.id.image_view_upload);
            rowview.setTag(holder);
        }
        else{
            holder= (ViewHolder) rowview.getTag();
        }

        holder.txt.setText(cloudImageList.get(position).NameImage);
//        Picasso.with(context)
//                .load(cloudImageList.get(position).getUrl())
//                .into(holder.img);
        Glide.with(context).load(cloudImageList.get(position).getUrl())
                .apply(new RequestOptions()
                        .placeholder(null).centerCrop())
                .into(holder.img);
        return  rowview;
    }
}
