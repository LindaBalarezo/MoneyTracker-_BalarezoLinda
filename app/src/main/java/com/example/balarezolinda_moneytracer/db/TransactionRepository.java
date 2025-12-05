package com.example.balarezolinda_moneytracer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.example.balarezolinda_moneytracer.model.Category;
import com.example.balarezolinda_moneytracer.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {

    private DBHelper dbHelper;

    public TransactionRepository(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long insert(Transaction t) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("type", t.getType());
        values.put("amount", t.getAmount());
        values.put("category", t.getCategoryId());
        values.put("description", t.getDescription());
        values.put("date", t.getDate());
        values.put("payment_method", t.getPaymentMethod());
        values.put("created_at", t.getCreatedAt());

        long id = db.insert(DBHelper.TABLE_TRANSACTIONS, null, values);
        db.close();
        return id;
    }

    public int update(Transaction t) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("type", t.getType());
        values.put("amount", t.getAmount());
        values.put("category", t.getCategoryId());
        values.put("description", t.getDescription());
        values.put("date", t.getDate());
        values.put("payment_method", t.getPaymentMethod());

        int rows = db.update(DBHelper.TABLE_TRANSACTIONS, values, "id = ?", new String[]{String.valueOf(t.getId())});
        db.close();
        return rows;
    }

    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DBHelper.TABLE_TRANSACTIONS, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public Transaction getById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_TRANSACTIONS, null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Transaction transaction = cursorToTransaction(cursor);
            cursor.close();
            db.close();
            return transaction;
        }

        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    public List<Transaction> getTransactions(String filterType, Category filterCategory) {
        if (TextUtils.isEmpty(filterType) && filterCategory == null) {
            return getAll();
        }
        return getFiltered(filterType, filterCategory != null ? filterCategory.getId() : -1);
    }

    public List<Transaction> getAll() {
        return getFiltered(null, -1);
    }

    private List<Transaction> getFiltered(String type, long categoryId) {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        StringBuilder query = new StringBuilder("SELECT * FROM " + DBHelper.TABLE_TRANSACTIONS);
        List<String> selectionArgs = new ArrayList<>();
        boolean hasWhere = false;

        if (!TextUtils.isEmpty(type) && !type.equals("ALL")) {
            query.append(" WHERE type = ?");
            selectionArgs.add(type);
            hasWhere = true;
        }

        if (categoryId != -1) {
            query.append(hasWhere ? " AND" : " WHERE").append(" category = ?");
            selectionArgs.add(String.valueOf(categoryId));
        }

        query.append(" ORDER BY date DESC");

        Cursor cursor = db.rawQuery(query.toString(), selectionArgs.toArray(new String[0]));

        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToTransaction(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public List<Transaction> getTransactionsBetween(long startDate, long endDate) {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DBHelper.TABLE_TRANSACTIONS + " WHERE date >= ? AND date <= ? ORDER BY date DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(startDate), String.valueOf(endDate)});

        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToTransaction(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public void deleteAllTransactions() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_TRANSACTIONS, null, null);
        db.close();
    }

    public void resetDatabase() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.onUpgrade(db, 1, 2); // Esto borra y recrea las tablas
    }

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_CATEGORIES, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToCategory(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public double getTotalByType(String type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM " + DBHelper.TABLE_TRANSACTIONS + " WHERE type = ?", new String[]{type});

        double total = 0;
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        db.close();
        return total;
    }

    private Transaction cursorToTransaction(Cursor c) {
        Transaction t = new Transaction();
        t.setId(c.getLong(c.getColumnIndexOrThrow("id")));
        t.setType(c.getString(c.getColumnIndexOrThrow("type")));
        t.setAmount(c.getDouble(c.getColumnIndexOrThrow("amount")));
        t.setCategoryId(c.getLong(c.getColumnIndexOrThrow("category")));
        t.setDescription(c.getString(c.getColumnIndexOrThrow("description")));
        t.setDate(c.getLong(c.getColumnIndexOrThrow("date")));
        t.setPaymentMethod(c.getString(c.getColumnIndexOrThrow("payment_method")));
        t.setCreatedAt(c.getLong(c.getColumnIndexOrThrow("created_at")));
        return t;
    }

    private Category cursorToCategory(Cursor c) {
        return new Category(
                c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getString(c.getColumnIndexOrThrow("type"))
        );
    }
}
