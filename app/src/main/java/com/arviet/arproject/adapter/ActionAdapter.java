package com.arviet.arproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arviet.arproject.R;
import com.arviet.arproject.model.Action;

import java.util.List;

public class ActionAdapter extends BaseAdapter {
    Context myContext;
    int myLayout;
    List<Action> actionList;

    public ActionAdapter(Context myContext, int myLayout, List<Action> actionList) {
        this.myContext = myContext;
        this.myLayout = myLayout;
        this.actionList = actionList;
    }

    @Override
    public int getCount() {
        return actionList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(myLayout, null);
        TextView txvAction = (TextView) convertView.findViewById(R.id.txv_actionName);
        String actionName = String.valueOf(actionList.get(position).getName());
        txvAction.setText(actionName.toUpperCase());
        return convertView;
    }
}
