package com.imoko.core;

/**
 * Created by jb on 2016/11/5.
 */
public class CoreManager {
    private static volatile CoreManager instance;

    public static CoreManager getInstance() {
        if (instance == null) {
            synchronized (CoreManager.class) {
                if (instance == null) {
                    instance = new CoreManager();
                }
            }
        }
        return instance;
    }
    public void init(){

    }
}
