package com.example.balarezolinda_moneytracer.ui.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.balarezolinda_moneytracer.R;
import com.example.balarezolinda_moneytracer.db.TransactionRepository;
import com.example.balarezolinda_moneytracer.util.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    private EditText edtName, edtBudget, edtStartDay;
    private Spinner spnCurrency;
    private Button btnSave, btnReset;

    private PreferenceManager preferenceManager;
    private TransactionRepository transactionRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferenceManager = new PreferenceManager(this);
        transactionRepository = new TransactionRepository(this);

        // Bind views
        edtName = findViewById(R.id.edtName_settings);
        edtBudget = findViewById(R.id.edtBudget_settings);
        edtStartDay = findViewById(R.id.edtStartDay_settings);
        spnCurrency = findViewById(R.id.spnCurrency_settings);
        btnSave = findViewById(R.id.btnSave_settings);
        btnReset = findViewById(R.id.btnResetData);

        setupCurrencySpinner();
        loadSettings();

        btnSave.setOnClickListener(v -> saveSettings());
        btnReset.setOnClickListener(v -> showResetConfirmationDialog());
    }

    private void loadSettings() {
        edtName.setText(preferenceManager.getUserName());
        edtBudget.setText(String.valueOf(preferenceManager.getBudget()));
        edtStartDay.setText(String.valueOf(preferenceManager.getStartDay()));

        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spnCurrency.getAdapter();
        int position = adapter.getPosition(preferenceManager.getCurrency());
        spnCurrency.setSelection(position);
    }

    private void setupCurrencySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currencies_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCurrency.setAdapter(adapter);
    }

    private void saveSettings() {
        String name = edtName.getText().toString().trim();
        String budgetStr = edtBudget.getText().toString().trim();
        String startDayStr = edtStartDay.getText().toString().trim();
        String currency = spnCurrency.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(budgetStr) || TextUtils.isEmpty(startDayStr)) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        float budget = Float.parseFloat(budgetStr);
        int startDay = Integer.parseInt(startDayStr);

        if (startDay < 1 || startDay > 28) {
            Toast.makeText(this, "El día de inicio debe estar entre 1 y 28", Toast.LENGTH_SHORT).show();
            return;
        }

        preferenceManager.saveUserSettings(name, budget, currency, startDay);
        Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();
        finish(); // Go back to the previous screen
    }

    private void showResetConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Restablecer Datos")
                .setMessage("¿Estás seguro de que quieres borrar todas las transacciones? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí, borrar todo", (dialog, which) -> {
                    transactionRepository.deleteAllTransactions();
                    Toast.makeText(SettingsActivity.this, "Todos los datos han sido eliminados", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
