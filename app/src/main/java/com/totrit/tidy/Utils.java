package com.totrit.tidy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.totrit.tidy.core.Communicator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;

/**
 * Created by totrit on 2015/1/31.
 */
public class Utils {
    private final static boolean LOG_ENABLED = true;

    public static void d(String tag, String msg) {
        if (LOG_ENABLED) {
            android.util.Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (LOG_ENABLED) {
            android.util.Log.e(tag, msg);
        }
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {
        }
    }

    public final static int REQUEST_CAMERA = 0;
    public final static int SELECT_FILE = 1;
    public static void selectImage(final Activity context) {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(Constants.TMP_SHOT_PIC_PATH);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    context.startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    context.startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public static boolean makeSureDirExist(String dir) {
        File d = new File(dir);
        if (d.exists() && d.isDirectory()) {
            return true;
        } else if (d.exists() && !d.isDirectory()){
            d.delete();
        }
        return d.mkdirs();
    }

    public static boolean copy(String srcPath, String dstPath) {
        File src = new File(srcPath);
        File dst = new File(dstPath);
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(src);
            outStream = new FileOutputStream(dst);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            return true;
        } catch (IOException ignore) {
            if (LOG_ENABLED) {
                ignore.printStackTrace();
            }
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException ignore) {
            }
        }
        return false;
    }

    public static String generateNewImageName() {
        return "tidy_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
    }

    public static String milliesToDateStr(long millis) {
        if (millis <= 0) {
            return "";
        }
        SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        java.util.Date dt = new java.util.Date(millis);
        return sdf.format(dt);
    }

    public static void asyncLoadImage(String imgName, ImageView view) {
        if (imgName == null) {
            return;
        }
        final String IMG_PREFIX = Constants.LOCAL_FILE_SCHEME + Constants.PIC_PATH_ROOT;
        ImageLoader.getInstance().displayImage(IMG_PREFIX + imgName, view);
    }

    public static void viewImage(String imageName, Activity context) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        File file = new File(Constants.PIC_PATH_ROOT + imageName);
        intent.setDataAndType(Uri.fromFile(file), "image/*");
        context.startActivity(intent);
    }

    public static float dp2px(float dp, Resources res) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, res.getDisplayMetrics());
    }
}
