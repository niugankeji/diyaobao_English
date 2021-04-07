LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libgetuiext
LOCAL_SRC_FILES := libgetuiext.so

include $(PREBUILT_SHARED_LIBRARY)