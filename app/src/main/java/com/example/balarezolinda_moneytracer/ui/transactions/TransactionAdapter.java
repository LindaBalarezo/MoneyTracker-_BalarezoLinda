package com.example.balarezolinda_moneytracer.ui.transactions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.balarezolinda_moneytracer.R;
import com.example.balarezolinda_moneytracer.model.Category;
import com.example.balarezolinda_moneytracer.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Transaction> transactionData;
    private List<Category> categoryData;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    public TransactionAdapter(List<Transaction> transactionData, List<Category> categoryData, OnItemClickListener listener) {
        this.transactionData = transactionData;
        this.categoryData = categoryData;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction t = transactionData.get(position);
        holder.bind(t, listener);
    }

    @Override
    public int getItemCount() {
        return transactionData.size();
    }

    public Transaction getItem(int position) {
        return transactionData.get(position);
    }

    public void remove(int position) {
        transactionData.remove(position);
        notifyItemRemoved(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDescription, txtAmount, txtCategory, txtDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtDate = itemView.findViewById(R.id.txtDate);
        }

        public void bind(final Transaction transaction, final OnItemClickListener listener) {
            txtDescription.setText(transaction.getDescription());
            txtAmount.setText("$ " + transaction.getAmount());

            String categoryName = "Desconocido";
            for (Category category : categoryData) {
                if (category.getId() == transaction.getCategoryId()) {
                    categoryName = category.getName();
                    break;
                }
            }
            txtCategory.setText(categoryName);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dateString = sdf.format(new Date(transaction.getDate()));
            txtDate.setText(dateString);

            itemView.setOnClickListener(v -> listener.onItemClick(transaction));
        }
    }
}
