package com.raonsecure.rslogger.contract;

import android.content.Context;

import com.raonsecure.rslogger.model.Configuration;

import org.aspectj.lang.JoinPoint;

public interface RSLoggerContract {
    interface OnConfigurationResponseCallback {
        void onResponse(Configuration configuration);
        void onError(String errorMessage);
    }

    interface OnCrashResponseCallback {
        void onCrash(Context context, String stackTraceString);
    }

    interface OnUIEventResponseCallback {
        void onUI(JoinPoint joinPoint);
        void onInteraction(JoinPoint joinPoint);
    }
}
