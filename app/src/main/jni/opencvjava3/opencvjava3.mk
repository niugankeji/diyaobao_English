LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libopencv_java3
LOCAL_SRC_FILES := libopencv_java3.so

include $(PREBUILT_SHARED_LIBRARY)
