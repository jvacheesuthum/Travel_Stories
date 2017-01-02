package group22.travelstories;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Felix on 02/01/2017.
 */

public class PreviousStoriesAdapter extends RecyclerView.Adapter<PreviousStoriesAdapter.ViewHolder> {

    private ArrayList<ArrayList> stories;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView storyName;
        TextView storyTime;
        ImageView storyPhoto;

        // each data item is just a string in this case

        public ViewHolder(View v) {
            super(v);
            storyName = (TextView)v.findViewById(R.id.story_name);
            storyPhoto = (ImageView)v.findViewById(R.id.story_photo);
            storyTime = (TextView)v.findViewById(R.id.story_time);
        }



//            View view = itemView;
//            view.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    System.out.println("Detected click on card view: " + v.toString());
//                    Intent intent = new Intent(v.getContext(), EditStoryActivity.class);
//                    System.out.println("Check: " + getAdapterPosition());
//                    Bundle dataMap = new Bundle();
////                    ArrayList<Photo> s = ((TimeLineEntry)fromIntent.get(getAdapterPosition())).photos;
//                    dataMap.putSerializable("photos", (TimeLineEntry)fromIntent.get(getAdapterPosition()));
//                    intent.putExtras(dataMap);
//
//                    intent.putExtra("Index", getAdapterPosition());
////                    v.getContext().startActivity(intent);
//                    ((Activity) v.getContext()).startActivityForResult(intent, 1);
//                }
//            });
//        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PreviousStoriesAdapter(ArrayList<ArrayList> stories) {
        this.stories = stories;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stories_cardview, parent, false); //<-------cardview layout HERE
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.storyName.setText("Placeholder");

        ArrayList timeline = stories.get(position);
        Date start = ((TimeLineEntry)timeline.get(0)).start.getTime();
        Date end = ((TimeLineEntry)timeline.get(timeline.size() - 1)).end.getTime();

        System.out.println("Start: " + start);
        System.out.println("End: " + end);

        holder.storyTime.setText(start + " - " + end);

        if (!((TimeLineEntry) timeline.get(0)).photos.isEmpty()) {
            System.out.println("In check");
            Bitmap b = BitmapFactory.decodeFile(((TimeLineEntry)timeline.get(0)).photos.get(0).path);
            Bitmap scaledB = Bitmap.createScaledBitmap(b,b.getWidth()/10, b.getHeight()/10, true);
            b.recycle();
            holder.storyPhoto.setImageBitmap(scaledB);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return stories.size();
    }

    public void updateAdapter(ArrayList timeline) {
        if (timeline == null) {
            notifyDataSetChanged();
            return;
        }

//        stories.add(timeline);
        notifyDataSetChanged();
    }

}
