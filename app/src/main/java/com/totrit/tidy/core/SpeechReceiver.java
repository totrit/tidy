package com.totrit.tidy.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.totrit.tidy.ApplicationImpl;
import com.totrit.tidy.Utils;

/**
 * Detect speaking and try receive one whole piece of speech, and transmit the speech to the TTS module
 * Created by totrit on 2015/1/31.
 */
public class SpeechReceiver {
    private static final String LOG_TAG = "SpeechReceiver";
    private static SpeechReceiver sInstance;

    public static SpeechReceiver getInstance() {
        if (sInstance == null) {
            sInstance = new SpeechReceiver();
        }
        return sInstance;
    }

    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(LOG_TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Log.e(LOG_TAG, "Error when initializing SpeechRecognizer");
            }
        }
    };
    private String mWholeSentence = "";
    /**
     * 识别监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
//            Utils.showTip("开始说话");
        }


        @Override
        public void onError(SpeechError error) {
            SpeechReceiver.this.startReceiving();
        }

        @Override
        public void onEndOfSpeech() {
//            Utils.showTip("结束说话");
        }


        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(LOG_TAG, results.getResultString());
            String text = JsonParser.parseIatResult(results.getResultString());
            if (!isLast) {
                mWholeSentence += text;
            } else {
                mWholeSentence += text;
                mHandler.onReceive(mWholeSentence);
                if (!TTS.getInstance().isSpeaking()) {
                    SpeechReceiver.this.startReceiving();
                }
                mWholeSentence = "";
            }
        }

        @Override
        public void onVolumeChanged(int volume) {
            Utils.showTip("当前正在说话，音量大小：" + volume);
        }


        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };
    private ISpeechCallback mHandler = new SpeechProcessor();
    private SpeechRecognizer mIat;
    private Context mBoundContext;
    private String mEngineType = "cloud";

    private  SpeechReceiver() {
        mIat = SpeechRecognizer.createRecognizer(ApplicationImpl.getGlobalContext(), mInitListener);
    }

    public boolean initIat() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = Utils.getCommonSP().getString("iat_language_preference", "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }
        // 设置语音前端点
        mIat.setParameter(SpeechConstant.VAD_BOS, Utils.getCommonSP().getString("iat_vadbos_preference", "4000"));
        // 设置语音后端点
        mIat.setParameter(SpeechConstant.VAD_EOS, Utils.getCommonSP().getString("iat_vadeos_preference", "1000"));
        // 设置标点符号
        mIat.setParameter(SpeechConstant.ASR_PTT, Utils.getCommonSP().getString("iat_punc_preference", "1"));
        // 设置音频保存路径
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/iflytek/wavaudio.pcm");
        return true;
    }

    public int startReceiving() {
        // 设置参数
        initIat();

        int ret = mIat.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            if (ret == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                //未安装则跳转到提示安装页面
                Utils.showTip("需要安装语音+");
            } else {
                Utils.showTip("识别失败,错误码: " + ret);
            }
            return -1;
        } else {
            Utils.showTip("开始监听");
            return 0;
        }
    }
}
