package com.jio.devicetracker.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;

import java.util.List;

public class AddPersonListAdapter extends RecyclerView.Adapter<AddPersonListAdapter.ViewHolder> {

    private List<String> mData;

    /**
     * Constructor to add devices in home screen
     *
     * @param mData
     */
    public AddPersonListAdapter(List mData) {
        this.mData = mData;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_addperson, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.contactName.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView contactImage;
        public TextView contactName;
        public Button deleteButton;
        public CardView mListLayout;

        /**
         * Constructor where we find element from .xml file
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.contactImage);
            contactName = itemView.findViewById(R.id.contactName);
            mListLayout = itemView.findViewById(R.id.listLayout);
            deleteButton = itemView.findViewById(R.id.deleteButton);

        }
    }


}