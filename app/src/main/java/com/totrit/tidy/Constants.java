package com.totrit.tidy;

import android.os.Environment;
import android.support.v4.content.ContextCompat;

import com.totrit.tidy.core.Communicator;

import java.io.File;

/**
 * Created by maruilin on 15/4/6.
 */
public class Constants {
    public final static String LOCAL_FILE_SCHEME = "file://";
    public static String PIC_PATH_ROOT;
    public static String TMP_SHOT_PIC_PATH;
    private static File[] PIC_ROOT_CANDIDATES;
    private static int PIC_ROOT_INDEX = 0;

    public static void init() {
        PIC_ROOT_CANDIDATES = ContextCompat.getExternalFilesDirs(Communicator.getInstance().getContext(), Environment.DIRECTORY_PICTURES);
        if (PIC_ROOT_CANDIDATES.length > 0) {
            PIC_PATH_ROOT = PIC_ROOT_CANDIDATES[PIC_ROOT_INDEX].getAbsolutePath();
            TMP_SHOT_PIC_PATH = PIC_PATH_ROOT + File.separator + "temp.jpg";
            Utils.d("Constants", "pic-root=" + PIC_PATH_ROOT);
        }
    }

    static boolean nextPicRoot() {
        if (PIC_ROOT_CANDIDATES.length > PIC_ROOT_INDEX + 1) {
            PIC_ROOT_INDEX ++;
            PIC_PATH_ROOT = PIC_ROOT_CANDIDATES[PIC_ROOT_INDEX].getAbsolutePath();
            TMP_SHOT_PIC_PATH = PIC_PATH_ROOT + File.separator + "temp.jpg";
            return true;
        } else {
            return false;
        }
    }

    static File[] getPicRootCandidates() {
        return PIC_ROOT_CANDIDATES;
    }
}
