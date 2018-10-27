package grup.developer.agung.kamus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import grup.developer.agung.kamus.R;
import grup.developer.agung.kamus.data.model.DictionaryModel;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.ViewHolder> {
    private ArrayList<DictionaryModel> listDictionary;
    private Context context;
    private OnClickHandler clickHandler;
    private OnLongClickHandler longClickHandler;

    public DictionaryAdapter(Context context, OnClickHandler clickHandler, OnLongClickHandler longClickHandler) {
        this.longClickHandler = longClickHandler;
        this.clickHandler = clickHandler;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dictionary, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.wordTv.setText(listDictionary.get(i).getWord());
        viewHolder.translaterTv.setText(listDictionary.get(i).getTranslate());
    }

    @Override
    public int getItemCount() {
        if (null == listDictionary) return 0;
        return listDictionary.size();
    }

    public void setDictionaryData(ArrayList<DictionaryModel> data) {
        listDictionary = data;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener {
        @BindView(R.id.list_word_tv)
        TextView wordTv;
        @BindView(R.id.list_translate_tv)
        TextView translaterTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            //set item to click
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            clickHandler.onClick(listDictionary.get(adapterPosition));
        }

        @Override
        public boolean onLongClick(View view) {
            int adapterPosition = getAdapterPosition();
            longClickHandler.onLongClick(listDictionary.get(adapterPosition));
            return true;
        }
    }

    public interface OnClickHandler{
        void onClick (DictionaryModel position);
    }

    public interface OnLongClickHandler{
        void onLongClick (DictionaryModel position);
    }
}
