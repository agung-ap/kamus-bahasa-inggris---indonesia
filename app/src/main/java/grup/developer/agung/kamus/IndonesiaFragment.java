package grup.developer.agung.kamus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import grup.developer.agung.kamus.adapter.DictionaryAdapter;
import grup.developer.agung.kamus.data.helper.DictionaryHelper;
import grup.developer.agung.kamus.data.model.DictionaryModel;

public class IndonesiaFragment extends Fragment implements DictionaryAdapter.OnClickHandler , DictionaryAdapter.OnLongClickHandler {
    private RecyclerView recyclerView;
    private TextView isEmptyMessage;
    private DictionaryHelper dictionaryHelper;
    private DictionaryAdapter dictionaryAdapter;
    private ArrayList<DictionaryModel> dictionaryList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_indonesia, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Indonesia - Inggris");
        setHasOptionsMenu(true);

        isEmptyMessage = (TextView)view.findViewById(R.id.is_empty_message);
        isEmptyMessage.setVisibility(View.GONE);
        //init recycler view
        recyclerView = (RecyclerView)view.findViewById(R.id.Indonesia_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        //init databasehelper
        dictionaryHelper = new DictionaryHelper(getActivity());
        dictionaryHelper.open(); //open connection

        dictionaryList = new ArrayList<>(); //init arraylist

        //init adapter
        dictionaryAdapter = new DictionaryAdapter(getActivity(),this, this);
        recyclerView.setAdapter(dictionaryAdapter); //setadapter to recycler view

        //load dictionary on background process
        new LoadDictionary().execute();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
        //search view
        searchVocabulary(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.tambah_kosakata) {
            Intent intent = new Intent(getActivity(), AddActivity.class);
            intent.putExtra("isEnglish", false);
            startActivityForResult(intent, AddActivity.REQUEST_ADD);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchVocabulary(Menu menu){
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setQueryHint("Cari Kosa Kata");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    dictionaryHelper.open();
                    if (query.isEmpty()) {
                        dictionaryList = dictionaryHelper.getAllData(false);
                    } else {
                        dictionaryList = dictionaryHelper.getDataByName(query, false);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    dictionaryHelper.close();
                }
                dictionaryAdapter.setDictionaryData(dictionaryList);
                //Toast.makeText(getActivity(),query,Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onClick(DictionaryModel position) {
        Bundle bundle = new Bundle();

        ArrayList<DictionaryModel> dictionaryModels = new ArrayList<>();
        dictionaryModels.add(position);

        bundle.putParcelableArrayList(getString(R.string.GET_SELECTED_VOCAB), dictionaryModels);

        //send data via intent
        Intent intent = new Intent(this.getActivity(), DetailDictionaryActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onLongClick(DictionaryModel position) {
        showOptionDialog(position, false);
        Toast.makeText(getActivity(), "position : " + position, Toast.LENGTH_SHORT).show();
    }

    private void showOptionDialog(final DictionaryModel dictionaryModel
            , final boolean isEnglish){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());

        final ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Edit");
        arrayAdapter.add("Delete");

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0 :
                        Intent intent = new Intent(getActivity(), AddActivity.class);
                        intent.putExtra(getString(R.string.isEnglish), isEnglish);
                        intent.putExtra(getString(R.string.isEdit), true);
                        intent.putExtra("word", dictionaryModel.getWord()); //send word data
                        intent.putExtra("translate", dictionaryModel.getTranslate()); //send translate data
                        intent.putExtra("id", dictionaryModel.getId());
                        startActivityForResult(intent, AddActivity.REQUEST_UPDATE);
                        //Toast.makeText(getActivity(), "which " + which, Toast.LENGTH_SHORT).show();
                        break;
                    case 1 :
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
                        builderInner.setTitle("Are You Sure to Delete this ");
                        builderInner.setPositiveButton("sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                dictionaryHelper.delete(dictionaryModel.getId(),false);
                                Toast.makeText(getActivity(), "item dihapus", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builderInner.setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builderInner.show();
                        //Toast.makeText(getActivity(), "which " + which, Toast.LENGTH_SHORT).show();
                        break;
                }
                /*
                String strName = arrayAdapter.getItem(which);

                AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();*/
            }
        });
        builderSingle.show();
    }

    private class LoadDictionary extends AsyncTask<Void, Void, ArrayList<DictionaryModel>> {

        @Override
        protected ArrayList<DictionaryModel> doInBackground(Void... voids) {
            //get all data from database
            return dictionaryHelper.getAllData(false);
        }

        @Override
        protected void onPostExecute(ArrayList<DictionaryModel> data) {
            super.onPostExecute(data);
            if (data.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                isEmptyMessage.setVisibility(View.GONE);

                dictionaryAdapter.setDictionaryData(data);

            }else if (data.size() <= 0){
                recyclerView.setVisibility(View.GONE);
                isEmptyMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddActivity.REQUEST_UPDATE){
            if (resultCode == AddActivity.RESULT_UPDATE){

                Toast.makeText(getActivity(), "data berhasil diubah", Toast.LENGTH_SHORT).show();
            }else if (requestCode == AddActivity.REQUEST_ADD){
                if (resultCode == AddActivity.RESULT_ADD){

                    Toast.makeText(getActivity(), "data berhasil ditambah", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
