package group22.travelstories;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by hayleykwan on 17/11/2016.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> photos;
    private static int mImageWidth, mImageHeight;


    public ImageAdapter(Context c, ArrayList<String> photos, int imageWidth, int imageHeight) {
        System.out.println("Image Adapter Constructor called so Photos should be initialized");
        mContext = c;
        this.photos = photos;
        mImageHeight = imageHeight;
        mImageWidth = imageWidth;
    }

        @Override
    public int getItemCount() {
        return photos.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        SimpleDraweeView simpleDraweeView = new SimpleDraweeView(viewGroup.getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mImageWidth, mImageHeight);
        simpleDraweeView.setLayoutParams(params);
        return new ViewHolder(simpleDraweeView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Uri uri = Uri.fromFile(new File(photos.get(position)));


        holder.getSimpleDraweeView().setImageURI(uri);

//        SimpleDraweeView test = (SimpleDraweeView) findViewById(R.id.test);
//        Uri uri = Uri.parse("http://image.slidesharecdn.com/androiddeeplinking-141118113917-conversion-gate02/95/android-deep-linking-19-638.jpg?cb=1416310811");
//        test.setImageURI(uri);

//        Bitmap b = BitmapFactory.decodeFile(photos.get(position).path);
//        Bitmap scaledB = Bitmap.createScaledBitmap(b,b.getWidth()/10, b.getHeight()/10, true);
//        b.recycle();
//        holder.imageView.setImageBitmap(scaledB);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        private ImageView imageView;
        private SimpleDraweeView simpleDraweeView;

        public ViewHolder(View view) {
            super(view);
            simpleDraweeView = (SimpleDraweeView) view;
        }

        public SimpleDraweeView getSimpleDraweeView() {
            return simpleDraweeView;
        }

    }

    public void updateAdapter(ArrayList<String> photoPaths) {
        for(String path : photoPaths) {
            if (!photos.contains(path)) {
                photos.add(path);
            }
        }
        notifyDataSetChanged();
    }


}
