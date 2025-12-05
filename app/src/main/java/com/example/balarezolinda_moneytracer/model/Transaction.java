package com.example.balarezolinda_moneytracer.model;

public class Transaction {

    private long id;
    private String type;            // Income / Expense
    private double amount;
    private long categoryId;        // category (FK)
    private String description;     // requerido por Repository
    private long date;              // timestamp
    private String paymentMethod;   // efectivo, tarjeta, etc.
    private long createdAt;         // fecha de creación

    public Transaction() {}

    public Transaction(long id, String type, double amount,
                       long categoryId, String description,
                       long date, String paymentMethod,
                       long createdAt) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.categoryId = categoryId;
        this.description = description;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
    }

    // ===== GETTERS =====
    public long getId() { return id; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public long getCategoryId() { return categoryId; }
    public String getDescription() { return description; }
    public long getDate() { return date; }
    public String getPaymentMethod() { return paymentMethod; }
    public long getCreatedAt() { return createdAt; }

    // ===== SETTERS =====
    public void setId(long id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(long date) { this.date = date; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
