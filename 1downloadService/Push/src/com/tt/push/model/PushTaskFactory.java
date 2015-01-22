package com.tt.push.model;

import co.lvdou.foundation.utils.extend.Logout;
import org.json.JSONObject;

public class PushTaskFactory {
    private PushTaskFactory() {
    }

    public static PushTask parseWithJson(JSONObject jsonMap) throws Exception {
        /**
         * 解析ret字段的值:1为有任务;101为没有任务;其它表示异常.
         */
        int code = jsonMap.getInt("ret");
        if (code == -101) {
            Logout.out("没有任务了");
            return null;
        }

        /**
         * 在有任务的情况下，根据任务类型解析不同的任务实体类.
         * 任务类型相关的字段为type:1为静默安装任务;2为通知栏任务
         */
        if (code == 1) {
            int type = jsonMap.getJSONObject("resultDate").getInt("type");
            if (type == 1) {
                Logout.out("检测到新的静默安装任务");
                return SliencePushTask.alloc().initWithJsonMap(jsonMap);
            }
            else if (type == 2) {
                Logout.out("检测到新的通知栏任务");
                return NotificationPushTask.alloc().initWithJsonMap(jsonMap);
            }
        }

        return null;
    }
}
