package group22.travelstories;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder> {

    private Place[] fromIntent;

    public static class SuggestionViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        ImageView suggestion_photo;
        TextView suggestion_name;
        TextView suggestion_address;

        public SuggestionViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv_suggestion);
            if (itemView.findViewById(R.id.cv_suggestion) == null){
                System.out.println("Debug: suggestion cardview is null");
            }
            suggestion_photo = (ImageView) itemView.findViewById(R.id.suggestion_photo);
            if (itemView.findViewById(R.id.suggestion_photo) == null){
                System.out.println("Debug: suggestion photo is null");
            }
            suggestion_name = (TextView) itemView.findViewById(R.id.suggestion_name);
            suggestion_address = (TextView) itemView.findViewById(R.id.suggestion_address);
            System.out.println("HERE4");
            if(suggestion_name == null && suggestion_photo == null && suggestion_address == null){
                System.out.println("suggestion name is null");
            }
        }
    }

    SuggestionAdapter(Place[] places){
        fromIntent = places;
        System.out.println("HERE1");
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        System.out.println("HERE3");
    }

    @Override
    public SuggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        System.out.println("HERE2");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_suggestion, parent, false);
        SuggestionViewHolder suggestionViewHolder = new SuggestionViewHolder(v);
        return suggestionViewHolder;
    }

    @Override
    public void onBindViewHolder(SuggestionViewHolder holder, int position) {
        System.out.println("DEBUG: "+fromIntent.length);
        System.out.println("DEBUG: fromIntent[0] location name "+ fromIntent[0].getName());
        System.out.println("DEBUG: fromIntent[0] location latlong "+ fromIntent[0].getAddress());
//        holder.suggestion_photo.setImageResource(fromIntent[position].photoID);
        holder.suggestion_name.setText("hello");
//        holder.suggestion_name.setText(fromIntent[position].getName());
        holder.suggestion_address.setText(fromIntent[position].getAddress());
    }

    @Override
    public int getItemCount() {
        return fromIntent.length;
    }




}
