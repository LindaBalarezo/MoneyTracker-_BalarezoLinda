package com.example.balarezolinda_moneytracer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // Nombre BD y versión
    public static final String DATABASE_NAME = "moneytracker.db";
    public static final int DATABASE_VERSION = 2;

    // Nombre de tablas
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String TABLE_CATEGORIES = "categories";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Tabla Categories
        db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "type TEXT NOT NULL," +         // INCOME o EXPENSE
                "icon TEXT," +
                "color TEXT" +
                ");"
        );

        // Tabla Transactions
        db.execSQL("CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "type TEXT NOT NULL," +         // INCOME o EXPENSE
                "amount REAL NOT NULL," +
                "category INTEGER NOT NULL," +
                "description TEXT," +
                "date INTEGER NOT NULL," +      // timestamp
                "payment_method TEXT," +
                "created_at INTEGER NOT NULL," +
                "FOREIGN KEY(category) REFERENCES " + TABLE_CATEGORIES + "(id)" +
                ");"
        );

        insertDefaultCategories(db);
    }

    // Inserta categorías iniciales por defecto
    private void insertDefaultCategories(SQLiteDatabase db) {

        // Categorías de gastos
        db.execSQL("INSERT INTO " + TABLE_CATEGORIES +
                " (name, type, icon, color) VALUES " +
                "('Alimentación', 'EXPENSE', 'ic_food', '#FF5722')," +
                "('Transporte', 'EXPENSE', 'ic_bus', '#3F51B5')," +
                "('Educación', 'EXPENSE', 'ic_school', '#9C27B0')," +
                "('Entretenimiento', 'EXPENSE', 'ic_movie', '#E91E63')," +
                "('Salud', 'EXPENSE', 'ic_health', '#4CAF50')," +
                "('Otros', 'EXPENSE', 'ic_other', '#9E9E9E');"
        );

        // Categorías de ingresos
        db.execSQL("INSERT INTO " + TABLE_CATEGORIES +
                " (name, type, icon, color) VALUES " +
                "('Salario', 'INCOME', 'ic_salary', '#2196F3')," +
                "('Freelance', 'INCOME', 'ic_work', '#009688')," +
                "('Beca', 'INCOME', 'ic_scholarship', '#8BC34A')," +
                "('Otros', 'INCOME', 'ic_other', '#9E9E9E');"
        );
    }

    // Manejo de actualización de versión de BD
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Si cambias algo en el futuro puedes modificar aquí
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);

        onCreate(db);
    }
}
