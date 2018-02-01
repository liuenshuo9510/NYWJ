package com.nanyue.app.nywj.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.nanyue.app.nywj.R;
import com.nanyue.app.nywj.fragment.CourseFragment;
import com.nanyue.app.nywj.fragment.HomeFragment;
import com.nanyue.app.nywj.fragment.MomentFragment;
import com.nanyue.app.nywj.fragment.PersonalFragment;
import com.nanyue.app.nywj.utils.UpdateApk;

import java.util.ArrayList;

import cn.jzvd.JZVideoPlayer;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener{

    private ArrayList<Fragment> list = new ArrayList<>();
    private HomeFragment homeFragment;
    private CourseFragment courseFragment;
    private MomentFragment momentFragment;
    private PersonalFragment personalFragment;
    private long firstBackPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JZVideoPlayer.SAVE_PROGRESS = false;

        UpdateApk.checkUpdate(this);

        initBottomBar();
        onTabSelected(0);
    }

    private void initBottomBar() {
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .addItem(new BottomNavigationItem(R.drawable.bottombar_home, "首页")
                        .setActiveColor(Color.RED).setInActiveColorResource(R.color.fontColor))
                .addItem(new BottomNavigationItem(R.drawable.bottombar_course, "微课堂")
                        .setActiveColor(Color.RED).setInActiveColorResource(R.color.fontColor))
                .addItem(new BottomNavigationItem(R.drawable.bottombar_moment, "微互动")
                        .setActiveColor(Color.RED).setInActiveColorResource(R.color.fontColor))
                .addItem(new BottomNavigationItem(R.drawable.bottombar_personal, "个人中心")
                        .setActiveColor(Color.RED).setInActiveColorResource(R.color.fontColor))
                .initialise();
        bottomNavigationBar.setTabSelectedListener(this);
    }

    public void hideFragment(FragmentTransaction transaction) {
        for (Fragment fragment : list) {
            transaction.hide(fragment);
        }
    }

    @Override
    public void onTabSelected(int position) {
        JZVideoPlayer.releaseAllVideos();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragment(transaction);
        switch (position) {
            case 0:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    transaction.add(R.id.fragment, homeFragment);
                    list.add(homeFragment);
                } else {
                    transaction.show(homeFragment);
                }
                break;
            case 1:
                if (courseFragment == null) {
                    courseFragment = new CourseFragment();
                    transaction.add(R.id.fragment, courseFragment);
                    list.add(courseFragment);
                } else {
                    transaction.show(courseFragment);
                }
                break;
            case 2:
                if (momentFragment == null) {
                    momentFragment = new MomentFragment();
                    transaction.add(R.id.fragment, momentFragment);
                    list.add(momentFragment);
                } else {
                    transaction.show(momentFragment);
                }
                break;
            case 3:
                if (personalFragment == null) {
                    personalFragment = new PersonalFragment();
                    transaction.add(R.id.fragment, personalFragment);
                    list.add(personalFragment);
                } else {
                    transaction.show(personalFragment);
                }
                break;
        }
        transaction.commit();
    }

    @Override
    public void onTabReselected(int position) {}

    @Override
    public void onTabUnselected(int position) {}


    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        if (firstBackPressedTime == 0) {
            firstBackPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
        } else {
            if (System.currentTimeMillis() - firstBackPressedTime > 2000) {
                firstBackPressedTime = System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
