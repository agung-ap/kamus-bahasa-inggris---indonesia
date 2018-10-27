package grup.developer.agung.kamus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import grup.developer.agung.kamus.data.helper.DictionaryHelper;
import grup.developer.agung.kamus.data.model.DictionaryModel;

public class DetailDictionaryFragment extends Fragment {
    private TextView vocab , translation;
    private DictionaryHelper dictionaryHelper;
    private ArrayList<DictionaryModel> dictionaryModels = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_dictionary, container, false);
        ((DetailDictionaryActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        if (savedInstanceState != null){
            dictionaryModels = savedInstanceState.getParcelableArrayList(getString(R.string.GET_SELECTED_VOCAB));
        }else {
            dictionaryModels = getArguments().getParcelableArrayList(getString(R.string.GET_SELECTED_VOCAB));
        }
        //set actionbar title
        ((DetailDictionaryActivity)getActivity()).getSupportActionBar().setTitle(dictionaryModels.get(0).getWord());
        //init layout
        vocab = (TextView)view.findViewById(R.id.vocab_detail);
        translation = (TextView)view.findViewById(R.id.translation_detail);
        //init helper
        dictionaryHelper = new DictionaryHelper(getActivity());
        dictionaryHelper.open(); //open connection

        vocab.setText(dictionaryModels.get(0).getWord());
        translation.setText(dictionaryModels.get(0).getTranslate());


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detail_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

       switch (item.getItemId()){
           case R.id.share:
               Intent intent = new Intent(Intent.ACTION_SEND);
               intent.setType("text/plain");
               intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name));
               intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
               intent.putExtra(Intent.EXTRA_TEXT, dictionaryModels.get(0).getWord() + "\n\n" + dictionaryModels.get(0).getTranslate());
               startActivity(Intent.createChooser(intent, getResources().getString(R.string.share)));
               //Toast.makeText(getActivity(), "share word id " + dictionaryModels.get(0).getId(), Toast.LENGTH_SHORT).show();
               break;
       }

        return super.onOptionsItemSelected(item);
    }


}
