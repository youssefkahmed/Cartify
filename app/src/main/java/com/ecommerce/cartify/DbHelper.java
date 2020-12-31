package com.ecommerce.cartify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.ecommerce.cartify.Helpers.GmailSender;
import com.ecommerce.cartify.Models.Customer;

import java.util.Date;

public class DbHelper extends SQLiteOpenHelper {

    private static String dbName = "CartifyDB";
    SQLiteDatabase cartifyDB;

    public DbHelper(@Nullable Context context) {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String custTable = "CREATE TABLE customers(cust_id INTEGER PRIMARY KEY, " +
                           "cust_name TEXT NOT NULL, username TEXT NOT NULL, " +
                           "email TEXT NOT NULL, password TEXT NOT NULL, " +
                           "gender TEXT NOT NULL, birth_date TEXT NOT NULL, " +
                           "job TEXT NOT NULL)";

        String catTable = "CREATE TABLE categories(cat_id INTEGER PRIMARY KEY, " +
                          "cat_name TEXT NOT NULL)";

        String ordTable = "CREATE TABLE orders(ord_id INTEGER PRIMARY KEY, ord_date DATE NOT NULL, " +
                          "cust_id INTEGER NOT NULL, address TEXT NOT NULL, " +
                          "FOREIGN KEY(cust_id) REFERENCES customers(cust_id))";

        String prodTable = "CREATE TABLE products(prod_id INTEGER PRIMARY KEY, prod_name TEXT NOT NULL, " +
                           "price INTEGER NOT NULL, quantity INTEGER NOT NULL, cat_id INTEGER NOT NULL, " +
                           "FOREIGN KEY(cat_id) REFERENCES categories(cat_id))";

        String ordDetTable = "CREATE TABLE order_details(ord_id INTEGER NOT NULL, prod_id INTEGER NOT NULL, " +
                             "quantity INTEGER NOT NULL, PRIMARY KEY(ord_id, prod_id), FOREIGN KEY(ord_id) " +
                             "REFERENCES orders(ord_id), FOREIGN KEY(prod_id) REFERENCES products(prod_id))";

        db.execSQL(custTable);
        db.execSQL(catTable);
        db.execSQL(ordTable);
        db.execSQL(prodTable);
        db.execSQL(ordDetTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS order_details");
        db.execSQL("DROP TABLE IF EXISTS  orders");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS customers");
        db.execSQL("DROP TABLE IF EXISTS categories");

        onCreate(db);
    }

    public void registerCustomer(Customer customer){
        ContentValues row = new ContentValues();
        row.put("cust_name", customer.getName());
        row.put("username", customer.getUsername());
        row.put("email", customer.getEmail());
        row.put("password", customer.getPassword());
        row.put("gender", customer.getGender());
        row.put("birth_date", customer.getBirthDate());
        row.put("job", customer.getJob());

        cartifyDB = getWritableDatabase();
        cartifyDB.insert("customers", null, row);
        cartifyDB.close();
    }

    public boolean checkLogin(String emailOrUsername, String password){

        boolean result = true;
        Cursor customer;

        cartifyDB = getReadableDatabase();
        if(emailOrUsername.contains("@"))
            customer = cartifyDB.rawQuery("SELECT * FROM customers WHERE email=? AND password=?",
                    new String[]{emailOrUsername, password});
        else
            customer = cartifyDB.rawQuery("SELECT * FROM customers WHERE username=? AND password=?",
                    new String[]{emailOrUsername, password});

        if(customer.getCount() == 0)
            result = false;

        customer.close();
        cartifyDB.close();
        return result;
    }

    public boolean resetPassword(String custEmail){
        // Check Email validity
        cartifyDB = getReadableDatabase();
        Cursor password = cartifyDB.rawQuery("SELECT password FROM customers WHERE email=?", new String[]{custEmail});

        password.moveToFirst();
        if(password.getCount() == 0){
            password.close();
            cartifyDB.close();
            return false;
        }

        // Send Email
        String emailSender = "cartify.ecommerce.app@gmail.com";
        String emailPassword = "Cartifyapp";
        String emailSubject = "Cartify Password";
        String emailBody = "Your password is: " + password.getString(0);
        try {
            GmailSender sender = new GmailSender(emailSender, emailPassword);
            sender.sendMail(
                    emailSubject,
                    emailBody,
                    emailSender,
                    custEmail);
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }

        password.close();
        cartifyDB.close();
        return true;
    }

}
