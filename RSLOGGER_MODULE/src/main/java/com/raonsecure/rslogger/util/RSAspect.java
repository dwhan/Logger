package com.raonsecure.rslogger.util;

import com.raonsecure.rslogger.contract.RSLoggerManager;
import com.raonsecure.rslogger.model.Configuration;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class RSAspect {
    private static final String TAG = "RSL";

    @Pointcut("execution(* android.view.View.OnClickListener.*(..))")
    public void onClickPoincut(){}

    @Pointcut("execution(* android.app.Activity.onCreate(..))")
    public void onCreateCutPoint() {}

    @Pointcut("execution(* android.app.Activity.onResume(..))")
    public void onResumeCutPoint() {}

    @Pointcut("execution(* android.app.Activity.onPause(..))")
    public void onPauseCutPoint() {}

    @Pointcut("execution(* android.app.Activity.onStart(..))")
    public void onStartCutPoint() {}

    @Pointcut("execution(* android.app.Activity.onStop(..))")
    public void onStopCutPoint() {}

    @Pointcut("execution(* android.app.Activity.onDestroy(..))")
    public void onDestroyCutPoint() {}


    //TODO: uievent internal log function

    @After("onClickPoincut()")
    public void atferOnClick(JoinPoint joinPoint){
        if (Configuration.getInstance().isEnabledUIEventLog() == false) {
            return;
        }

        RSLoggerManager.getInstance().onInteraction(joinPoint);
    }

    @After("onCreateCutPoint()")
    public void atferOnCreate(JoinPoint joinPoint){
        if (Configuration.getInstance().isEnabledUIEventLog() == false) {
            return;
        }

        RSLoggerManager.getInstance().onUI(joinPoint);
    }

    @After("onResumeCutPoint()")
    public void atferOnResume(JoinPoint joinPoint){
        if (Configuration.getInstance().isEnabledUIEventLog() == false) {
            return;
        }
        RSLoggerManager.getInstance().onUI(joinPoint);
    }

    @After("onPauseCutPoint()")
    public void atferOnPause(JoinPoint joinPoint){
        if (Configuration.getInstance().isEnabledUIEventLog() == false) {
            return;
        }

        RSLoggerManager.getInstance().onUI(joinPoint);
    }

    @After("onStartCutPoint()")
    public void atferOnStart(JoinPoint joinPoint){
        if (Configuration.getInstance().isEnabledUIEventLog() == false) {
            return;
        }

        RSLoggerManager.getInstance().onUI(joinPoint);
    }

    @After("onStopCutPoint()")
    public void atferOnStop(JoinPoint joinPoint){
        if (Configuration.getInstance().isEnabledUIEventLog() == false) {
            return;
        }

        RSLoggerManager.getInstance().onInteraction(joinPoint);
    }

    @After("onDestroyCutPoint()")
    public void atferOnDestroy(JoinPoint joinPoint){
        if (Configuration.getInstance().isEnabledUIEventLog() == false) {
            return;
        }

        RSLoggerManager.getInstance().onUI(joinPoint);
    }
}
