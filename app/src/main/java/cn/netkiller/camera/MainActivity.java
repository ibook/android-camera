package cn.netkiller.camera;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageViewPicture;
    private Button buttonOpenCamera;
    private Button buttonSavePhoto;
    private Button buttonAlbum;

    private static int REQUEST_CAMERA = 1;
    private static int REQUEST_PHOTO = 2;
    private static int REQUEST_ALBUM = 3;
    private File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewPicture = (ImageView) findViewById(R.id.imageViewPicture);
        buttonOpenCamera = (Button) findViewById(R.id.buttonOpenCamera);
        buttonSavePhoto = (Button) findViewById(R.id.buttonSavePhoto);
        buttonAlbum = (Button) findViewById(R.id.buttonAlbum);

        buttonOpenCamera.setOnClickListener(this);
        buttonSavePhoto.setOnClickListener(this);
        buttonAlbum.setOnClickListener(this);

        StrictMode.VmPolicy.Builder newbuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newbuilder.build());

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.buttonOpenCamera:
                // 拍照并显示图片
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 启动系统相机
                startActivityForResult(intent, REQUEST_CAMERA);
                break;
            case R.id.buttonSavePhoto:

                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    TextView textView = (TextView) findViewById(R.id.textView);
                    textView.setText("SD 卡不存在，请插入 SD 卡！");
                }

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    return;
                } else {

                    String dir = Environment.getExternalStorageDirectory().getPath();
                    new File(dir).mkdirs();

                    imageFile = new File(dir, "tmp.png");

                    Uri imageUri = FileProvider.getUriForFile(MainActivity.this, "cn.netkiller.camera.provider", imageFile);
                    Log.d("album", imageFile.getPath());

                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 启动系统相机
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, REQUEST_PHOTO);


                }

                break;
            case R.id.buttonAlbum:

                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 3);

                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回数据
            if (requestCode == REQUEST_CAMERA) { // 判断请求码是否为REQUEST_CAMERA,如果是代表是这个页面传过去的，需要进行获取
                Bundle bundle = data.getExtras(); // 从data中取出传递回来缩略图的信息，图片质量差，适合传递小图片
                Bitmap bitmap = (Bitmap) bundle.get("data"); // 将data中的信息流解析为Bitmap类型
                imageViewPicture.setImageBitmap(bitmap);// 显示图片
            } else if (requestCode == REQUEST_PHOTO) {
                Log.i("photo", imageFile.getPath());
                try {
//                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    String dir = Environment.getExternalStorageDirectory().getPath();
                    FileInputStream fileInputStream = new FileInputStream(imageFile);
                    Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                    fileInputStream.close();
                    imageViewPicture.setImageBitmap(bitmap);// 显示图片
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_ALBUM) {

                Bitmap bitmap = null;

                //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
                ContentResolver resolver = getContentResolver();
                Uri originalUri = data.getData();        //获得图片的uri

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(resolver, originalUri);        //显得到bitmap图片
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //获取图片的路径：
                Log.i("album", String.valueOf(originalUri));

                imageViewPicture.setImageBitmap(bitmap);
            }
        }
    }

}
