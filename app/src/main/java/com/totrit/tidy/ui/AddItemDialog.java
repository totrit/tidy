package com.totrit.tidy.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.totrit.tidy.Constants;
import com.totrit.tidy.R;
import com.totrit.tidy.Utils;
import com.totrit.tidy.core.Entity;
import com.totrit.tidy.core.EntityManager;
import com.totrit.tidy.core.WorkingThread;

public class AddItemDialog extends ActionBarActivity {
    private final static String LOG_TAG = "AddItemDialog";

    private String mObjectImageName = null;
    private ImageButton mObjectImageButton = null;
    private String mContainerImageName = null;
    private ImageButton mContainerImageButton = null;
    private EditText mObjectDescEdit = null;
    private EditText mContainerDescEdit = null;

    // -1: none; 0: object image; 1: container image.
    private int mSelectingImageFor = -1;
    private EntityCreationController entityCreationController = new EntityCreationController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item_dialog);
        setTitleColor(getResources().getColor(R.color.holo_darker_green));
        mObjectDescEdit = ((EditText)findViewById(R.id.objectDesc));
        mContainerDescEdit = ((EditText)findViewById(R.id.containerDesc));
        mObjectImageButton = ((ImageButton)findViewById(R.id.objectImage));
        mContainerImageButton = ((ImageButton)findViewById(R.id.containerImage));

        mObjectDescEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view == mObjectDescEdit && b) {
                    handleObjectDescClick();
                }
            }
        });
        mObjectDescEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleObjectDescClick();
            }
        });

        mContainerDescEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view == mContainerDescEdit && b) {
                    handleContainerDescClick();
                }
            }
        });
        mContainerDescEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleContainerDescClick();
            }
        });

        mObjectImageButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view == mObjectImageButton && b) {
                    mSelectingImageFor = 0;
                    Utils.selectImage(AddItemDialog.this);
                }
            }
        });
        mObjectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectingImageFor = 0;
                Utils.selectImage(AddItemDialog.this);
            }
        });

        mContainerImageButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view == mContainerImageButton && b) {
                    mSelectingImageFor = 1;
                    Utils.selectImage(AddItemDialog.this);
                }
            }
        });
        mContainerImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectingImageFor = 1;
                Utils.selectImage(AddItemDialog.this);
            }
        });

        findViewById(R.id.button_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable objDesc = mObjectDescEdit.getText();
                Editable conDesc = mContainerDescEdit.getText();
                if (objDesc != null && !TextUtils.isEmpty(objDesc.toString()) && conDesc != null && !TextUtils.isEmpty(conDesc.toString())) {
                    Entity objEntity = new Entity(entityCreationController.usingExsitedObjectId, objDesc.toString(), mObjectImageName);
                    Entity conEntity = new Entity(entityCreationController.usingExsitedContainerId, conDesc.toString(), mContainerImageName);
                    objEntity.setContainer(conEntity.getEntityId());
                    EntityManager.getInstance().asyncSave(objEntity);
                    EntityManager.getInstance().asyncSave(conEntity);
                }
                AddItemDialog.this.finish();
            }
        });
    }

    private void handleObjectDescClick() {
        SearchActivity.startActivity(AddItemDialog.this, true, new SearchActivity.ISearchCallback() {
            @Override
            public void onEnd(SearchActivity.SearchResult result) {
                if (result.selectedEntity != null) {
                    entityCreationController.usingExsitedObjectId = result.selectedEntity.getEntityId();
                    entityCreationController.usingExsitedContainerId = result.selectedEntity.getContainerId();
                    mObjectDescEdit.setText(result.selectedEntity.getDescription());
                    EntityManager.getInstance().asyncQueryItemInfo(result.selectedEntity.getContainerId(), new EntityManager.IItemInfoQueryCallback() {
                        @Override
                        public void dataFetched(Entity entity) {
                            if (!TextUtils.isEmpty(entity.getDescription())) {
                                mContainerDescEdit.setText(entity.getDescription());
                            }
                            if (!TextUtils.isEmpty(entity.getImageName())) {
                                setContainerImage(entity.getImageName());
                            }
                        }
                    });
                    if (!TextUtils.isEmpty(result.selectedEntity.getImageName())) {
                        setObjectImage(result.selectedEntity.getImageName());
                    }
                } else {
                    entityCreationController.usingExsitedObjectId = -1;
                    if (!TextUtils.isEmpty(result.typedText)) {
                        mObjectDescEdit.setText(result.typedText);
                    }
                    setObjectImage(null);
                }
                hideKeyboard();
            }
        });
    }

    private void handleContainerDescClick() {
        SearchActivity.startActivity(AddItemDialog.this, true, new SearchActivity.ISearchCallback() {
            @Override
            public void onEnd(SearchActivity.SearchResult result) {
                if (result.selectedEntity != null) {
                    entityCreationController.usingExsitedContainerId = result.selectedEntity.getEntityId();
                    if (!TextUtils.isEmpty(result.selectedEntity.getDescription())) {
                        mContainerDescEdit.setText(result.selectedEntity.getDescription());
                        String imagePath = result.selectedEntity.getImageName();
                        if (!TextUtils.isEmpty(imagePath)) {
                            setContainerImage(imagePath);
                        }
                    }
                } else {
                    entityCreationController.usingExsitedContainerId = -1;
                    if (!TextUtils.isEmpty(result.typedText)) {
                        mContainerDescEdit.setText(result.typedText);
                    }
                }
                hideKeyboard();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (mSelectingImageFor == -1) {
                return;
            }
            final int rc = requestCode;
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    final String newImage = Utils.generateNewImageName();
                    if (rc == Utils.REQUEST_CAMERA) {
                        Utils.copyImageToPrivateDir(Constants.TMP_SHOT_PIC_PATH, newImage);
                    } else if (rc == Utils.SELECT_FILE) {
                        Uri selectedImageUri = data.getData();
                        String gallPicPath = getAbsolutePath(selectedImageUri);
                        Utils.d(LOG_TAG, "selected image path: " + gallPicPath);
                        Utils.copyImageToPrivateDir(gallPicPath, newImage);
                    }
                    AddItemDialog.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (newImage != null) {
                                switch (mSelectingImageFor) {
                                    case 0: {
                                        setObjectImage(newImage);
                                        break;
                                    }
                                    case 1: {
                                        setContainerImage(newImage);
                                        break;
                                    }
                                }
                            }
                        }
                    });
                }
            };
            WorkingThread.getInstance().post(r);
        }
    }

    private void setObjectImage(String image) {
        mObjectImageName = image;
        Utils.asyncLoadImage(image, mObjectImageButton);
    }

    private void setContainerImage(String image) {
        mContainerImageName = image;
        Utils.asyncLoadImage(image, mContainerImageButton);
    }

    private String getAbsolutePath(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    private class EntityCreationController {
        volatile long usingExsitedObjectId = -1;
        volatile long usingExsitedContainerId = -1;
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
