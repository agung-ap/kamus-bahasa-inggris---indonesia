package grup.developer.agung.kamus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import grup.developer.agung.kamus.data.helper.DictionaryHelper;
import grup.developer.agung.kamus.data.model.DictionaryModel;

public class AddActivity extends AppCompatActivity {
    @BindView(R.id.edt_title)
    EditText word;
    @BindView(R.id.edt_description)
    EditText translate;
    @BindView(R.id.btn_save)
    Button submit;

    private ArrayList <DictionaryModel> data;
    private DictionaryHelper dictionaryHelper;

    private boolean isEnglish , isEdit;

    public static int REQUEST_ADD = 100;
    public static int RESULT_ADD = 101;
    public static int REQUEST_UPDATE = 200;
    public static int RESULT_UPDATE = 201;
    public static int RESULT_DELETE = 301;

    private String wordData, translateData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);
        //enable home button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //init arraylist
        data = new ArrayList<>();
        //init helper
        dictionaryHelper = new DictionaryHelper(this);
        dictionaryHelper.open();
        //is english language or not?
        isEnglish = getIntent().getBooleanExtra(getString(R.string.isEnglish), true);
        //is editable or not?
        isEdit = getIntent().getBooleanExtra(getString(R.string.isEdit), false);
        //if edit is true
        if (isEdit){
            word.setText(getIntent().getStringExtra("word"));
            translate.setText(getIntent().getStringExtra("translate"));
            submit.setText("Edit");
        }
        //input data
        addData();
    }
    //input data function
    public void addData(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wordData = word.getText().toString().trim();
                translateData = translate.getText().toString().trim();

                boolean isEmpty = false;

                if (wordData.isEmpty() && translateData.isEmpty()){
                    word.setError("Field can not be blank");
                    translate.setError("Field can not be blank");
                    isEmpty = true;
                }

                if (!isEmpty){
                    DictionaryModel newDictionaryModel = new DictionaryModel();
                    newDictionaryModel.setWord(wordData);
                    newDictionaryModel.setTranslate(translateData);

                    if (isEdit){
                        newDictionaryModel.setId(getIntent().getIntExtra("id",0));
                        dictionaryHelper.update(newDictionaryModel, isEnglish);

                        setResult(RESULT_UPDATE);
                        finish();
                    }else {
                        dictionaryHelper.insert(newDictionaryModel, isEnglish);

                        setResult(RESULT_ADD);
                        finish();
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
               finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
