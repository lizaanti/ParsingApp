package com.example.parsingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.BaseExpandableListAdapter;

import java.util.List;
import java.util.Map;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> titles;
    private final Map<String, String> contentMap;

    public ExpandableListAdapter(Context context, List<String> titles, Map<String, String> contentMap) {
        this.context = context;
        this.titles = titles;
        this.contentMap = contentMap;
    }


    @Override
    public int getGroupCount() {
        return titles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1; // Один элемент для каждого заголовка
    }

    @Override
    public Object getGroup(int groupPosition) {
        return titles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return contentMap.get(titles.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String title = getGroup(groupPosition).toString(); // Здесь получаем главный заголовок
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(title);
        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String content = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_content, parent, false);
        }
        WebView webView = convertView.findViewById(R.id.webView);
        webView.loadDataWithBaseURL(null, "<html><body>" + content + "</body></html>", "text/html", "utf-8", null);
        return convertView;

    }



    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
