package com.immotor.batterystation.android.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.immotor.batterystation.android.BuildConfig;
import com.immotor.batterystation.android.MyApplication;
import com.immotor.batterystation.android.MyConfiguration;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.ui.views.CircleImageView;
import com.immotor.batterystation.android.util.BitmapUtil;
import com.immotor.batterystation.android.util.DateTimeUtil;
import com.immotor.batterystation.android.util.FileUtil;
import com.immotor.batterystation.android.util.LogUtil;
import com.immotor.batterystation.android.util.PermissionUtils;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Ashion on 2017/5/11.
 */

public class ProfileActivity extends BaseActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 101;
    private static final int REQUEST_CODE_CAPTURE_CAMERA  = 102;
    private static final int REQUEST_CODE_CUTTING = 103;

    @Bind(R.id.avatar_view)
    CircleImageView avatarView;

    @Bind(R.id.name_panel)
    View modifyNameView;
    @Bind(R.id.gender_panel)
    View modifyGenderView;
    @Bind(R.id.birthday_panel)
    View modifyBirthdayView;

    @Bind(R.id.name_show_view)
    TextView nameShowView;

    @Bind(R.id.credit_view)
    TextView creditView;

    @Bind(R.id.name_value)
    TextView nameView;

    @Bind(R.id.gender_value)
    TextView sexView;

    @Bind(R.id.birthday_value)
    TextView birthdayView;

    @Bind(R.id.phone_value)
    TextView phoneView;



    private String newAvatarPath;
    private String takePhotoPath;
    private BottomSheetDialog mDialog;
    private Uri photoUri;
    private Intent savedIntent = null;

    private boolean isUploadAvatar = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    @Override
    public void initUIView() {
        TextView mTittle = (TextView) findViewById(R.id.home_actionbar_text);
        mTittle.setText(R.string.personal_center);
        ImageView mImg = (ImageView) findViewById(R.id.home_actionbar_menu);
        mImg.setImageDrawable(getDrawable(R.mipmap.nav_back_icon_white));
        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initBottomSheet();
        updateUserInfo();
        if (mPreferences.getPhone().length() > 7) {
            String phoneNumber = mPreferences.getPhone();
            String number = phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7, phoneNumber.length());
            phoneView.setText(number);
        }

        creditView.setText(R.string.Credit_point_default);

        String avatar = mPreferences.getAvatar();
        if(!TextUtils.isEmpty(avatar)){
            Glide.with(this).load(MyConfiguration.AVATAR_PATH + FileUtil.getServerFileName(avatar)).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .priority(Priority.IMMEDIATE).crossFade().error(R.mipmap.ic_avatar_default).into(avatarView);
//            File file = new File(MyConfiguration.AVATAR_PATH + FileUtil.getServerFileName(avatar));
//            if(file.exists())
//            {
//                Bitmap photo = BitmapFactory.decodeFile(MyConfiguration.AVATAR_PATH + FileUtil.getServerFileName(avatar));
//                Bitmap bgBitmap2 = BitmapUtil.blurBitmap(this, photo);
//                findViewById(R.id.avatar_bg).setBackground(BitmapUtil.bitmapToDrawable(getResources(), bgBitmap2));
//            }
        }

    }

    @OnClick(R.id.avatar_view)
    public void ActionAvatar(){
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(ProfileActivity.this, perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_required_toast),
                    PermissionUtils.PERMISSION_REQUEST_CODE_LOCATION, perms);
        }else{
            if(isUploadAvatar){
                showSnackbar(getString(R.string.updating_deal_later));
            }else {
                mDialog.show();
            }
        }
    }

    public void initBottomSheet() {
        mDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.take_photo_sheet,null,false);
        Button takePhotoBt = (Button) view.findViewById(R.id.take_photo_bt);
        Button pictureBt = (Button)view.findViewById(R.id.picture_bt);
        takePhotoBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] perms = {Manifest.permission.CAMERA};
                if (!EasyPermissions.hasPermissions(ProfileActivity.this, perms)) {
                    EasyPermissions.requestPermissions(this, getString(R.string.permission_required_toast),
                            PermissionUtils.PERMISSION_REQUEST_CODE_LOCATION, perms);
                }else{
                    takePhoto();
                }
                mDialog.dismiss();
            }
        });
        pictureBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromLibrary();
                mDialog.dismiss();
            }
        });
        mDialog.setContentView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(savedIntent!=null){
            setImageToView(savedIntent);
            savedIntent = null;
        }
        updateUserInfo();
    }

    @Override
    protected void onDestroy() {
        if(!TextUtils.isEmpty(takePhotoPath)){
            File file = new File(takePhotoPath);
            if(file.exists()){
                file.delete();
            }
        }
        super.onDestroy();
    }

    private void updateUserInfo(){
        sexView.setText(getResources().getStringArray(R.array.gender)[mPreferences.getSex()]);
        long birthday = mPreferences.getBirthday();
        if(birthday==0){
            birthdayView.setText(R.string.not_setting);
        }else {
            try {
                String birthdayStr = DateTimeUtil.getDateTimeString("yyyy年MM月dd日", birthday);
                birthdayView.setText(birthdayStr);
            } catch (Exception e) {
                birthdayView.setText(R.string.not_setting);
            }
        }
        String name = mPreferences.getUserName();
        nameView.setText(TextUtils.isEmpty(name)?getString(R.string.not_setting):name);

        if (TextUtils.isEmpty(name)) {
            name = mPreferences.getPhone();
            if (name.length() > 7) {
                name = name.substring(0, 3) + "****" + name.substring(7, name.length());
            }
        }
        nameShowView.setText(name);
    }

    private void makeNewAvatarPath(){
        newAvatarPath = MyConfiguration.AVATAR_PATH + System.currentTimeMillis() + ".jpg";
    }

    private void makeTakePhotoPath(){
        int i = 0;
        do{
            takePhotoPath = MyConfiguration.AVATAR_PATH + "temp"+i + ".jpg";
            File file = new File(takePhotoPath);
            if(!file.exists()){
                return;
            }
            i++;
        }while(true);

    }

    private void httpUpdateAvatar(){
        if (!isNetworkAvaliable()){
            File file = new File(newAvatarPath);
            if(file.exists()){
                file.delete();
            }
            isUploadAvatar = false;
            return;
        }

        File file = new File(newAvatarPath);
        if(file.exists()){
            RequestBody requestFile =
            RequestBody.create(MediaType.parse("image/png"), file);
            MultipartBody.Part part =  MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<String>() {
                @Override
                public void onError(Throwable e) {
                    File file = new File(newAvatarPath);
                    if(file.exists()){
                        file.delete();
                    }
                    showSnackbar(getString(R.string.updating_fail));
                    isUploadAvatar = false;
                }

                @Override
                public void onNext(String result) {
                    if(!TextUtils.isEmpty(result)){
                        mPreferences.setAvatar(result);
                        String fileName = FileUtil.getServerFileName(result);
                        File f = new File(newAvatarPath);
                        f.renameTo(new File(MyConfiguration.AVATAR_PATH+fileName));
                        showSnackbar(getString(R.string.updating_scuess));
                        isUploadAvatar = false;
                    }
                }
            };
            HttpMethods.getInstance().updateAvatar(new ProgressSubscriber(subscriberOnNextListener, this, null), mPreferences.getToken(), part);
        }
    }


    @OnClick(R.id.name_panel)
    public void actionNamePanel(){
        Intent intent = new Intent(this,ModifyProfileActivity.class);
        intent.putExtra(ModifyProfileActivity.KEY_MODIFY_PROFILE, ModifyProfileActivity.TYPE_NAME);
        startActivity(intent);
    }

    @OnClick(R.id.gender_panel)
    public void actionGenderPanel(){
        Intent intent = new Intent(this,ModifyProfileActivity.class);
        intent.putExtra(ModifyProfileActivity.KEY_MODIFY_PROFILE, ModifyProfileActivity.TYPE_GENDER);
        startActivity(intent);
    }

    @OnClick(R.id.birthday_panel)
    public void actionBirthdayPanel(){
        Intent intent = new Intent(this,ModifyProfileActivity.class);
        intent.putExtra(ModifyProfileActivity.KEY_MODIFY_PROFILE, ModifyProfileActivity.TYPE_BIRTHDAY);
        startActivity(intent);
    }

    @OnClick(R.id.logout)
    public void actionLogout(){
        showdialog();
    }
    private void showdialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.log_out);
        dialog.setMessage(R.string.wether_log_out);
        dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                httpLogout();
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void httpLogout(){
        if (!isNetworkAvaliable()){
            return;
        }

        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Object>() {
            @Override
            public void onError(Throwable e) {
                showSnackbar(getString(R.string.log_out_fail));
            }

            @Override
            public void onNext(Object object) {
                if(object!=null){
                    LogUtil.d("obj: "+object);
                }
               // showSnackbar("登出成功");
                mPreferences.setToken(null);
                mPreferences.setComboStatus(false);
                mPreferences.setMyBatteryStatus(false);
                startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
            }
        };
        HttpMethods.getInstance().logout(new ProgressSubscriber(subscriberOnNextListener, this, null), mPreferences.getToken());
    }


    /**
     * 保存裁剪之后的图片数据
     *
     * @param data
     */
    private void setImageToView(Intent data) {
        if(isStop()){
            return;
        }
        Bundle extras = data.getExtras();
        Uri selectedImage = data.getData();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            saveBitmapFile(photo);
        }else if(selectedImage.toString().startsWith("file")){
            //Uri selectedImage = data.getData();
            Bitmap photo = null;
            try{
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                LogUtil.v("xxxxxxxxxx="+photo.getWidth());
            }catch (IOException e){
                e.printStackTrace();
            }
            if (photo != null && photo.getWidth() == 320){
                saveBitmapFile(photo);
            }
        }
    }


    private void saveBitmapFile(Bitmap photo){
        if(photo != null) {
            isUploadAvatar = true;
            makeNewAvatarPath();
            FileUtil.saveBitmapFile(photo, newAvatarPath);
            LogUtil.v("photo avatar path="+ newAvatarPath);
            Glide.with(this).load(newAvatarPath).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .priority(Priority.IMMEDIATE).crossFade().error(R.mipmap.ic_avatar_default).into(avatarView);
//            Bitmap bgBitmap2 = BitmapUtil.blurBitmap(this, photo);
//            findViewById(R.id.avatar_bg).setBackground(BitmapUtil.bitmapToDrawable(getResources(), bgBitmap2));
            httpUpdateAvatar();
        }
    }


    /**
     * 拍照
     */
    public void takePhoto(){
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            if(TextUtils.isEmpty(takePhotoPath)){
                makeTakePhotoPath();
            }

            photoUri = getUriForFile(new File(takePhotoPath));
        //    photoUri = Uri.fromFile(new File(takePhotoPath));
            LogUtil.v("photoUri=="+photoUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMERA);

        } else {
            showSnackbar(getString(R.string.insert_the_storage_card));
        }
    }
    public Uri getUriForFile( File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            uri = FileProvider.getUriForFile(getApplicationContext(), "com.immotor.batterystation.android.provider", file);
            if (uri==null) {
                uri = Uri.fromFile(file);
            }
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }
    /**
     * 从图库选择
     */
    public void pickFromLibrary(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CAPTURE_CAMERA:
                    startCutPhoto(photoUri);
                    break;
                case REQUEST_CODE_PICK_IMAGE:
                    if (data != null && data.getData() != null) {
                        startCutPhoto(data.getData());
                    }
                    break;
                case REQUEST_CODE_CUTTING:
                    savedIntent = data;
                    break;
            }
        }
    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * 裁剪图片方法实现
     * @param uri
     */
    public void startCutPhoto(Uri uri) {
        if (uri != null && uri.toString().startsWith("content")) {
            Bitmap bmp;
            String str = "";
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                str = MediaStore.Images.Media.insertImage(getContentResolver(), bmp, "", "");
                if (str==null) {
                    str = getPath(uri);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (str==null) {
                Toast.makeText(this, R.string.not_get_photo_address, Toast.LENGTH_SHORT).show();
                return;
            }
            uri = Uri.parse(str);
        }

        Intent intent = new Intent("com.android.camera.action.CROP");

        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String filePath = BitmapUtil.getPathByUri(ProfileActivity.this, uri);
            File uriFile = new File(filePath);
            Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", uriFile);
            if (contentUri==null) {
                contentUri = Uri.fromFile(uriFile);
            }
            intent.setDataAndType(contentUri, "image/*");
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File uriFile = new File(BitmapUtil.getPathByUri(ProfileActivity.this, uri));
            intent.setDataAndType(Uri.fromFile(uriFile), "image/*");
        }else{
            intent.setDataAndType(uri, "image/*");
        }
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", true);
        intent.putExtra("scale", true);
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.putExtra("output", uri);
        }*/
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQUEST_CODE_CUTTING);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            MyApplication myApplication = null;
            try {
                myApplication = (MyApplication) getApplicationContext();
            } catch (Exception e) {
                LogUtil.e( e.toString() );
                myApplication = null;
            }

            if (null == myApplication) {
                return true;
            }
            myApplication.exitAllActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
