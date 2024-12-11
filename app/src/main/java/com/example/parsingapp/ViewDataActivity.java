package com.example.parsingapp;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ViewDataActivity extends AppCompatActivity {

    private EditText searchInput;
    private Button searchButton;
    private ExpandableListView listView;
    private DatabaseHelper dbHelper;
    private ExpandableListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);

        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.searchButton);
        listView = findViewById(R.id.expandableListView);
        dbHelper = new DatabaseHelper(this);

        loadSavedData();

        searchButton.setOnClickListener(v -> {
            String keyword = searchInput.getText().toString().trim();
            if (!keyword.isEmpty()) {
                filterDataByKeyword(keyword);
            } else {
                Toast.makeText(this, "Введите ключевое слово", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSavedData() {
        List<String> savedData = dbHelper.getAllData();
        adapter = new ExpandableListAdapter(this, savedData);
        listView.setAdapter(adapter);
    }

    private void filterDataByKeyword(String keyword) {
        List<String> filteredData = dbHelper.searchData(keyword);
        if (filteredData.isEmpty()) {
            Toast.makeText(this, "Нет данных по запросу", Toast.LENGTH_SHORT).show();
        } else {
            adapter = new ExpandableListAdapter(this, filteredData);
            listView.setAdapter(adapter);
        }
    }
}
