package com.pebble.tricorder.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pebble.tricorder.R;
import com.pebble.tricorder.adapter.TricorderListAdapter;
import com.pebble.tricorder.model.TricorderData;

import java.util.ArrayList;


public class LogsFragment extends Fragment {

    private ArrayList<TricorderData> tricorderDatas;
    public static TricorderListAdapter tricorderListAdapter;

    public LogsFragment(ArrayList<TricorderData> tricorderDatas) {
        super();
        this.tricorderDatas = tricorderDatas;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.logs_page, container, false);

        tricorderListAdapter = new TricorderListAdapter(getActivity(), tricorderDatas);

        ListView logsListView = (ListView) inflated.findViewById(R.id.logs_list_view);

        logsListView.setAdapter(tricorderListAdapter);

        return inflated;
    }

    public void updateLogsListView() {
        tricorderListAdapter.notifyDataSetInvalidated();
    }
}