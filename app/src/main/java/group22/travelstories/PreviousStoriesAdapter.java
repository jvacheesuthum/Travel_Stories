package group22.travelstories;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Felix on 02/01/2017.
 */

public class PreviousStoriesAdapter extends RecyclerView.Adapter<PreviousStoriesAdapter.ViewHolder> {

    private ArrayList<ArrayList> stories;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        // each data item is just a string in this case

        public ViewHolder(View v) {
            super(v);
            text = (TextView)v.findViewById(R.id.test_text);
        }
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
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.text.setText((String)stories.get(position).get(0));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return stories.size();
    }


}
