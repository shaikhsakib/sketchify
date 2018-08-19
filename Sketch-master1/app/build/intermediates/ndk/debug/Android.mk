LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := AbhanFilter
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
	/Users/shaikhsakib/AndroidStudioProjects/Sketch-master1/app/src/main/jni/Android.mk \
	/Users/shaikhsakib/AndroidStudioProjects/Sketch-master1/app/src/main/jni/com_abhan_example_AbhanNativeFilter.cpp \

LOCAL_C_INCLUDES += /Users/shaikhsakib/AndroidStudioProjects/Sketch-master1/app/src/debug/jni
LOCAL_C_INCLUDES += /Users/shaikhsakib/AndroidStudioProjects/Sketch-master1/app/src/main/jni

include $(BUILD_SHARED_LIBRARY)
