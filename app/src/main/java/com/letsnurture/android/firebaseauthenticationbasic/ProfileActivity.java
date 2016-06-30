package com.letsnurture.android.firebaseauthenticationbasic;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.letsnurture.android.firebaseauthenticationbasic.utils.DialogUtils;
import com.letsnurture.android.firebaseauthenticationbasic.utils.FormValidationUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class ProfileActivity extends BaseAppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 2;

    @BindView(R.id.iv_profile)
    ImageView mImageView;

    @BindView(R.id.et_profile)
    EditText mEditText;

    private Uri mUri;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            Glide.with(this)
                    .load(mFirebaseUser.getPhotoUrl())
                    .into(mImageView);
            mEditText.setText(mFirebaseUser.getDisplayName());
        }
    }

    @OnClick(R.id.iv_profile)
    protected void onProfilePicClick(View view) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
        else {
            pickImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_IMAGE) {
                mUri = data.getData();
                Glide.with(this)
                        .loadFromMediaStore(data.getData())
                        .into(mImageView);
            }
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_profile;
    }

    @OnClick(R.id.btn_profile)
    void onUpdateProfileClick() {

        if (FormValidationUtils.isBlank(mEditText)) {
            FormValidationUtils.setError(null, mEditText, "Please enter your display name");
            return;
        }

        DialogUtils.showProgressDialog(this, "Updating Profile", "Please wait...", false);
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        builder.setDisplayName(mEditText.getText().toString());
        if (mUri != null) {
            builder.setPhotoUri(mUri);
        }
        UserProfileChangeRequest profileUpdates = builder.build();

        mFirebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        DialogUtils.dismissProgressDialog();
                        if (task.isSuccessful()) {
                            showToast("Profile Updated Successfully.");
                        }
                    }
                });
    }
}
