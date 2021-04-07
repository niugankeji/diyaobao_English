LOCAL_PATH := $(call my-dir)
MY_DIR := $(call my-dir)

include $(CLEAR_VARS)
include $(MY_DIR)/opencvSDK/native/jni/OpenCV.mk
LOCAL_MODULE    := SmartLAI
LOCAL_SRC_FILES := SmartLAI.cpp

include $(BUILD_SHARED_LIBRARY)

include $(MY_DIR)/opencvinfo/opencvinfo.mk
include $(MY_DIR)/opencvjava/opencvjava.mk
include $(MY_DIR)/opencvjava3/opencvjava3.mk
include $(MY_DIR)/BaiduLBS/BaiduLBS.mk
include $(MY_DIR)/getui/getui.mk

