//
// Created by BaiYang on 2023-04-12.
//

#ifndef MUSICPLAYER_ANDCALLJAVA_H
#define MUSICPLAYER_ANDCALLJAVA_H

#include "jni.h"
#include <linux/stddef.h>
#include "AndroidLog.h"

#define MAIN_THREAD 0
#define CHILD_THREAD 1

class AndCallJava {
public:
    _JavaVM *javaVM = NULL;
    JNIEnv *jniEnv = NULL;
    jobject jobj;

    jmethodID jmid_prepared;
    jmethodID jmid_timeinfo;

    jmethodID jmid_load;
    jmethodID jmid_renderyuv;

public:
    AndCallJava(_JavaVM *javaVM, JNIEnv *env, jobject obj);
    void onCallPrepared(int type);
    void onCallTimeInfo(int type, int curr, int total);
};


#endif //MUSICPLAYER_ANDCALLJAVA_H
