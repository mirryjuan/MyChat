package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.utils.PreferencesUtil;
import com.example.mirry.chat.view.CircleImageView;
import com.example.mirry.chat.view.IconFontTextView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ContactInfoActivity extends Activity implements View.OnClickListener {

    @InjectView(R.id.head)
    CircleImageView head;
    @InjectView(R.id.nickname)
    EditText nickname;
    @InjectView(R.id.note)
    EditText note;
    @InjectView(R.id.info_phone)
    EditText phoneInfo;
    @InjectView(R.id.info_birthday)
    EditText birthdayInfo;
    @InjectView(R.id.delete)
    Button delete;
    @InjectView(R.id.phone)
    TextView phone;
    @InjectView(R.id.birthday)
    TextView birthday;
    @InjectView(R.id.back)
    IconFontTextView back;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.account)
    EditText account;
    @InjectView(R.id.chat)
    Button chat;
    private String curAccount;
    private String mNickname;
    private int mSex;
    private String mPhone;
    private String mBirthday;
    private String mAccount;
    private Boolean isMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_contact_info);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        curAccount = intent.getStringExtra("account");
        mAccount = PreferencesUtil.getString(ContactInfoActivity.this, "config", "account", "");
        if (mAccount.equals(curAccount)) {
            isMe = true;
        } else {
            isMe = false;
        }

        initData(isMe);

        delete.setOnClickListener(this);
        back.setOnClickListener(this);
        chat.setOnClickListener(this);
    }

    private void initData(Boolean isMe) {
        if (isMe) {
            title.setText("个人信息");
            delete.setVisibility(View.GONE);
            note.setVisibility(View.GONE);
            account.setVisibility(View.VISIBLE);
            chat.setVisibility(View.GONE);
        } else {
            title.setText("好友信息");
            note.setVisibility(View.VISIBLE);
            account.setVisibility(View.GONE);
            boolean isMyFriend = NIMClient.getService(FriendService.class).isMyFriend(curAccount);
            if(isMyFriend){
                delete.setVisibility(View.VISIBLE);
                chat.setVisibility(View.GONE);
            }else{
                delete.setVisibility(View.GONE);
                chat.setVisibility(View.VISIBLE);
            }
        }
        setVisibilities(isMe);
        NimUserInfo mInfo = NIMClient.getService(UserService.class).getUserInfo(curAccount);
        if (mInfo != null) {
            mNickname = mInfo.getName();
            mSex = mInfo.getGenderEnum().getValue();
            mPhone = mInfo.getMobile();
            mBirthday = mInfo.getBirthday();

            if (mNickname == null || mNickname.equals("")) {
                nickname.setText(curAccount);
            } else {
                nickname.setText(mNickname);
            }
            if (isMe) {
                phone.setVisibility(View.VISIBLE);
                birthday.setVisibility(View.VISIBLE);
                account.setText(mAccount);
                if (mPhone != null && !mPhone.equals("")) {
                    phoneInfo.setText(mPhone);
                }
                if (mBirthday != null && !mBirthday.equals("")) {
                    birthdayInfo.setText(mBirthday);
                }
            } else {
                if (mPhone == null || mPhone.equals("")) {
                    phone.setVisibility(View.GONE);
                    phoneInfo.setVisibility(View.GONE);
                } else {
                    phoneInfo.setText(mPhone);
                }
                if (mBirthday == null || mBirthday.equals("")) {
                    birthday.setVisibility(View.GONE);
                    birthdayInfo.setVisibility(View.GONE);
                } else {
                    birthdayInfo.setText(mBirthday);
                }
            }
        }
    }

    private void setVisibilities(Boolean isMe) {
        nickname.setFocusable(isMe);
        nickname.setFocusableInTouchMode(isMe);
        phoneInfo.setFocusable(isMe);
        phoneInfo.setFocusableInTouchMode(isMe);
        birthdayInfo.setFocusable(isMe);
        birthdayInfo.setFocusableInTouchMode(isMe);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete:
                NIMClient.getService(FriendService.class).deleteFriend(curAccount)
                        .setCallback(new RequestCallback<Void>() {

                            @Override
                            public void onSuccess(Void param) {
                                Toast.makeText(ContactInfoActivity.this, "删除好友成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailed(int code) {
                                Toast.makeText(ContactInfoActivity.this, "删除好友失败", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onException(Throwable exception) {
                                Toast.makeText(ContactInfoActivity.this, "系统异常，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case R.id.back:
                if (isMe) {
                    String curNick = nickname.getText().toString().trim();
                    String curPhone = phoneInfo.getText().toString().trim();
                    String curBirthday = birthdayInfo.getText().toString().trim();

                    Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
                    if (!curNick.equals(mNickname)) {
                        fields.put(UserInfoFieldEnum.Name, curNick);
                    }
                    if (!curPhone.equals(mPhone)) {
                        fields.put(UserInfoFieldEnum.MOBILE, curNick);
                    }
                    if (!curBirthday.equals(mBirthday)) {
                        fields.put(UserInfoFieldEnum.BIRTHDAY, curNick);
                    }

                    if (!fields.isEmpty()) {
                        NIMClient.getService(UserService.class).updateUserInfo(fields)
                                .setCallback(new RequestCallbackWrapper<Void>() {

                                    @Override
                                    public void onResult(int code, Void result, Throwable exception) {
                                        Toast.makeText(ContactInfoActivity.this, "用户信息设置完成", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                }
                finish();
                break;
            case R.id.chat:
                Intent intent = new Intent(ContactInfoActivity.this, ChatActivity.class);
                intent.putExtra("curAccount",curAccount);
                intent.putExtra("curUsername",mNickname);
                startActivity(intent);
                break;
        }
    }
}
