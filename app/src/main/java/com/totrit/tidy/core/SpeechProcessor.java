package com.totrit.tidy.core;

import android.util.Pair;

import com.totrit.tidy.Utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by totrit on 2015/1/31.
 */
class SpeechProcessor implements ISpeechCallback {
    private final static String LOG_TAG = "SpeechProcessor";
    private SpeechProcessorContext mFSM;
    private SubjectAbstractFactory mSubjectsFactory = new SubjectSimpleFactory();
    private Pair<ISubject, ISubject> mProvisionalInstruction;
    private Collection<ISubject> mConflictingSubjects;
    private Collection<ISubject> mConflictingPlaces;
    private int mConflictingPart = -1;

    public SpeechProcessor() {
        mFSM = new SpeechProcessorContext(this);
        mFSM.enterStartState();
    }

    @Override
    public int onReceive(String sentence) {
        if (sentence == null || sentence.length() < 2) {
            Utils.e(LOG_TAG, "sentence too short, skip.");
            return 0;
        }
        Utils.d(LOG_TAG, "onReceive, sentence=" + sentence);
        sentence = sentence.substring(0, sentence.length() - "。".length());
        if (checkStandby(sentence)) {
            mFSM.standby();
            return -1;
        }
        switch (mFSM.getState().getId()) {
            case 0: { // Standby
                if (checkBackToNormalState(sentence)) {
                    mFSM.start();
                    return 1;
                }
                break;
            }
            case 1: { // Normal
                int tryResult = judgeSentenceType(sentence);
                if (tryResult == 0) {
                    mFSM.check("你是说" + mProvisionalInstruction.first + "被搞到" + mProvisionalInstruction.second + "了吗？");
                } else if (tryResult == -1) {
                    mFSM.unexpectedAnswer();
                } else if (tryResult == -2) {
                    //TODO the subject and placedInto may both have conflicts
                    if (mConflictingSubjects != null) {
                        mFSM.conflict(mProvisionalInstruction.first, mConflictingSubjects.iterator().next());
                        mConflictingPart = 0;
                    } else if (mConflictingPlaces != null) {
                        mFSM.conflict(mProvisionalInstruction.second, mConflictingPlaces.iterator().next());
                        mConflictingPart = 1;
                    }
                }
                break;
            }
            case 2: { // Checking
                if (checkWhetherResetCommand(sentence)) {
                    Utils.d(LOG_TAG, "reset-cmd=true");
                    mFSM.restart();
                    break;
                }
                int answerType = answerType(sentence);
                Utils.d(LOG_TAG, sentence + ", answer-type=" + answerType);
                if (answerType == 1) {
                    mFSM.ok();
                } else if (answerType == -1) {
                    mFSM.wrong();
                } else {
                    mFSM.unexpectedAnswer();
                }
                break;
            }
            case 3: { // ConflictResolving
                if (checkWhetherResetCommand(sentence)) {
                    Utils.d(LOG_TAG, "reset=true, state=" + mFSM.getState());
                    mFSM.restart();
                    break;
                }
                int answerType = answerType(sentence);
                Utils.d(LOG_TAG, "answer-type=" + answerType + ", state=" + mFSM.getState());
                if (answerType == 1) {
                    if (mConflictingPart == 0) {
                        mFSM.replace(mProvisionalInstruction.first, mConflictingSubjects.iterator().next());
                    } else if (mConflictingPart == 1) {
                        mFSM.replace(mProvisionalInstruction.second, mConflictingPlaces.iterator().next());
                    }
                } else if (answerType == -1) {
                    mFSM.keep();
                } else {
                    mFSM.unexpectedAnswer();
                }
                break;
            }
            default: {
                Utils.d(LOG_TAG, "ERROR! state=" + mFSM.getState());
                break;
            }
        }
        return 0;
    }

