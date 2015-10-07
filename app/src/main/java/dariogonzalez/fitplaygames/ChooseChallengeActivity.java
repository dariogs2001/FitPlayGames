package dariogonzalez.fitplaygames;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dariogonzalez.fitplaygames.Adapters.ChallengeRecyclerViewAdapter;
import dariogonzalez.fitplaygames.classes.ParentGame;
import dariogonzalez.fitplaygames.utils.RecyclerItemClickListener;

public class ChooseChallengeActivity extends AppCompatActivity {

    private List<ParentGame> parentGameList;

    private void initializeData(){
        parentGameList = new ArrayList<>();
        parentGameList.add(new ParentGame("Hot Potato", R.mipmap.fitbit_black));
        parentGameList.add(new ParentGame("Running", R.mipmap.fitbit_white));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_challenge);

        initializeData();

        RecyclerView rv = (RecyclerView)findViewById(R.id.choose_challenge_recyclerView);
        rv.setHasFixedSize(true);
        StaggeredGridLayoutManager llm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        ChallengeRecyclerViewAdapter adapter = new ChallengeRecyclerViewAdapter(parentGameList);
        rv.setAdapter(adapter);

        rv.addOnItemTouchListener(new RecyclerItemClickListener(this, rv, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String challengeName = parentGameList.get(position).getChallengeName();
                switch (challengeName) {
                    case "Hot Potato":
                        Intent intent = new Intent(ChooseChallengeActivity.this, HotPotatoChallengeActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(view.getContext(), "LONG LONG LONG", Toast.LENGTH_SHORT).show();
            }
        }));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_challenge, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
