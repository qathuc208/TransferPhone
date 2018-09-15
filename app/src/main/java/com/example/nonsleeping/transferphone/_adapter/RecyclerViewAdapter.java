package com.example.nonsleeping.transferphone._adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.nonsleeping.transferphone.R;
import com.example.nonsleeping.transferphone._viewholder.RecyclerViewHolders;
import com.example.nonsleeping.transferphone.dto.ContactInfor;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {

    private Context context;
    private List<ContactInfor> itemList;

    public RecyclerViewAdapter(Context context, List<ContactInfor> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    public void updateList(List<ContactInfor> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolders(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_contacts_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolders holder, int position) {
        ContactInfor contactInfor = (ContactInfor) this.itemList.get(position);
        holder.txtName.setText(this.context.getResources().getString(R.string.name) + ": " + contactInfor.getName());
        holder.txtPhone.setText(this.context.getResources().getString(R.string.phone_number) + ": " + contactInfor.getPhoneNumber());
    }

    public int getItemCount() {
        return this.itemList.size();
    }
}