    private static int answerType(String statement) {
        final String[] POSITIVE_ANSWERS = {"是", "是的", "对"};
        final Set<String> POSITIVE_ANSWERS_SET = new HashSet<String>(Arrays.asList(POSITIVE_ANSWERS));

        final String[] NEGATIVE_ANSWERS = {"不是", "错", "不对"};
        final Set<String> NEGATIVE_ANSWERS_SET = new HashSet<String>(Arrays.asList(NEGATIVE_ANSWERS));
        if (POSITIVE_ANSWERS_SET.contains(statement)) {
            return 1;
        } else if (NEGATIVE_ANSWERS_SET.contains(statement)) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * @param sentence
     * @return 0   :the sentence is an insert instruction
     * 1    : the sentence is a query instruction
     * -1  : sentence is in wrong format, can not recognize
     * -2   : the subject or the placedInto may have conflicts;
     *
     */
    private int judgeSentenceType(String sentence) {
        final String KEYWORD_QUERY = "在哪里";
        int queryIndex = sentence.indexOf(KEYWORD_QUERY);
        if (queryIndex != null) {

        }
        if (sentence.contains(KEYWORD_QUERY))
        final double SIMILARITY_THRESHOLD = 0.9;
        mProvisionalInstruction = mSubjectsFactory.createObject(sentence);
        if (mProvisionalInstruction != null) {
            if ((mConflictingSubjects = mSubjectsFactory.similarSubjects(mProvisionalInstruction.first, SIMILARITY_THRESHOLD)) != null ||
                    (mConflictingPlaces = mSubjectsFactory.similarSubjects(mProvisionalInstruction.second, SIMILARITY_THRESHOLD)) != null) {
                return -2;
            } else {
                return 0;
            }
        } else {
            return -1;
        }
    }

    private boolean checkWhetherResetCommand(String msg) {
        final String[] RESET_CMDS = {"重来", "重新开始"};
        for (int i = 0, count = RESET_CMDS.length; i < count; i++) {
            if (msg.equals(RESET_CMDS[i])) {
                return true;
            }
        }
        return false;
    }

    // check whether it's a stand-by command.
    private boolean checkStandby(String msg) {
        final String KEYWORD_PAUSE = "暂停";
        return msg.equals(KEYWORD_PAUSE);
    }

    private boolean checkBackToNormalState(String msg) {
        return msg.equals("开始");
    }

    void tipGoodbye() {
        Utils.d(LOG_TAG, "goodbye.");
        TTS.getInstance().speak("暂停接受指令，如需重新开始，请讲开始指令");
    }

    void tipWelcome() {
        Utils.d(LOG_TAG, "welcome.");
        TTS.getInstance().speak("您好，请您以什么放到什么里这样的句式来给出指令");
    }

    void tipCheck(String toRepeat) {
        TTS.getInstance().speak(toRepeat);
    }

    void saveProvisional() {
        Utils.d(LOG_TAG, "save item, subject=" + mProvisionalInstruction.first + ", place=" + mProvisionalInstruction.second);
        mSubjectsFactory.putSubject(mProvisionalInstruction.first, mProvisionalInstruction.second);
        TTS.getInstance().speak("收到!");
    }

    void discardProvisional() {
        Utils.d(LOG_TAG, "discardProvisional");
        mProvisionalInstruction = null;
        mConflictingPart = -1;
        mConflictingPlaces = mConflictingSubjects = null;
    }

    void tipUnexpectedAnswer() {
        Utils.d(LOG_TAG, "tipUnexpectedAnswer");
        TTS.getInstance().speak("请您再说一遍");
    }

    void trySolve(ISubject newSubject, ISubject existingSubject) {
        Utils.d(LOG_TAG, "trySolve, newOne=" + newSubject + ", existing=" + existingSubject);
        TTS.getInstance().speak("您是指之前说过的" + existingSubject + "吗？");
    }

    void changeAndSaveProvisional(ISubject newSubject, ISubject existingSubject) {
        ISubject subject = mProvisionalInstruction.first, place = mProvisionalInstruction.second;
        if (mConflictingPart == 0) {
            subject = mConflictingSubjects.iterator().next();
        } else if (mConflictingPart == 1) {
            place = mConflictingPlaces.iterator().next();
        }
        Utils.d(LOG_TAG, "changeAndSaveProvisional, conflicting=" + mConflictingPart + ", existing=" + existingSubject + ", newOne=" + newSubject);
        mSubjectsFactory.putSubject(subject, place);
    }

    void tipStartOver() {
        TTS.getInstance().speak("请您完整地从头说一遍");
    }

    void endOfConflict() {
        mConflictingPart = -1;
        mConflictingPlaces = mConflictingSubjects = null;
    }

    void logState() {
        Utils.d(LOG_TAG, "state switched to " + mFSM.getState());
    }

    void tipAnswer(String sentence) {
        TTS.getInstance().speak(sentence);
    }

    void readyForInstruction() {
        mProvisionalInstruction = null;
    }
}
