package com.clearlee.lockscreenmusiccontrol.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Clearlee on 2017/12/26 0026.
 */

public class ThreadManager {
    static ExecutorService mExecutorService;

    public static ExecutorService getExecutorService() {

        if (mExecutorService == null) {
            mExecutorService = Executors.newCachedThreadPool();
        }

        return mExecutorService;
    }
}
