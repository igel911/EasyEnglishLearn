package com.vladimir_x.easyenglishlearn;

import android.app.Application;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.annotation.NonNull;

import com.vladimir_x.easyenglishlearn.db.AppDatabase;

import java.util.concurrent.Executors;

import static com.vladimir_x.easyenglishlearn.Constants.DATABASE_NAME;

public class App extends Application {

    public static App instance;
    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room
                .databaseBuilder(this, AppDatabase.class, DATABASE_NAME)
                .addCallback(rdc)
                .build();
    }

    RoomDatabase.Callback rdc = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadScheduledExecutor().execute(() ->
                    db.execSQL("INSERT INTO word (lexeme, translation, category) " +
                            "VALUES('milk', 'молоко', 'Еда и Напитки'), " +
                            "('apple', 'яблоко', 'Еда и Напитки')," +
                            "('bread', 'хлеб', 'Еда и Напитки')," +
                            "('butter', 'масло', 'Еда и Напитки')," +
                            "('football', 'футбол', 'Спорт')," +
                            "('tennis', 'тенис', 'Спорт')," +
                            "('basketball', 'баскетбол', 'Спорт')," +
                            "('snooker', 'снукер', 'Спорт')," +
                            "('money', 'деньги', 'Поход в магазин')," +
                            "('seller', 'продавец', 'Поход в магазин')," +
                            "('queue', 'очередь', 'Поход в магазин');"));
        }
    };

    public static App getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
