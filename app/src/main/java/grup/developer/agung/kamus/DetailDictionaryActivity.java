package grup.developer.agung.kamus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;

import grup.developer.agung.kamus.data.model.DictionaryModel;

public class DetailDictionaryActivity extends AppCompatActivity {
    private ArrayList<DictionaryModel> dictionaryModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_dictionary);

        if (savedInstanceState == null){
            Bundle getBundle = getIntent().getExtras();

            //get data from intent
            dictionaryModels = new ArrayList<>();
            dictionaryModels = getBundle.getParcelableArrayList(getString(R.string.GET_SELECTED_VOCAB));

            // init detail fragment
            DetailDictionaryFragment fragment = new DetailDictionaryFragment();
            fragment.setArguments(getBundle); //send argument to framgment from bundle
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_frame_layout, fragment).commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                supportFinishAfterTransition();
                super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
