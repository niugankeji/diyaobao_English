LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libopencv_info
LOCAL_SRC_FILES := libopencv_info.so

include $(PREBUILT_SHARED_LIBRARY)