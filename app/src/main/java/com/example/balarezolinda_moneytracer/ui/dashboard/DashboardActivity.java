package com.example.balarezolinda_moneytracer.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balarezolinda_moneytracer.R;
import com.example.balarezolinda_moneytracer.db.TransactionRepository;
import com.example.balarezolinda_moneytracer.model.Transaction;
import com.example.balarezolinda_moneytracer.ui.settings.SettingsActivity;
import com.example.balarezolinda_moneytracer.ui.statistics.StatisticsActivity;
import com.example.balarezolinda_moneytracer.ui.transactions.TransactionsActivity;
import com.example.balarezolinda_moneytracer.util.PreferenceManager;

import java.util.Calendar;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private TextView txtWelcome, txtBudgetSummary, txtBudgetAlert, txtIncome, txtExpenses, txtBalance;
    private ProgressBar progressBudget;
    private Button btnViewTransactions, btnViewStatistics;
    private ImageButton btnSettings;

    private PreferenceManager preferenceManager;
    private TransactionRepository transactionRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        preferenceManager = new PreferenceManager(this);
        transactionRepository = new TransactionRepository(this);

        // Bind views
        txtWelcome = findViewById(R.id.txtWelcome);
        txtBudgetSummary = findViewById(R.id.txtBudgetSummary);
        txtBudgetAlert = findViewById(R.id.txtBudgetAlert);
        txtIncome = findViewById(R.id.txtIncome);
        txtExpenses = findViewById(R.id.txtExpenses);
        txtBalance = findViewById(R.id.txtBalance);
        progressBudget = findViewById(R.id.progressBudget);
        btnViewTransactions = findViewById(R.id.btnViewTransactions);
        btnViewStatistics = findViewById(R.id.btnViewStatistics);
        btnSettings = findViewById(R.id.btnSettings);

        btnViewTransactions.setOnClickListener(v -> 
            startActivity(new Intent(this, TransactionsActivity.class)));
        
        btnViewStatistics.setOnClickListener(v -> 
            startActivity(new Intent(this, StatisticsActivity.class)));

        btnSettings.setOnClickListener(v -> 
            startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        // Welcome message
        String userName = preferenceManager.getUserName();
        txtWelcome.setText("Hola, " + userName);

        // Get date range for the current month
        Calendar calendar = Calendar.getInstance();
        int startDay = preferenceManager.getStartDay();
        calendar.set(Calendar.DAY_OF_MONTH, startDay);
        long startDate = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        long endDate = calendar.getTimeInMillis();

        // Get transactions for the period
        List<Transaction> transactions = transactionRepository.getTransactionsBetween(startDate, endDate);

        // Calculate totals
        double totalIncome = 0;
        double totalExpenses = 0;
        for (Transaction t : transactions) {
            if (t.getType().equals("INCOME")) {
                totalIncome += t.getAmount();
            } else {
                totalExpenses += t.getAmount();
            }
        }

        double balance = totalIncome - totalExpenses;
        float budget = preferenceManager.getBudget();
        String currency = preferenceManager.getCurrency();

        // Display summaries
        txtIncome.setText(String.format("Ingresos: %s%.2f", currency, totalIncome));
        txtExpenses.setText(String.format("Gastos: %s%.2f", currency, totalExpenses));
        txtBalance.setText(String.format("Balance: %s%.2f", currency, balance));

        // Budget progress
        if (budget > 0) {
            int progress = (int) ((totalExpenses / budget) * 100);
            progressBudget.setProgress(progress);
            txtBudgetSummary.setText(String.format("%s%.2f de %s%.2f", currency, totalExpenses, currency, budget));

            if (progress > 80) {
                txtBudgetAlert.setVisibility(View.VISIBLE);
            } else {
                txtBudgetAlert.setVisibility(View.GONE);
            }
        } else {
            progressBudget.setProgress(0);
            txtBudgetSummary.setText("No has establecido un presupuesto.");
        }
    }
}
