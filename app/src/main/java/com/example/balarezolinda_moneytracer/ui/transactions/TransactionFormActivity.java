package com.example.balarezolinda_moneytracer.ui.transactions;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.balarezolinda_moneytracer.R;
import com.example.balarezolinda_moneytracer.db.TransactionRepository;
import com.example.balarezolinda_moneytracer.model.Category;
import com.example.balarezolinda_moneytracer.model.Transaction;
import com.example.balarezolinda_moneytracer.network.ApiService;
import com.example.balarezolinda_moneytracer.network.ExchangeRateResponse;
import com.example.balarezolinda_moneytracer.network.RetrofitClient;
import com.example.balarezolinda_moneytracer.util.PreferenceManager;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionFormActivity extends AppCompatActivity {

    private EditText edtDescription, edtAmount, edtPaymentMethod;
    private Spinner spnCategory, spnType, spnConvertTo;
    private Button btnSave, btnConvert;
    private TextView txtConversionResult;

    private TransactionRepository repository;
    private PreferenceManager preferenceManager;
    private List<Category> categories;
    private Transaction currentTransaction; // Para edición
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_form);

        // Bind views
        edtDescription = findViewById(R.id.edtDescription);
        edtAmount = findViewById(R.id.edtAmount);
        edtPaymentMethod = findViewById(R.id.edtPaymentMethod);
        spnCategory = findViewById(R.id.spnCategory);
        spnType = findViewById(R.id.spnType);
        spnConvertTo = findViewById(R.id.spnConvertTo);
        btnSave = findViewById(R.id.btnSave);
        btnConvert = findViewById(R.id.btnConvert);
        txtConversionResult = findViewById(R.id.txtConversionResult);

        repository = new TransactionRepository(this);
        preferenceManager = new PreferenceManager(this);
        categories = repository.getAllCategories();

        setupSpinners();

        long transactionId = getIntent().getLongExtra(TransactionsActivity.EXTRA_TRANSACTION_ID, -1);
        if (transactionId != -1) {
            isEditMode = true;
            currentTransaction = repository.getById(transactionId);
            btnSave.setText("Actualizar");
            setTitle("Editar Transacción");
            populateForm();
        } else {
            setTitle("Nueva Transacción");
        }

        btnSave.setOnClickListener(v -> saveOrUpdateTransaction());
        btnConvert.setOnClickListener(v -> convertCurrency());
    }

    private void setupSpinners() {
        // Type Spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"EXPENSE", "INCOME"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(typeAdapter);

        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isEditMode || currentTransaction == null) {
                    String selectedType = (String) parent.getItemAtPosition(position);
                    updateCategorySpinner(selectedType);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Currency Converter Spinner
        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(this, R.array.currencies_array, android.R.layout.simple_spinner_item);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnConvertTo.setAdapter(currencyAdapter);
    }

    private void updateCategorySpinner(String type) {
        List<Category> filteredCategories = categories.stream()
                .filter(c -> c.getType().equals(type))
                .collect(Collectors.toList());
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filteredCategories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(categoryAdapter);
    }

    private void populateForm() {
        if (currentTransaction == null) return;

        edtDescription.setText(currentTransaction.getDescription());
        edtAmount.setText(String.valueOf(currentTransaction.getAmount()));
        edtPaymentMethod.setText(currentTransaction.getPaymentMethod());

        String[] types = {"EXPENSE", "INCOME"};
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(currentTransaction.getType())) {
                spnType.setSelection(i);
                break;
            }
        }
        updateCategorySpinner(currentTransaction.getType());
        for (int i = 0; i < spnCategory.getAdapter().getCount(); i++) {
            if (((Category) spnCategory.getItemAtPosition(i)).getId() == currentTransaction.getCategoryId()) {
                spnCategory.setSelection(i);
                break;
            }
        }
    }

    private void saveOrUpdateTransaction() {
        String description = edtDescription.getText().toString();
        String amountStr = edtAmount.getText().toString();
        String paymentMethod = edtPaymentMethod.getText().toString();

        if (TextUtils.isEmpty(description) || TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Descripción y monto son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        Category selectedCategory = (Category) spnCategory.getSelectedItem();
        String type = spnType.getSelectedItem().toString();
        long categoryId = selectedCategory.getId();

        if (isEditMode) {
            currentTransaction.setType(type);
            currentTransaction.setAmount(amount);
            currentTransaction.setCategoryId(categoryId);
            currentTransaction.setDescription(description);
            currentTransaction.setPaymentMethod(paymentMethod);
            repository.update(currentTransaction);
            Toast.makeText(this, "Transacción actualizada", Toast.LENGTH_SHORT).show();
        } else {
            long currentTime = System.currentTimeMillis();
            Transaction t = new Transaction(0, type, amount, categoryId, description, currentTime, paymentMethod, currentTime);
            repository.insert(t);
            Toast.makeText(this, "Transacción guardada", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void convertCurrency() {
        String amountStr = edtAmount.getText().toString();
        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Ingresa un monto para convertir", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String fromCurrency = preferenceManager.getCurrency();
        String toCurrency = spnConvertTo.getSelectedItem().toString();

        ApiService apiService = RetrofitClient.getApiService();
        Call<ExchangeRateResponse> call = apiService.getExchangeRates(fromCurrency);

        call.enqueue(new Callback<ExchangeRateResponse>() {
            @Override
            public void onResponse(@NonNull Call<ExchangeRateResponse> call, @NonNull Response<ExchangeRateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Double rate = response.body().getRates().get(toCurrency);
                    if (rate != null) {
                        double convertedAmount = amount * rate;
                        txtConversionResult.setText(String.format("%.2f %s = %.2f %s", amount, fromCurrency, convertedAmount, toCurrency));
                    } else {
                        Toast.makeText(TransactionFormActivity.this, "Tasa de cambio no disponible para " + toCurrency, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TransactionFormActivity.this, "Error al obtener tasas de cambio", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ExchangeRateResponse> call, @NonNull Throwable t) {
                Toast.makeText(TransactionFormActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
