package com.nanyue.app.nywj.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nanyue.app.nywj.R;
import com.nanyue.app.nywj.activity.MainActivity;
import com.nanyue.app.nywj.activity.PersonalFeedback;
import com.nanyue.app.nywj.activity.PersonalNameEdit;
import com.nanyue.app.nywj.activity.PersonalPasswordEdit;
import com.nanyue.app.nywj.activity.PersonalSignEdit;

import okhttp3.OkHttpClient;

public class PersonalFragment extends Fragment implements View.OnClickListener{

    private RelativeLayout changePassword, nickname, briefIntroduction, problemFeedback;
    private TextView briefIntroductionView, nicknameView;
    private SharedPreferences sharedPreferences;
    private OkHttpClient okHttpClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        initView(view);
        initClickListener();
        return view;
    }

    @Override
    public void onStart() {
        sharedPreferences = getActivity().getSharedPreferences("personal", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        if (!name.equals("")) {
            nicknameView.setText(name);
        }
        String sign = sharedPreferences.getString("sign", "");
        if (!sign.equals("")) {
            briefIntroductionView.setText(sign);
        }
        super.onStart();
    }

    private void initView(View view) {
        changePassword = (RelativeLayout) view.findViewById(R.id.change_password);
        nickname = (RelativeLayout) view.findViewById(R.id.nickname);
        briefIntroduction = (RelativeLayout) view.findViewById(R.id.brief_introduction);
        problemFeedback = (RelativeLayout) view.findViewById(R.id.problem_feedback);

        briefIntroductionView = (TextView) view.findViewById(R.id.brief_introduction_view);
        nicknameView = (TextView) view.findViewById(R.id.nickname_view);
    }

    private void initClickListener() {
        changePassword.setOnClickListener(this);
        nickname.setOnClickListener(this);
        briefIntroduction.setOnClickListener(this);
        problemFeedback.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_password:
                Intent intent = new Intent(getActivity(), PersonalPasswordEdit.class);
                getActivity().startActivity(intent);
                break;
            case R.id.nickname:
                Intent intent1 = new Intent(getActivity(), PersonalNameEdit.class);
                getActivity().startActivity(intent1);
                break;
            case R.id.brief_introduction:
                Intent intent2 = new Intent(getActivity(), PersonalSignEdit.class);
                getActivity().startActivity(intent2);
                break;
            case R.id.problem_feedback:
                Intent intent3 = new Intent(getActivity(), PersonalFeedback.class);
                getActivity().startActivity(intent3);
        }
    }
}
