package group22.travelstories;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;

public class SuggestionActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.travelstories.Suggestion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Suggestion activity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.suggestions_rv);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        Intent intent = getIntent();
        System.out.println("HERE: get intent from main activity" + intent.toString());
        String json = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Gson gson = new Gson();
        Place[] places = gson.fromJson(json, Place[].class);
        SuggestionAdapter suggestionAdapter = new SuggestionAdapter(places);
        recyclerView.setAdapter(suggestionAdapter);

    }

//    public void clickSuggestion(View view) {
//        Toast.makeText(this, "show on map", Toast.LENGTH_LONG).show();
//        Intent intent = new Intent(this, MainActivity.class);
//        TextView address = (TextView) findViewById(R.id.suggestion_address);
//        String latlong = address.getText().toString();
//        System.out.println("!!!!suggestion address:" +latlong);
//        intent.putExtra(SuggestionActivity.EXTRA_MESSAGE, latlong);
//        startActivity(intent);
//    }
}
