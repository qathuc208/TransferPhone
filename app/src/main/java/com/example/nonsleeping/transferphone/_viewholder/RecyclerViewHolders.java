package com.example.nonsleeping.transferphone._viewholder;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.TextView;

import com.example.nonsleeping.transferphone.R;

public class RecyclerViewHolders extends ViewHolder {
    public TextView txtName;
    public TextView txtPhone;

    public RecyclerViewHolders(View itemView) {
        super(itemView);
        this.txtName = (TextView) itemView.findViewById(R.id.tvName);
        this.txtPhone = (TextView) itemView.findViewById(R.id.tvPhone);
    }
}
