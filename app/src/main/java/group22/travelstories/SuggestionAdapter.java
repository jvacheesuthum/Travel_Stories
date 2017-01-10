package group22.travelstories;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

            suggestion_photo = (ImageView) itemView.findViewById(R.id.suggestion_photo);
            suggestion_name = (TextView) itemView.findViewById(R.id.suggestion_name);
            suggestion_address = (TextView) itemView.findViewById(R.id.suggestion_address);

            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    System.out.println("Detected click on card view: " + v.toString());
                    String latlong = suggestion_address.getText().toString();
                    Intent addMarkerIntent = new Intent(v.getContext(), MainActivity.class);
                    System.out.println("Sending intent from SUGGESTION!!!: " + addMarkerIntent);
                    System.out.println("!!!!suggestion address: " +latlong);
//                    addMarkerIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    addMarkerIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    addMarkerIntent.putExtra("latlong", latlong);
                    (v.getContext()).startActivity(addMarkerIntent);
                }
            });
        }
    }

    SuggestionAdapter(Place[] places){
        fromIntent = places;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public SuggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_suggestion, parent, false);
        SuggestionViewHolder suggestionViewHolder = new SuggestionViewHolder(v);
        return suggestionViewHolder;
    }

    @Override
    public void onBindViewHolder(SuggestionViewHolder holder, int position) {
//        holder.suggestion_photo.setImageResource(fromIntent[position].photoID);
        holder.suggestion_name.setText(fromIntent[position].getName());
        holder.suggestion_address.setText(fromIntent[position].getAddress());
    }

    @Override
    public int getItemCount() {
        return fromIntent.length;
    }


}
