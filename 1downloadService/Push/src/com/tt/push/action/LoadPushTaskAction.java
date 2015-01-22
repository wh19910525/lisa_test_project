package com.tt.push.action;

import org.json.JSONObject;

import co.lvdou.foundation.action.LDAction;
import co.lvdou.foundation.action.LDActionDelegate;
import co.lvdou.foundation.action.LDNetworkAction;
import co.lvdou.foundation.utils.extend.Logout;
import co.lvdou.foundation.utils.net.LDResponseHandle;

import com.tt.push.model.PushTask;
import com.tt.push.model.PushTaskFactory;
import com.tt.push.net.LoadPushTaskRequestBuilder;

public class LoadPushTaskAction extends LDNetworkAction<LoadPushTaskAction.ActionDelegate> {

    private ActionDelegate mDelegate = ActionDelegate.NULL;

    private LoadPushTaskAction() {
    }

    public static LoadPushTaskAction alloc() {
        return new LoadPushTaskAction();
    }

    @Override
    protected void runCore() {
        cancelRequestHandle();
        mRequestHandle = LoadPushTaskRequestBuilder.alloc().build(new LDResponseHandle() {
            @Override
            public void onCallback(String content) {
                Logout.out(String.format("[response_data: %s]", content));
                try {
                    final JSONObject rootMap = new JSONObject(content);
                    PushTask task = PushTaskFactory.parseWithJson(rootMap);
                    if (task != null && task.isValid()) {
                        mDelegate.onFinishLoadPushTask(task);
                    } else {
                        onFail();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFail();
                }

                dispatchOnExecuteCompleteEvent();
            }

            @Override
            public void onFail() {
                mDelegate.onFinishLoadPushTask(null);
                dispatchOnExecuteCompleteEvent();
            }
        });
    }

    @Override
    public LDAction setDelegate(ActionDelegate delegate) {

        if (delegate == null)
            mDelegate = ActionDelegate.NULL;
        else
            mDelegate = delegate;

        return this;
    }

    public static interface ActionDelegate extends LDActionDelegate {
        public static ActionDelegate NULL = new ActionDelegate() {

            @Override
            public void onFinishLoadPushTask(PushTask task) {
            }
        };

        void onFinishLoadPushTask(PushTask task);
    }
}
