package com.totrit.tidy.core;

/**
 * Created by totrit on 2015/1/31.
 */
public interface ISpeechCallback {
    /**
     * When there is speech received, call this callback.
     *
     * @param sentence
     * @return
     */
    public int onReceive(String sentence);
}
