package group22.travelstories;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.net.URI;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vasin on 09/11/2016.
 */
public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.TimeLineViewHolder> {
    private ArrayList fromIntent;
    private int rowLayout;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class TimeLineViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView timeLineName;
        TextView timeLineTime;
        ImageView timeLinePhoto;

        TimeLineViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            timeLineName = (TextView)itemView.findViewById(R.id.timeLine_name);
            timeLineTime = (TextView)itemView.findViewById(R.id.timeLine_time);
            timeLinePhoto = (ImageView)itemView.findViewById(R.id.timeLine_photo);

            View view = itemView;
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    System.out.println("Detected click on card view: " + v.toString());
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SummaryAdapter(ArrayList timeLine, int rowLayout) {
        fromIntent = (ArrayList) timeLine;
        this.rowLayout = rowLayout;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        TimeLineViewHolder pvh = new TimeLineViewHolder(v);
        return pvh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int i) {

        holder.timeLineName.setText(((TimeLineEntry)fromIntent.get(i)).getLocationName());
        holder.timeLineTime.setText(((TimeLineEntry)fromIntent.get(i)).getTime());

//        Bitmap b = BitmapFactory.decodeFile(((TimeLineEntry)fromIntent.get(i)).photos.get(0).path);
//        Bitmap scaledB = Bitmap.createScaledBitmap(b,b.getWidth()/10, b.getHeight()/10, true);
//        b.recycle();
//        holder.timeLinePhoto.setImageBitmap(scaledB);

        //holder.timeLinePhoto.setImageResource(persons.get(i).photoId);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return fromIntent.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
