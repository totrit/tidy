package com.totrit.tidy.core;

import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.totrit.tidy.ApplicationImpl;
import com.totrit.tidy.Utils;

/**
 * Created by totrit on 2015/1/31.
 */
public class TTS {
    private final static String LOG_TAG = "TTS";
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(LOG_TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Utils.showTip("初始化失败,错误码：" + code);
            }
        }
    };
    private static TTS sInstance;
    private SpeechSynthesizer mTts;
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private String voicer = "xiaoyan";
    private int mPercentForBuffering = 0;
    private int mPercentForPlaying = 0;
    private volatile boolean mIsSpeaking = false;
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
        }

        @Override
        public void onSpeakPaused() {
        }

        @Override
        public void onSpeakResumed() {
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            mPercentForBuffering = percent;
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            mPercentForPlaying = percent;
        }

        @Override
        public void onCompleted(SpeechError error) {
            synchronized (TTS.this) {
                TTS.this.mIsSpeaking = false;
            }
            if (error == null) {
                SpeechReceiver.getInstance().startReceiving();
            } else if (error != null) {
                Utils.showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };

    private TTS() {
        mTts = SpeechSynthesizer.createSynthesizer(ApplicationImpl.getGlobalContext(), mTtsInitListener);
    }

    public static TTS getInstance() {
        if (sInstance == null) {
            sInstance = new TTS();
            sInstance.initParams();
        }
        return sInstance;
    }

    public synchronized boolean isSpeaking() {
        return mIsSpeaking;
    }
    public void notifySettingMayChanged() {
        initParams();
    }

    public int speak(String msg) {
        mTts.stopSpeaking();
        synchronized (TTS.this) {
            TTS.this.mIsSpeaking = false;
        }
        int code = mTts.startSpeaking(msg, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                Utils.showTip("Need to install Yuyin+");
            } else {
                Utils.showTip("语音合成失败,错误码: " + code);
            }
            return -1;
        } else {
            return 0;
        }
    }

    private void initParams() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        //设置合成
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            //设置发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);

            //设置语速
            mTts.setParameter(SpeechConstant.SPEED, Utils.getCommonSP().getString("speed_preference", "50"));

            //设置音调
            mTts.setParameter(SpeechConstant.PITCH, Utils.getCommonSP().getString("pitch_preference", "50"));

            //设置音量
            mTts.setParameter(SpeechConstant.VOLUME, Utils.getCommonSP().getString("volume_preference", "50"));

            //设置播放器音频流类型
            mTts.setParameter(SpeechConstant.STREAM_TYPE, Utils.getCommonSP().getString("stream_preference", "3"));
        } else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            //设置发音人 voicer为空默认通过语音+界面指定发音人。
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
        }
    }
}
