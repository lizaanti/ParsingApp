package com.example.parsingapp;

import com.example.parsingapp.ExpandableListAdapter;
import android.os.Bundle;
import android.widget.BaseExpandableListAdapter;

import android.widget.ExpandableListView;
import android.widget.Toast;



import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewDataActivity extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private DatabaseHelper databaseHelper;
    private List<String> titles;
    private Map<String, String> contentMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);

        expandableListView = findViewById(R.id.expandableListView);
        databaseHelper = new DatabaseHelper(this);

        // Загружаем данные из базы
        loadData();

        // Настраиваем адаптер
        ExpandableListAdapter adapter = new ExpandableListAdapter(ViewDataActivity.this, titles, contentMap);

        expandableListView.setAdapter(adapter);
    }
    private void searchData(String keyword) {
        List<String> results = databaseHelper.searchData(keyword);
        titles.clear();
        contentMap.clear();

        for (String result : results) {

            String highlightedResult = result.replaceAll("(?i)(" + keyword + ")", "<b>$1</b>");
            String[] splitData = highlightedResult.split("\n", 2);
            String title = splitData[0];
            String content = splitData.length > 1 ? splitData[1] : "Нет содержимого";

            titles.add(title);
            contentMap.put(title, content);
        }

        ((BaseExpandableListAdapter) expandableListView.getExpandableListAdapter()).notifyDataSetChanged();

        if (titles.isEmpty()) {
            Toast.makeText(this, "Ничего не найдено", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadData() {
        List<String> allData = databaseHelper.getAllData();
        titles = new ArrayList<>();
        contentMap = new HashMap<>();

        for (String item : allData) {
            String[] splitData = item.split("\n", 2);
            String title = splitData[0]; // Первый абзац как заголовок
            String content = splitData.length > 1 ? splitData[1] : "Нет содержимого";

            titles.add(title);
            contentMap.put(title, content);
        }

        if (titles.isEmpty()) {
            Toast.makeText(this, "Нет сохранённых данных", Toast.LENGTH_SHORT).show();
        }
    }
}
