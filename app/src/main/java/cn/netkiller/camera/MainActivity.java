package cn.netkiller.camera;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageViewPicture;
    private Button buttonOpenCamera;
    private Button buttonSavePhoto;
    private Button buttonAlbum;

    private static int REQUEST_CAMERA = 1;
    private static int REQUEST_PHOTO = 2;
    private static int REQUEST_ALBUM = 3;
    private String imagePath;

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

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    TextView textView = (TextView) findViewById(R.id.textView);
                    textView.setText("SD 卡不存在，请插入 SD 卡！");

//                    imagePath = getDataDir().getAbsolutePath();
//                    imagePath = getDir("tmp.png", MODE_PRIVATE).getAbsolutePath();
//                    File dir = new File(imagePath);
//                    if (!dir.exists()) {
//                        boolean result = dir.mkdirs();
//                        Log.d("album", "dir.mkdirs() -> " + imagePath);
//                    }

                } else {
                    imagePath = Environment.getExternalStorageDirectory().getPath();// 获取SD卡路径
                    imagePath = imagePath + "/" + "temp.png";// 指定路径

                    Log.d("album", imagePath);

                    // 拍照后存储并显示图片
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 启动系统相机
                    Uri photoUri = Uri.fromFile(new File(imagePath)); // 传递路径
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);// 更改系统默认存储路径
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
                Log.i("photo", imagePath);
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(imagePath); // 根据路径获取数据
                    Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                    imageViewPicture.setImageBitmap(bitmap);// 显示图片
                    fileInputStream.close();// 关闭流
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
