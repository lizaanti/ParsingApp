package com.example.parsingapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parsingapp.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText urlInput;
    private Button parseButton, saveButton, shareButton;
    private ListView listView;

    private SQLiteDatabase database;
    private List<String> parsedData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов UI
        urlInput = findViewById(R.id.urlInput);
        parseButton = findViewById(R.id.parseButton);
        saveButton = findViewById(R.id.saveButton);
        shareButton = findViewById(R.id.shareButton);
        listView = findViewById(R.id.listView);

        // Настройка базы данных
        database = openOrCreateDatabase("WebData.db", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS ParsedData (id INTEGER PRIMARY KEY, content TEXT)");

        parseButton.setOnClickListener(v -> {
            String url = urlInput.getText().toString();
            if (!url.isEmpty()) {
                new ParseWebsiteTask().execute(url);
            } else {
                Toast.makeText(this, "Введите ссылку", Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(v -> saveDataToDatabase());
        shareButton.setOnClickListener(v -> shareData());
        Button viewDataButton = findViewById(R.id.viewDataButton);
        viewDataButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewDataActivity.class);
            startActivity(intent);
        });

    }


    private class ParseWebsiteTask extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... urls) {
            String url = urls[0].trim();
            List<String> data = new ArrayList<>();

            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url; // Добавляем http:// если протокол отсутствует
            }

            try {
                Document document = Jsoup.connect(url).get();
                String title = document.title();
                String mainTitle = document.select("h1").first().text(); // Получение текста первого <h1>

                String text = document.body().text();
                data.add("Title: " + title);
                data.add("Content: " + text);
            } catch (IOException e) {
                data.add("Ошибка при парсинге: " + e.getMessage());
            }
            return data;
        }




        @Override
        protected void onPostExecute(List<String> data) {
            parsedData = data;

            // Отображение данных в ListView
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.simple_list_item_1, parsedData);
            listView.setAdapter(adapter);

            if (data.isEmpty()) {
                Toast.makeText(MainActivity.this, "Нет данных для отображения", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void saveDataToDatabase() {
        if (parsedData.isEmpty()) {
            Toast.makeText(this, "Нет данных для сохранения", Toast.LENGTH_SHORT).show();
            return;
        }
        for (String item : parsedData) {
            database.execSQL("INSERT INTO ParsedData (content) VALUES (?)", new Object[]{item});
        }
        Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
    }

    private void shareData() {
        if (parsedData.isEmpty()) {
            Toast.makeText(this, "Нет данных для отправки", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, String.join("\n", parsedData));
        startActivity(Intent.createChooser(shareIntent, "Отправить данные"));
    }
}
