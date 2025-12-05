package com.example.balarezolinda_moneytracer.ui.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balarezolinda_moneytracer.R;
import com.example.balarezolinda_moneytracer.db.TransactionRepository;
import com.example.balarezolinda_moneytracer.model.Category;
import com.example.balarezolinda_moneytracer.model.Transaction;
import com.example.balarezolinda_moneytracer.util.PreferenceManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private TextView txtAverageDailyExpense;

    private TransactionRepository transactionRepository;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        pieChart = findViewById(R.id.pieChart);
        txtAverageDailyExpense = findViewById(R.id.txtAverageDailyExpense);

        transactionRepository = new TransactionRepository(this);
        preferenceManager = new PreferenceManager(this);

        loadStatistics();
    }

    private void loadStatistics() {
        // Get date range for the current month
        Calendar calendar = Calendar.getInstance();
        int startDay = preferenceManager.getStartDay();
        calendar.set(Calendar.DAY_OF_MONTH, startDay);
        long startDate = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        long endDate = calendar.getTimeInMillis();

        List<Transaction> transactions = transactionRepository.getTransactionsBetween(startDate, endDate);
        List<Category> categories = transactionRepository.getAllCategories();

        // Calculate expenses per category
        Map<Long, Double> expensesPerCategory = new HashMap<>();
        double totalExpenses = 0;
        for (Transaction t : transactions) {
            if (t.getType().equals("EXPENSE")) {
                totalExpenses += t.getAmount();
                expensesPerCategory.merge(t.getCategoryId(), t.getAmount(), Double::sum);
            }
        }

        // Setup Pie Chart
        setupPieChart(expensesPerCategory, categories);

        // Calculate and display average daily expense
        long diff = endDate - startDate;
        long days = diff / (1000 * 60 * 60 * 24);
        double averageDailyExpense = (days > 0) ? totalExpenses / days : 0;
        String currency = preferenceManager.getCurrency();
        txtAverageDailyExpense.setText(String.format("Gasto promedio diario: %s%.2f", currency, averageDailyExpense));
    }

    private void setupPieChart(Map<Long, Double> expenses, List<Category> categories) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : expenses.entrySet()) {
            String categoryName = "Desconocida";
            for (Category cat : categories) {
                if (cat.getId() == entry.getKey()) {
                    categoryName = cat.getName();
                    break;
                }
            }
            entries.add(new PieEntry(entry.getValue().floatValue(), categoryName));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Gastos por Categoría");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.animateY(1400);
        pieChart.invalidate(); // refresh
    }
}
