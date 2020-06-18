package com.example.android.notepad;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.Toast;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 * 对应着底部栏第三个界面,实现备份
 */
public class BlankFragment3 extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SHOW_TEXT = "text";

    private String mContentText;

    public BlankFragment3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment BlankFragment.
     */
    public static BlankFragment3 newInstance(String param1) {
        BlankFragment3 fragment = new BlankFragment3();
        Bundle args = new Bundle();
        args.putString(ARG_SHOW_TEXT, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mContentText = getArguments().getString(ARG_SHOW_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_blank3, container, false);
        Button backupButton = rootView.findViewById(R.id.backupButton);
        Button restoreButton = rootView.findViewById(R.id.restoreButton);

        //设置点击后记录日志和Toast提示
        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                createBackupDialog();
                Log.i("blank3", "backupButton onClick");
                //Toast.makeText(getActivity(),"备份",Toast.LENGTH_LONG).show();
            }
        });
        restoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                createRestoreDialog();
                Log.i("blank3", "restoreButton onClick");
                //Toast.makeText(getActivity(),"还原",Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }


    public void createBackupDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());

        builder.setTitle("您确定要备份嘛？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // 确定备份
                Toast.makeText(getActivity(),"确定",Toast.LENGTH_LONG).show();

                BackupTask backupTask = new BackupTask(getActivity());
                String backup_version = backupTask.doInBackground("backupDatabase");
                Toast.makeText(getActivity(),"备份时间："+backup_version,Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getActivity(),"取消",Toast.LENGTH_LONG).show();
            }
        });
        builder.create();//创建对话框
        builder.show();//显示对话框
    }


    public void createRestoreDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());

        builder.setTitle("您确定要还原嘛？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // 确定备份
                Toast.makeText(getActivity(),"确定",Toast.LENGTH_LONG).show();

                BackupTask backupTask = new BackupTask(getActivity());
                String backup_version = backupTask.doInBackground("restoreDatabase");
                Toast.makeText(getActivity(),"上次备份时间："+backup_version,Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getActivity(), BottomActivity.class);
                startActivity(intent);

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getActivity(),"取消",Toast.LENGTH_LONG).show();
            }
        });
        builder.create();//创建对话框
        builder.show();//显示对话框
    }


}