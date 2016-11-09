package group22.travelstories;

import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vasin on 09/11/2016.
 */
public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.TimeLineViewHolder> {
    private ArrayList<String[]> fromIntent;

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
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SummaryAdapter(ArrayList timeLine) {
        fromIntent = (ArrayList<String[]>) timeLine;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        TimeLineViewHolder pvh = new TimeLineViewHolder(v);
        return pvh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int i) {

        if(holder.timeLineName == null){
            System.out.println("NAME NULL");
        }

        //TODO: null here!
//        holder.timeLineName.setText("nanme");
        //holder.timeLineTime.setText("time");
        holder.timeLineName.setText(fromIntent.get(i)[0]);
        holder.timeLineTime.setText(fromIntent.get(i)[1]);

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
