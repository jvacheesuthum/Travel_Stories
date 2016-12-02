package group22.travelstories;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class SuggestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Suggestion activity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        Intent intent = getIntent();
        String json = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Gson gson = new Gson();
        Place[] places = gson.fromJson(json, Place[].class);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.suggestions_rv);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
    }
}
