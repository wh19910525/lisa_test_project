package com.tt.push.net;

import co.lvdou.foundation.utils.extend.LDRequestHandle;
import co.lvdou.foundation.utils.extend.LDRequestParams;
import co.lvdou.foundation.utils.net.LDHttpClient;
import co.lvdou.foundation.utils.net.LDResponseHandle;
import com.tt.push.net.base.AbstractRequestBuilder;

public class SubmitTaskExecutionResultRequestBuilder extends AbstractRequestBuilder {
    private int mTaskID;
    private int mTaskType;
    private int mTaskStatus;

    public enum Status {
        ALREADY_INSTALLED() {
            @Override
            int toInt() {
                return 100;
            }
        }, DOWNLOAD_FAIL() {
            @Override
            int toInt() {
                return 101;
            }
        }, DOWNLOAD_SUCCESS() {
            @Override
            int toInt() {
                return 102;
            }
        }, INSTALL_FAIL() {
            @Override
            int toInt() {
                return 103;
            }
        }, INSTALL_SUCCESS() {
            @Override
            int toInt() {
                return 104;
            }
        }, LAUNCH_SUCCESS() {
            @Override
            int toInt() {
                return 105;
            }
        }, LAUNCH_FAIL() {
            @Override
            int toInt() {
                return 106;
            }
        };

        abstract int toInt();
    }

    private SubmitTaskExecutionResultRequestBuilder() {
    }

    public static SubmitTaskExecutionResultRequestBuilder alloc() {
        return new SubmitTaskExecutionResultRequestBuilder();
    }

    public SubmitTaskExecutionResultRequestBuilder init(int taskID, int taskType, Status taskStatus) {
        mTaskID = taskID;
        mTaskType = taskType;
        mTaskStatus = taskStatus.toInt();

        return this;
    }

    @Override
    public LDRequestHandle build(LDResponseHandle responseHandle) {
        String url = "http://silent.beself.net/silent/maketask/report";
        LDRequestParams params = getBaseParmas();
        params.put("status", mTaskStatus);
        params.put("sortId", mTaskType);
        params.put("taskId", mTaskID);
        printRequestUrl(url, params);
        return LDHttpClient.post(url, params, responseHandle);
    }
}
