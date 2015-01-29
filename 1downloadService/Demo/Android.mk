LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional 

LOCAL_STATIC_JAVA_LIBRARIES := \
            android-support-v4  \
            android-async-http-1.4.5 \
            universal-image-loader-1.9.1


LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := test_mm

LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := android-async-http-1.4.5:libs/android-async-http-1.4.5.jar
include $(BUILD_MULTI_PREBUILT)

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := universal-image-loader-1.9.1:libs/universal-image-loader-1.9.1.jar
include $(BUILD_MULTI_PREBUILT)



