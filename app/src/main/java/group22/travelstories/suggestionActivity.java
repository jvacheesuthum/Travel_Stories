package group22.travelstories;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;

public class SuggestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Suggestion activity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.suggestions_rv);
        recyclerView.setHasFixedSize(true);

        System.out.println("HERE: found recyclerview");

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        System.out.println("HERE: set layout manager for recycler view");

        Intent intent = getIntent();
        System.out.println("HERE: get intent from main activity" + intent.toString());
        String json = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Gson gson = new Gson();
        System.out.println("DEBUGGGG: " + json);
        Place[] places = gson.fromJson(json, Place[].class);
        System.out.println("Size of suggestions: " + places.length);
        SuggestionAdapter suggestionAdapter = new SuggestionAdapter(places);
        recyclerView.setAdapter(suggestionAdapter);
        System.out.println("HERE!!!!");

    }
}
