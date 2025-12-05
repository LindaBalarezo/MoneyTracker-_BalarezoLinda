package com.example.balarezolinda_moneytracer.ui.transactions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.balarezolinda_moneytracer.R;
import com.example.balarezolinda_moneytracer.db.TransactionRepository;
import com.example.balarezolinda_moneytracer.model.Category;
import com.example.balarezolinda_moneytracer.model.Transaction;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity implements TransactionAdapter.OnItemClickListener, AdapterView.OnItemSelectedListener {

    public static final String EXTRA_TRANSACTION_ID = "extra_transaction_id";

    private RecyclerView recyclerTransactions;
    private Button btnAddTransaction;
    private Spinner spnFilterType, spnFilterCategory;

    private TransactionAdapter adapter;
    private TransactionRepository repository;
    private List<Category> categoryList;
    private ActivityResultLauncher<Intent> transactionFormLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        recyclerTransactions = findViewById(R.id.recyclerTransactions);
        btnAddTransaction = findViewById(R.id.btnAddTransaction);
        spnFilterType = findViewById(R.id.spnFilterType);
        spnFilterCategory = findViewById(R.id.spnFilterCategory);
        recyclerTransactions.setLayoutManager(new LinearLayoutManager(this));

        repository = new TransactionRepository(this);
        categoryList = repository.getAllCategories();

        // ActivityResultLauncher para el formulario de transacciones
        transactionFormLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // onResume se encargará de recargar
            });

        setupFilterSpinners();
        loadTransactions(null, null);
        setupSwipeToDelete();

        btnAddTransaction.setOnClickListener(v ->
                transactionFormLauncher.launch(new Intent(this, TransactionFormActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransactions(null, null); // Recargar con filtros por defecto
    }

    private void setupFilterSpinners() {
        // Type Spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"ALL", "INCOME", "EXPENSE"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFilterType.setAdapter(typeAdapter);

        // Category Spinner
        List<Category> categoriesWithAll = new ArrayList<>();
        categoriesWithAll.add(new Category(0, "ALL", "")); // Opción "ALL"
        categoriesWithAll.addAll(categoryList);
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesWithAll);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFilterCategory.setAdapter(categoryAdapter);

        spnFilterType.setOnItemSelectedListener(this);
        spnFilterCategory.setOnItemSelectedListener(this);
    }

    private void loadTransactions(String filterType, Category filterCategory) {
        List<Transaction> transactionList = repository.getTransactions(filterType, filterCategory);
        adapter = new TransactionAdapter(transactionList, categoryList, this);
        recyclerTransactions.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String type = (String) spnFilterType.getSelectedItem();
        Category category = (Category) spnFilterCategory.getSelectedItem();
        if (category.getId() == 0) { // "ALL" category
            category = null;
        }
        loadTransactions(type, category);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    @Override
    public void onItemClick(Transaction transaction) {
        Intent intent = new Intent(this, TransactionFormActivity.class);
        intent.putExtra(EXTRA_TRANSACTION_ID, transaction.getId());
        transactionFormLauncher.launch(intent);
    }

    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Transaction transaction = adapter.getItem(position);

                repository.delete((int) transaction.getId());
                adapter.remove(position);

                Snackbar.make(recyclerTransactions, "Transacción eliminada", Snackbar.LENGTH_LONG)
                        .setAction("Deshacer", v -> {
                            repository.insert(transaction);
                            loadTransactions(null, null); // Recargar para restaurar
                        }).show();
            }
        }).attachToRecyclerView(recyclerTransactions);
    }
}
