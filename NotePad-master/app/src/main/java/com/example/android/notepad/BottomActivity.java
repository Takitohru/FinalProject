package com.example.android.notepad;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
public class BottomActivity extends Activity {

    private RadioGroup mTabRadioGroup;
    private BlankFragment mFrag1=BlankFragment.newInstance("首页");
    private BlankFragment2 mFrag2=BlankFragment2.newInstance("搜索");
    private BlankFragment3 mFrag3=BlankFragment3.newInstance("首页备份");
    private BlankFragment4 mFrag4=BlankFragment4.newInstance("设置");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom);
        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(NotePad.Notes.CONTENT_URI);
        }
        initView();
    }
    private void hideFragments(FragmentTransaction transaction) {
        if (mFrag1 != null) {
            transaction.hide(mFrag1);
        }
        if (mFrag2 != null) {
            transaction.hide(mFrag2);
        }
        if (mFrag3 != null) {
            transaction.hide(mFrag3);
        }
        if (mFrag4 != null) {
            transaction.hide(mFrag4);
        }
    }
    private void initView() {
        mTabRadioGroup = findViewById(R.id.tabs_rg);
        mTabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 具体的fragment切换逻辑可以根据应用调整，例如使用show()/hide()
                //获取FragmentManager对象
                FragmentManager manager = getFragmentManager();
                //获取FragmentTransaction对象
                FragmentTransaction transaction = manager.beginTransaction();
                //先隐藏所有的Fragment
                hideFragments(transaction);
                switch (checkedId)
                {
                    case R.id.today_tab:
                        transaction.show(mFrag1);
                        break;
                    case R.id.record_tab:
                        transaction.show(mFrag2);
                        break;
                    case R.id.contact_tab:
                        transaction.show(mFrag3);
                        break;
                    case R.id.settings_tab:
                        transaction.show(mFrag4);
                        break;
                }
                transaction.commit();
            }
        });
        //获取FragmentManager对象
        FragmentManager manager = getFragmentManager();
        //获取FragmentTransaction对象
        FragmentTransaction transaction = manager.beginTransaction();
        // 默认显示第一个 添加进transaction然后隐藏其他三个，预处理
        transaction.add(R.id.fragment_container,mFrag1);
        transaction.add(R.id.fragment_container,mFrag2);
        transaction.add(R.id.fragment_container,mFrag3);
        transaction.add(R.id.fragment_container,mFrag4);
        transaction.hide(mFrag2);
        transaction.hide(mFrag3);
        transaction.hide(mFrag4);
        transaction.commit();
        //中间的加号监听,创建一个新笔记
        findViewById(R.id.sign_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
            }
        });
    }

}
