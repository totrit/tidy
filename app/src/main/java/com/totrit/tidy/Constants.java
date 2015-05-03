package com.totrit.tidy;

import java.io.File;

/**
 * Created by maruilin on 15/4/6.
 */
public class Constants {
    public final static String PIC_PATH_REL = "tidy/pictures/";
    public final static String LOCAL_FILE_SCHEME = "file://";
    public static String PIC_PATH_ROOT = (System.getenv("SECONDARY_STORAGE") != null? System.getenv("SECONDARY_STORAGE"): System.getenv("EXTERNAL_STORAGE")) + File.separator + PIC_PATH_REL;
    public final static String TMP_SHOT_PIC_PATH = (System.getenv("SECONDARY_STORAGE") != null? System.getenv("SECONDARY_STORAGE"): System.getenv("EXTERNAL_STORAGE")) + File.separator + "temp.jpg";
}
