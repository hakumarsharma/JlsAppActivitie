package com.jio.devicetracker.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.FAQData;
import com.jio.devicetracker.util.Util;

import java.util.List;

public class FAQListAdapter  extends RecyclerView.Adapter<FAQListAdapter.ViewHolder> {

    private List<FAQData> mData;
    private Context mContext;

    /**
     * Constructor to add devices in home screen
     *
     * @param mData
     */
    public FAQListAdapter(List mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_faq_list, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull FAQListAdapter.ViewHolder holder, int position) {
        holder.questionText.setText(mData.get(position).getQuestion());
        holder.answerText.setText(mData.get(position).getAnswer());
        holder.questionText.setTypeface(Util.mTypeface(mContext,5));
        boolean isExpandable = mData.get(position).isExpandable();
        holder.answerLayout.setVisibility(isExpandable? View.VISIBLE : View.GONE);
        holder.faqOpenCloseIcon.setImageDrawable(isExpandable? mContext.getResources().getDrawable(R.drawable.faq_arrowclose):mContext.getResources().getDrawable(R.drawable.faq_arrowopen));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView faqOpenCloseIcon;
        public TextView questionText;
        public TextView answerText;
        public RelativeLayout answerLayout;

        /**
         * Constructor where we find element from .xml file
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            faqOpenCloseIcon = itemView.findViewById(R.id.faq_open_close_icon);
            questionText = itemView.findViewById(R.id.question);
            answerText = itemView.findViewById(R.id.answer);
            answerLayout =itemView.findViewById(R.id.answerLayout);

            faqOpenCloseIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FAQData data = mData.get(getAdapterPosition());
                    data.setExpandable(!data.isExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });

        }
    }


}
