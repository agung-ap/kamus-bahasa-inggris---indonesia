package grup.developer.agung.kamus.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DictionaryModel implements Parcelable {
    private int id;
    private String word;
    private String translate;

    public DictionaryModel() {
    }

    public DictionaryModel (String word, String translate) {
        //this.id = id;
        this.word = word;
        this.translate = translate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public static Creator<DictionaryModel> getCREATOR() {
        return CREATOR;
    }

    protected DictionaryModel(Parcel in) {
        id = in.readInt();
        word = in.readString();
        translate = in.readString();
    }

    public static final Creator<DictionaryModel> CREATOR = new Creator<DictionaryModel>() {
        @Override
        public DictionaryModel createFromParcel(Parcel in) {
            return new DictionaryModel(in);
        }

        @Override
        public DictionaryModel[] newArray(int size) {
            return new DictionaryModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(word);
        parcel.writeString(translate);
    }
}
