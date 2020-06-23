package com.example.android.notepad;


import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 * 继承ListFragemnt对应底部栏第一个界面，实现listview的效果
 */
public class BlankFragment extends ListFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SHOW_TEXT = "text";
    Intent intent ;
    private static final String[] PROJECTION = new String[] {
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, //1
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE,
            NotePad.Notes.COLUMN_NAME_BACK_COLOR,
            NotePad.Notes.COLUMN_NAME_ALARM
    };
    private static final String TAG = "MainActivity";
    private ArrayAdapter<String> arr_adapter;
    /** The index of the title column */
    private static final int COLUMN_INDEX_TITLE = 1;

    public BlankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment BlankFragment.
     */
    public static BlankFragment newInstance(String param1) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SHOW_TEXT, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getActivity().getIntent();
        if (intent.getData() == null) {
            intent.setData(NotePad.Notes.CONTENT_URI);
        }
        getActivity().setDefaultKeyMode(Activity.DEFAULT_KEYS_SHORTCUT);
        Cursor cursor = getActivity().managedQuery(
                intent.getData(),            // Use the default content URI for the provider.
                PROJECTION,                       // Return the note ID and title for each note.
                null,                             // No where clause, return all records.
                null,                             // No where clause, therefore no where column values.
                NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
        );
        /*
         * The following two arrays create a "map" between columns in the cursor and view IDs
         * for items in the ListView. Each element in the dataColumns array represents
         * a column name; each element in the viewID array represents the ID of a View.
         * The AlarmCursorAdapter maps them in ascending order to determine where each column
         * value will appear in the ListView.
         */
        // The names of the cursor columns to display in the view, initialized to the title column
        String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE, NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE} ;
        // The view IDs that will display the cursor columns, initialized to the TextView in
        // noteslist_item.xml
        int[] viewIDs = { android.R.id.text1, R.id.date};

        // Creates the backing adapter for the ListView.
        AlarmCursorAdapter adapter
                = new AlarmCursorAdapter(
                getActivity(),                             // The Context for the ListView
                R.layout.noteslist_item,          // Points to the XML for a list item
                cursor,                           // The cursor to get items from
                dataColumns,
                viewIDs
        );
        // Sets the ListView's adapter to be the cursor adapter that was just created.
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);
        //数据
        List<String> data_list = new ArrayList<>();
        data_list.add("按创建时间排序");
        data_list.add("按修改时间排序");
        data_list.add("按颜色排序");
        Spinner final_spinner = (Spinner) rootView.findViewById(R.id.spinner1);
        //适配器 系统默认布局  android.R.layout.simple_spinner_item
        // 可以自定义
        arr_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, data_list);
        //设置默认样式 android.R.layout.simple_spinner_dropdown_item
        // 自定义样式与右边三角形一致
        arr_adapter.setDropDownViewResource(R.layout.select_sort);
        //加载适配器
        final_spinner.setAdapter(arr_adapter);
        //为下拉列表设置各种点击事件，以响应菜单中的文本item被选中了，用setOnItemSelectedListener
        //设置垂直偏移量 (以下属性均可布局里设置)
        final_spinner.setDropDownVerticalOffset(80);
        //设置水平偏移量
        final_spinner.setDropDownHorizontalOffset(80);
        //修改背景 (带圆角的背景)
        final_spinner.setPopupBackgroundResource(R.drawable.radius);
        //MODE_DROPDOWN 在控件下面显示   MODE_DIALOG:在中间显示
        final_spinner.setLayoutMode(Spinner.MODE_DROPDOWN);
        //里面item点击监听 默认选择第一个
        final_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //点击事件对应的epostion
                AlarmCursorAdapter adapter;
                 Cursor cursor;
                 String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE ,  NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE } ;
                 int[] viewIDs = { android.R.id.text1 , R.id.date };
               switch (position) {
                   case 0:
                       cursor = getActivity().managedQuery(
                               intent.getData(),
                               PROJECTION,
                               null,
                               null,
                               NotePad.Notes._ID
                       );
                        adapter
                               = new AlarmCursorAdapter(
                               getActivity(),                             // The Context for the ListView
                               R.layout.noteslist_item,          // Points to the XML for a list item
                               cursor,                           // The cursor to get items from
                               dataColumns,
                               viewIDs
                       );
                       setListAdapter(adapter);
                       break;
                   case 1:
                        cursor = getActivity().managedQuery(
                               intent.getData(),
                               PROJECTION,
                               null,
                               null,
                               NotePad.Notes.DEFAULT_SORT_ORDER
                       );
                        adapter
                               = new AlarmCursorAdapter(
                               getActivity(),                             // The Context for the ListView
                               R.layout.noteslist_item,          // Points to the XML for a list item
                               cursor,                           // The cursor to get items from
                               dataColumns,
                               viewIDs
                       );
                       setListAdapter(adapter);
                       break;
                   case 2:
                        cursor = getActivity().managedQuery(
                               intent.getData(),
                               PROJECTION,
                               null,
                               null,
                               NotePad.Notes.COLUMN_NAME_BACK_COLOR
                       );
                        adapter
                               = new AlarmCursorAdapter(
                               getActivity(),                             // The Context for the ListView
                               R.layout.noteslist_item,          // Points to the XML for a list item
                               cursor,                           // The cursor to get items from
                               dataColumns,
                               viewIDs
                       );
                       setListAdapter(adapter);
                       break;
               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return rootView;
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        // Constructs a new URI from the incoming URI and the row ID
        Uri uri = ContentUris.withAppendedId(intent.getData(), id);

        // Gets the action from the incoming Intent
        String action = intent.getAction();

        // Handles requests for note data
        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {

            // Sets the result to return to the component that called this Activity. The
            // result contains the new URI
            getActivity().setResult(getActivity().RESULT_OK, new Intent().setData(uri));
        } else {

            // Sends out an Intent to start an Activity that can handle ACTION_EDIT. The
            // Intent's data is the note ID URI. The effect is to call NoteEdit.
            startActivity(new Intent(Intent.ACTION_EDIT, uri));
        }
    }
}
