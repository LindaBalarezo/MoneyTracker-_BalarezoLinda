package com.example.balarezolinda_moneytracer.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balarezolinda_moneytracer.R;
import com.example.balarezolinda_moneytracer.ui.dashboard.DashboardActivity;
import com.example.balarezolinda_moneytracer.util.PreferenceManager;

public class OnboardingActivity extends AppCompatActivity {

    private EditText edtName, edtBudget, edtStartDay;
    private Spinner spnCurrency;
    private Button btnStart;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        preferenceManager = new PreferenceManager(this);

        // Si el onboarding ya se completó, ir a la pantalla principal
        if (preferenceManager.isOnboardingComplete()) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
            return;
        }

        edtName = findViewById(R.id.edtName);
        edtBudget = findViewById(R.id.edtBudget);
        edtStartDay = findViewById(R.id.edtStartDay);
        spnCurrency = findViewById(R.id.spnCurrency);
        btnStart = findViewById(R.id.btnStart);

        setupCurrencySpinner();

        btnStart.setOnClickListener(v -> saveSettings());
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
        preferenceManager.setOnboardingComplete(true);

        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }
}
