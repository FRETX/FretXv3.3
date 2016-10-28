LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := app
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
	C:\Users\Kickdrum\Projects\FretX\FretXv3.2\pdCore\src\main\jni\Android.mk \
	C:\Users\Kickdrum\Projects\FretX\FretXv3.2\pdCore\src\main\jni\Application.mk \

LOCAL_C_INCLUDES += C:\Users\Kickdrum\Projects\FretX\FretXv3.2\pdCore\src\main\jni
LOCAL_C_INCLUDES += C:\Users\Kickdrum\Projects\FretX\FretXv3.2\pdCore\src\arm\jni
LOCAL_C_INCLUDES += C:\Users\Kickdrum\Projects\FretX\FretXv3.2\pdCore\src\debug\jni
LOCAL_C_INCLUDES += C:\Users\Kickdrum\Projects\FretX\FretXv3.2\pdCore\src\armDebug\jni

include $(BUILD_SHARED_LIBRARY)
