LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := liblocSDK6a
LOCAL_SRC_FILES := liblocSDK6a.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libBaiduMapSDK_base_v3_7_3
LOCAL_SRC_FILES := libBaiduMapSDK_base_v3_7_3.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libBaiduMapSDK_map_v3_7_3
LOCAL_SRC_FILES := libBaiduMapSDK_map_v3_7_3.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libBaiduMapSDK_util_v3_7_3
LOCAL_SRC_FILES := libBaiduMapSDK_util_v3_7_3.so
include $(PREBUILT_SHARED_LIBRARY)