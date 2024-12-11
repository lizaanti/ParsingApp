package com.example.parsingapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText urlInput;
    private Button parseButton, saveButton, shareButton, viewDataButton;
    private ListView listView;
    private ArrayAdapter<String> listAdapter;
    private List<String> parsedData = new ArrayList<>();
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlInput = findViewById(R.id.urlInput);
        parseButton = findViewById(R.id.parseButton);
        saveButton = findViewById(R.id.saveButton);
        shareButton = findViewById(R.id.shareButton);
        viewDataButton = findViewById(R.id.viewDataButton);
        listView = findViewById(R.id.listView);

        dbHelper = new DatabaseHelper(this);

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, parsedData);
        listView.setAdapter(listAdapter);

        parseButton.setOnClickListener(v -> {
            String url = urlInput.getText().toString();
            if (!url.isEmpty()) {
                parseWebsite(url);
            } else {
                Toast.makeText(this, "Введите URL", Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(v -> {
            if (!parsedData.isEmpty()) {
                for (String content : parsedData) {
                    // Генерация заголовка из первых символов содержимого
                    String title = content.length() > 50 ? content.substring(0, 50) + "..." : content;
                    dbHelper.saveData(title, content);
                }
                Toast.makeText(this, "Данные успешно сохранены", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Нет данных для сохранения", Toast.LENGTH_SHORT).show();
            }
        });


        List<Map<String, String>> savedData = dbHelper.getSavedData();
        for (Map<String, String> item : savedData) {
            Log.d("SavedData", "Заголовок: " + item.get("title") + ", Контент: " + item.get("content"));
        }

            shareButton.setOnClickListener(v -> {
                if (!parsedData.isEmpty()) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, String.join("\n", parsedData));
                    startActivity(Intent.createChooser(shareIntent, "Поделиться через"));
                } else {
                    Toast.makeText(this, "Нет данных для отправки", Toast.LENGTH_SHORT).show();
                }
            });

            viewDataButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ViewDataActivity.class);
                startActivity(intent);
            });
        }

    private void parseWebsite(String url) {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(url).get();

                doc.select("script, style, .ad, .comment").remove();
                String title = doc.title();
                Elements paragraphs = doc.select("p");
                parsedData.clear();
                parsedData.add("<b>" + title + "</b>");
                for (Element p : paragraphs) {
                    parsedData.add(p.text());
                }

                runOnUiThread(() -> listAdapter.notifyDataSetChanged());

            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}

