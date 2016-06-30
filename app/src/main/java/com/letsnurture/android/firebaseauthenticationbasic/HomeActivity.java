package com.letsnurture.android.firebaseauthenticationbasic;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeActivity extends BaseAppCompatActivity {

    @BindView(R.id.tv_home)
    TextView mTextView;

    @BindView(R.id.iv_home)
    ImageView mImageView;

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            Glide.with(this)
                    .load(firebaseUser.getPhotoUrl())
                    .into(mImageView);
            mTextView.setText("Display Name: " + firebaseUser.getDisplayName());
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }

    @OnClick(R.id.btn_home_update_profile)
    void updateProfile() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    @OnClick(R.id.btn_home_logout)
    void logout() {
        mFirebaseAuth.signOut();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
