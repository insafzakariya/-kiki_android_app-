package lk.mobilevisions.kiki.video.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Constants;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.activity.AudioProfileActivity;
import lk.mobilevisions.kiki.chat.ChatProfileActivity;
import lk.mobilevisions.kiki.chat.ChatUserImageAdapter;
import lk.mobilevisions.kiki.chat.module.ChatManager;
import lk.mobilevisions.kiki.chat.module.dto.Avatar;
import lk.mobilevisions.kiki.databinding.ActivityVideoProfileBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.AuthUser;
import lk.mobilevisions.kiki.modules.auth.AuthManager;
import lk.mobilevisions.kiki.modules.auth.AuthOptions;
import lk.mobilevisions.kiki.modules.auth.User;
import lk.mobilevisions.kiki.ui.auth.EditMobileNumberActivity;


public class VideoProfileActivity extends AppCompatActivity implements View.OnClickListener, ChatUserImageAdapter.OnImageItemActionListener {
    @Inject
    AuthManager authManager;
    @Inject
    ChatManager chatManager;
    ActivityVideoProfileBinding binding;
    private String selectedLanguage = "English";
    AlertDialog alertDialog;
    private Context context;
    List<Avatar> avatarList = new ArrayList<>();
    private Uri selectedImageUri;
    private String encodedImage = "";
    private int selectedAvatarId = 0;
    private String imageType;
    private String selectedGender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_profile);
        ((Application) getApplication()).getInjector().inject(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.newUiBackground));
        }
        ((Application) getApplication()).getAnalytics().setCurrentScreen(this, "VideoProfileActivity", null);
        binding.includedToolbar.backImageview.setOnClickListener(this);
        binding.sinhalaTextView.setOnClickListener(this);
        binding.tamilTextView.setOnClickListener(this);
        binding.englishTextView.setOnClickListener(this);
        binding.phoneNumberEdittext.setOnClickListener(this);
        binding.updateLayout.setOnClickListener(this);
        binding.updateLayout.setEnabled(false);
        binding.userImage.setOnClickListener(this);
        binding.maleSelectImageview.setOnClickListener(this);
        binding.femaleSelectImageview.setOnClickListener(this);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_user_avatar)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);

        Glide.with(this).load("https://storage.googleapis.com/kiki_images/live/viewer/3x/" + Application.getInstance().getAuthUser().getId() + ".jpeg")
                .apply(options).into(binding.userImage);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_imageview:
                finish();
                break;
            case R.id.english_text_view:
                changeLanguageSelectStates("english");
                break;
            case R.id.sinhala_text_view:
                changeLanguageSelectStates("sinhala");
                break;
            case R.id.tamil_text_view:
                changeLanguageSelectStates("tamil");
                break;
            case R.id.phone_number_edittext:
                Intent profileIntent = new Intent(this, EditMobileNumberActivity.class);
                startActivity(profileIntent);
                break;
            case R.id.update_layout:
                updateProfileDetails();
                break;
            case R.id.user_image:
                getAvatarImages();
                break;
            case R.id.male_select_imageview:
                if (binding.maleSelectImageview.isChecked()) {
                    selectedGender = "MALE";
                    binding.femaleSelectImageview.setChecked(false);
                }
                break;
            case R.id.female_select_imageview:
                if (binding.femaleSelectImageview.isChecked()) {
                    selectedGender = "FEMALE";
                    binding.maleSelectImageview.setChecked(false);
                }

                break;
            default:
        }
    }

    private void changeLanguageSelectStates(String language) {

        if (language.equals("english")) {
            selectedLanguage = "English";
            binding.tamilSelectImageview.setVisibility(View.GONE);
            binding.sinhalaSelectImageview.setVisibility(View.GONE);
            binding.englishSelectImageview.setVisibility(View.VISIBLE);

        } else if (language.equals("tamil")) {
            selectedLanguage = "Tamil";
            binding.tamilSelectImageview.setVisibility(View.VISIBLE);
            binding.sinhalaSelectImageview.setVisibility(View.GONE);
            binding.englishSelectImageview.setVisibility(View.GONE);
        } else if (language.equals("sinhala")) {

            selectedLanguage = "Sinhala";
            binding.tamilSelectImageview.setVisibility(View.GONE);
            binding.sinhalaSelectImageview.setVisibility(View.VISIBLE);
            binding.englishSelectImageview.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        getUserDetails();
        super.onResume();
    }

    private void getUserDetails() {
        binding.aviProgress.setVisibility(View.VISIBLE);
        authManager.getUserDetails(new APIListener<AuthUser>() {
            @Override
            public void onSuccess(AuthUser user, List<Object> params) {
                binding.phoneNumberEdittext.setText(user.getMobileNumber());
                binding.nameEdittext.setText(user.getName());
//                if (user.getGender() != null) {
//                    System.out.println("djfbdjbf " + user.getGender());
//                    if (user.getGender().equals("M")){
//                        binding.maleSelectImageview.setChecked(true);
//                    } else if (user.getGender().equals("F")){
//                        binding.femaleSelectImageview.setChecked(true);
//                    }
//
//                }
//                System.out.println("sdsdsd " + user.getGender().toUpperCase());
                binding.usernameField.setText(user.getUsername());
                changeLanguageSelectStates(user.getLanguage().toLowerCase());
                binding.updateLayout.setEnabled(true);
                binding.aviProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {

                Utils.Error.onServiceCallFail(VideoProfileActivity.this, t);
            }
        });
    }

    private void updateProfileDetails() {
        final MaterialDialog progressDialog = createProgressDialog();
        if (((Activity) VideoProfileActivity.this).hasWindowFocus()) {
            progressDialog.show();
        }
        final User user = new User();
        user.setName(binding.nameEdittext.getText().toString());
        user.setLanguage(AuthOptions.Language.valueOf
                (selectedLanguage.toUpperCase()));
//        user.setGender(AuthOptions.Gender.valueOf
//                (selectedGender.toUpperCase()));
        System.out.println("nfdjnfjd " + selectedGender);
        authManager.updateUserDetails(user, new APIListener<AuthUser>() {
            @Override
            public void onSuccess(AuthUser result, List<Object> params) {
                ((Application) getApplication()).setAuthUser(result);
                progressDialog.dismiss();
                setUpAppLanguage();
                Intent intent = new Intent(VideoProfileActivity.this, VideoDashboardActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
                Utils.Error.onServiceCallFail(VideoProfileActivity.this, t);
            }
        });
    }

    private MaterialDialog createProgressDialog() {
        MaterialDialog dialog = Utils.Dialog.createDialog(this, getString(R.string.please_wait))
                .cancelable(false)
                .backgroundColorRes(R.color.dialogProgressBackground)
                .canceledOnTouchOutside(false)
                .progress(true, 0)
                .build();
        return dialog;
    }

    private void setUpAppLanguage() {
        if (selectedLanguage.equals("English")) {
            Utils.SharedPrefUtil.saveStringToSharedPref(this, Constants.KEY_LANGUAGE_PHONE, Constants.LANG_EN_PHONE);
            Utils.SharedPrefUtil.saveStringToSharedPref(this, Constants.KEY_LANGUAGE_PHONE_FULLNAME, Constants.LANG_EN_PHONE_FULL);

        } else if (selectedLanguage.equals("Tamil")) {
            Utils.SharedPrefUtil.saveStringToSharedPref(this, Constants.KEY_LANGUAGE_PHONE, Constants.LANG_TA_PHONE);
            Utils.SharedPrefUtil.saveStringToSharedPref(this, Constants.KEY_LANGUAGE_PHONE_FULLNAME, Constants.LANG_TA_PHONE_FULL);


        } else if (selectedLanguage.equals("Sinhala")) {
            Utils.SharedPrefUtil.saveStringToSharedPref(this, Constants.KEY_LANGUAGE_PHONE, Constants.LANG_SI_PHONE);
            Utils.SharedPrefUtil.saveStringToSharedPref(this, Constants.KEY_LANGUAGE_PHONE_FULLNAME, Constants.LANG_SI_PHONE_FULL);


        }
        finish();
    }

    private void getAvatarImages() {

        avatarList.clear();
        chatManager.getAvatarImages(new APIListener<List<Avatar>>() {
            @Override
            public void onSuccess(List<Avatar> avatars, List<Object> params) {

                Avatar avatar = new Avatar();
                avatar.setId(0);
                avatar.setUrl("");
                avatarList.add(avatar);
                avatarList.addAll(avatars);
                selectUserImageDialog(avatarList);

            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getBaseContext(), t);
            }
        });
    }


    private void selectUserImageDialog(List<Avatar> avatars) {

        LayoutInflater myLayout = LayoutInflater.from(this);
        final View dialogView = myLayout.inflate(R.layout.alert_profile_image_select, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VideoProfileActivity.this, R.style.CustomAlertDialog);
        alertDialogBuilder.setView(dialogView);
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        RecyclerView recyclerView = (RecyclerView) alertDialog.findViewById(R.id.userImagesRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 4));

        ChatUserImageAdapter adapter = new ChatUserImageAdapter(this, avatars, VideoProfileActivity.this);
        recyclerView.setAdapter(adapter);


        TextView continueView = (TextView) alertDialog.findViewById(R.id.continue_textview);

        continueView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (selectedAvatarId == 0 && encodedImage.equals("")) {
                    Toast.makeText(getApplicationContext(), "Select an image to Continue.", Toast.LENGTH_SHORT).show();
                } else {
                    uploadUserImage(imageType, selectedAvatarId);
                    alertDialog.dismiss();
                }
            }
        });

    }

    @Override
    public void onImagePlayAction(Avatar avatar, int position, List<Avatar> avatars) {

        if (position == 0) {
            imageType = "CUSTOM";
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, 1);
        } else {
            selectedAvatarId = avatar.getId();
            imageType = "AVATAR";
            try {
                Picasso.with(context).load(URLDecoder.decode(avatar.getUrl(), "UTF-8")).fit().centerCrop()
                        .placeholder(R.drawable.program)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(binding.userImage);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
//            RequestOptions options = new RequestOptions()
//                    .centerCrop()
//                    .placeholder(R.drawable.program);
//
//            Glide.with(this).load(avatar.getUrl()).apply(options).into(binding.userImage);

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    selectedImageUri = imageReturnedIntent.getData();
                    binding.userImage.setImageURI(selectedImageUri);

                    final InputStream imageStream;
                    try {
                        imageStream = getApplicationContext().getContentResolver().openInputStream(selectedImageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        encodedImage = encodeImage(selectedImage);
                        encodedImage = "data:image/png;base64," + encodedImage.trim();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
    }

    private String encodeImage(Bitmap bm) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] b = baos.toByteArray();
//        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
//
//        return encImage;

        Bitmap immagex = bm;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.e("LOOK", imageEncoded.trim().replace("\n", "").replace("\r", ""));
        return imageEncoded;
    }

    private void uploadUserImage(String imageType, int avatarId) {

        System.out.println("jdhfhsdf 111 " + imageType);
        System.out.println("jdhfhsdf 222 " + avatarId);
        if (imageType.equals("AVATAR")) {
            encodedImage = "";
        }
        encodedImage = encodedImage.trim().replace("\n", "").replace("\r", "");
        System.out.println("jdhfhsdf " + encodedImage);
        chatManager.uploadUserImage(encodedImage, imageType, avatarId, new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {

                System.out.println("jdhfhsdf 333 ");
                Toast.makeText(getApplicationContext(), "Uploaded.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("jdhfhsdf 444 " + t.getMessage());

            }
        });
    }
}
