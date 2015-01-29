package com.tt.push.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import co.lvdou.foundation.utils.extend.LDContextHelper;
import co.lvdou.foundation.utils.extend.Logout;

import com.tt.push.db.TaskInfoDBHelper;
import com.tt.push.demo.R;
import com.tt.push.model.InstalledApkInfo;
import com.tt.push.service.PushPluginManager;
import com.tt.push.util.ShellManager;

public class ActDemo extends Activity implements View.OnClickListener {

    private View mLaunchButton;
    private View mRootButton;
    private View mInsertButton;
    private View mQueryButton;
    private View mDeleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LDContextHelper.init(this);
        setContentView(R.layout.act_main);
        initViews();
    }

    @Override
    public void onClick(View v) {
        if (v == mLaunchButton) {
            onLaunchButtonClick();
        } else if (v == mRootButton) {
            onRootButtonClick();
        } else if (v == mInsertButton) {
            onInsertButtonClick();
        } else if (v == mQueryButton) {
            onQueryButtonClick();
        } else if (v == mDeleteButton) {
            onDeleteButtonClick();
        }
    }

    //View Event Callback

    private void onLaunchButtonClick() {
        PushPluginManager.start(this);
    }

    private void onRootButtonClick() {
        ShellManager tm = ShellManager.shareManager();
        if (tm.isObtainRootPermission()) {
            Logout.out("已经获取了ROOT权限");
        } else {
            Logout.out("没有获取ROOT权限");
        }
    }

    private void onInsertButtonClick() {
        InstalledApkInfo info = new InstalledApkInfo().init(1, 2, "com.tt.push.demo");
        TaskInfoDBHelper.shareHelper().insertInstalledApkInfo(info);
//        Intent intent = new Intent(this, PushService.class);
//        intent.setAction(PushService.ACTION_LOAD_PUSH_TASK);
//        startService(intent);
    }

    private void onQueryButtonClick() {
        InstalledApkInfo apkInfo = TaskInfoDBHelper.shareHelper().queryInstalledApkInfo("com.tt.push.demo");
        if (apkInfo != null) {
            Logout.out(String.format("[InstalledApkInfo: [task_id: %d][task_type: %d][package_name: %s]]",
                    apkInfo.taskID(), apkInfo.taskType(), apkInfo.packageName()));
        }
    }

    private void onDeleteButtonClick() {
        TaskInfoDBHelper.shareHelper().deleteInstalledApkInfo("com.tt.push.demo");
    }

    //end

    private void initViews() {
        mLaunchButton = findViewById(R.id.btn_launch);
        mLaunchButton.setOnClickListener(this);

        mRootButton = findViewById(R.id.btn_root);
        mRootButton.setOnClickListener(this);

        mInsertButton = findViewById(R.id.btn_insert);
        mInsertButton.setOnClickListener(this);

        mQueryButton = findViewById(R.id.btn_query);
        mQueryButton.setOnClickListener(this);

        mDeleteButton = findViewById(R.id.btn_delete);
        mDeleteButton.setOnClickListener(this);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
