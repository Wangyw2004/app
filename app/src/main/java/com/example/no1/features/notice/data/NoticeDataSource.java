package com.example.no1.features.notice.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.no1.features.notice.models.Notice;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NoticeDataSource {
    private static final String PREF_NAME = "notice_prefs";
    private static final String KEY_NOTICES = "notices_list";

    private SharedPreferences sharedPreferences;
    private Gson gson;
    private static NoticeDataSource instance;

    private NoticeDataSource(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized NoticeDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new NoticeDataSource(context);
        }
        return instance;
    }

    public void saveNotices(List<Notice> notices) {
        String json = gson.toJson(notices);
        sharedPreferences.edit().putString(KEY_NOTICES, json).apply();
    }

    public List<Notice> loadNotices() {
        String json = sharedPreferences.getString(KEY_NOTICES, "");
        if (json.isEmpty()) {
            return getMockNotices();
        }
        Type type = new TypeToken<List<Notice>>(){}.getType();
        List<Notice> notices = gson.fromJson(json, type);
        return notices != null ? notices : getMockNotices();
    }

    private List<Notice> getMockNotices() {
        List<Notice> notices = new ArrayList<>();

        Notice notice1 = new Notice("1", "关于小区电梯维修的公告",
                "为确保业主出行安全，将于本周六上午9:00-12:00对小区所有电梯进行维修保养，届时电梯将暂停使用，请各位业主提前做好准备。",
                "系统管理员", "system");
        notice1.setViewCount(156);
        notices.add(notice1);

        Notice notice2 = new Notice("2", "春节期间物业值班安排",
                "春节期间物业服务中心正常值班，24小时服务热线：XXXX-XXXXXXX。祝各位业主春节快乐！",
                "系统管理员", "system");
        notice2.setViewCount(89);
        notices.add(notice2);

        Notice notice3 = new Notice("3", "垃圾分类温馨提示",
                "请各位业主按照分类要求投放垃圾，共同维护小区环境。",
                "系统管理员", "system");
        notice3.setViewCount(245);
        notices.add(notice3);

        return notices;
    }

    public void addNotice(Notice notice) {
        List<Notice> notices = loadNotices();
        notices.add(0, notice);
        saveNotices(notices);
    }

    public void updateNotice(Notice notice) {
        List<Notice> notices = loadNotices();
        for (int i = 0; i < notices.size(); i++) {
            if (notices.get(i).getId().equals(notice.getId())) {
                notices.set(i, notice);
                break;
            }
        }
        saveNotices(notices);
    }

    public boolean deleteNotice(String noticeId) {
        List<Notice> notices = loadNotices();
        for (int i = 0; i < notices.size(); i++) {
            if (notices.get(i).getId().equals(noticeId)) {
                notices.remove(i);
                saveNotices(notices);
                return true;
            }
        }
        return false;
    }

    public Notice getNoticeById(String id) {
        List<Notice> notices = loadNotices();
        for (Notice notice : notices) {
            if (notice.getId().equals(id)) {
                return notice;
            }
        }
        return null;
    }
}