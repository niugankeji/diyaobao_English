LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libopencv_java
LOCAL_SRC_FILES := libopencv_java.so

include $(PREBUILT_SHARED_LIBRARY)
