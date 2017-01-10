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

import java.util.ArrayList;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.TimeLineViewHolder> {
    private static ArrayList fromIntent;
    private int rowLayout;
    private boolean zeroOriginal = false;

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
            final String location = timeLineName.getText().toString();
            timeLineTime = (TextView)itemView.findViewById(R.id.timeLine_time);
            String time = timeLineTime.getText().toString();
            timeLinePhoto = (ImageView)itemView.findViewById(R.id.timeLine_photo);


            View view = itemView;
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    System.out.println("Detected click on card view: " + v.toString());
                    Intent intent = new Intent(v.getContext(), EditStoryActivity.class);
                    System.out.println("Check: " + getAdapterPosition());
                    Bundle dataMap = new Bundle();
//                    ArrayList<Photo> s = ((TimeLineEntry)fromIntent.get(getAdapterPosition())).photos;
                    dataMap.putSerializable("photos", (TimeLineEntry)fromIntent.get(getAdapterPosition()));
                    intent.putExtras(dataMap);

                    intent.putExtra("Index", getAdapterPosition());
//                    v.getContext().startActivity(intent);
                    ((Activity) v.getContext()).startActivityForResult(intent, DisplayStoryActivity.EDIT_STORY_ACTIVITY_REQUEST_CODE);
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
        System.out.println("OnBindViewHolder in SummaryAdapter called");
        if (((TimeLineEntry)fromIntent.get(i)).getLocationName() == null) {
            holder.timeLineName.setText("No Location");
        } else if (((TimeLineEntry)fromIntent.get(i)).getLocationName().toString().equals("null")) {
            holder.timeLineName.setText("No Location");
        } else {
            holder.timeLineName.setText(((TimeLineEntry)fromIntent.get(i)).getLocationName());
        }
        holder.timeLineTime.setText(((TimeLineEntry)fromIntent.get(i)).getTime());

        if (!((TimeLineEntry) fromIntent.get(i)).photos.isEmpty()) {
            System.out.println("In check");
            Bitmap b = BitmapFactory.decodeFile(((TimeLineEntry)fromIntent.get(i)).photos.get(0).path);
            Bitmap scaledB = Bitmap.createScaledBitmap(b,b.getWidth()/10, b.getHeight()/10, true);
            b.recycle();
            holder.timeLinePhoto.setImageBitmap(scaledB);
        }


        //holder.timeLinePhoto.setImageResource(persons.get(i).photoId);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        if (fromIntent == null) return 0;
        return fromIntent.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void updateAdapter(TimeLineEntry entry, int position) {
        if (entry == null) {
            notifyDataSetChanged();
            return;
        }

        if (fromIntent == null) {
            fromIntent = new ArrayList();
            zeroOriginal = true;
        }
        if (entry.getLocationName() == null) {
            entry.locationName = "No Location";
        }

        if (zeroOriginal) {
            if (position == -1) {
                fromIntent.add(entry);
            } else if (position == -2) {
                System.out.println("ERROR IN INDEX");
            } else {
                System.out.println("Input at position: " + position);
                fromIntent.add(position, entry);
            }
        }
        notifyDataSetChanged();
    }

}
