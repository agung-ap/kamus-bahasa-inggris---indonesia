package grup.developer.agung.kamus;

import android.content.Intent;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import grup.developer.agung.kamus.data.helper.DictionaryHelper;
import grup.developer.agung.kamus.data.model.DictionaryModel;

public class PreloadActivity extends AppCompatActivity {
    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;

    @BindView(R.id.tv_load)
    TextView tv_load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preload);

        ButterKnife.bind(this);
        //new LoadDataAsync(this).execute();
        new LoadData().execute();
    }

    private void loadDummyProcess() {
        final int countDown = 1000;
        progress_bar.setMax(countDown);
        CountDownTimer countDownTimer = new CountDownTimer(countDown, (countDown / 100)) {
            @Override
            public void onTick(long l) {
                progress_bar.setProgress((int) (countDown - l));
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }

    public ArrayList<DictionaryModel> preLoadRaw(int data) {
        ArrayList<DictionaryModel> dictionaryModels = new ArrayList<>();
        BufferedReader reader;
        try {
            InputStream raw_dict = getResources().openRawResource(data);

            reader = new BufferedReader(new InputStreamReader(raw_dict));
            String line = null;
            do {
                line = reader.readLine();
                String[] splitstr = line.split("\t");
                DictionaryModel dictionaryModel;
                dictionaryModel = new DictionaryModel(splitstr[0], splitstr[1]);
                dictionaryModels.add(dictionaryModel);

            } while (line != null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dictionaryModels;
    }

    private class LoadData extends AsyncTask<Void,Integer,Void>{
        DictionaryHelper dictionaryHelper;
        StorePreference storePreference;
        double progress;
        double maxprogress = 100;

        @Override
        protected void onPreExecute() {
            dictionaryHelper = new DictionaryHelper(PreloadActivity.this);
            storePreference = new StorePreference(PreloadActivity.this);
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            Boolean firstRun = storePreference.getFirstRun();

            if (firstRun){
                ArrayList<DictionaryModel> kamusEnglish = preLoadRaw(R.raw.english_indonesia);
                ArrayList<DictionaryModel> kamusIndonesia = preLoadRaw(R.raw.indonesia_english);

                publishProgress((int) progress);

                try {
                    dictionaryHelper.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                Double progressMaxInsert = 100.0;
                Double progressDiff = (progressMaxInsert - progress) / (kamusEnglish.size() + kamusIndonesia.size());

                dictionaryHelper.insertTransaction(kamusEnglish, true);
                progress += progressDiff;
                publishProgress((int) progress);

                dictionaryHelper.insertTransaction(kamusIndonesia, false);
                progress += progressDiff;
                publishProgress((int) progress);

                dictionaryHelper.close();
                storePreference.setFirstRun(false);

                publishProgress((int) maxprogress);
            }else {
                tv_load.setVisibility(View.INVISIBLE);
                progress_bar.setVisibility(View.INVISIBLE);
                try {
                    synchronized (this) {
                        this.wait(1000);
                        publishProgress(50);

                        this.wait(300);
                        publishProgress((int) maxprogress);
                    }
                } catch (Exception e) {
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress_bar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            Intent intent = new Intent(PreloadActivity.this, MainActivity.class);
            startActivity(intent);

            finish();
        }
    }
}
