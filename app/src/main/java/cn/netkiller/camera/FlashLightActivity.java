package cn.netkiller.camera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.security.Policy;

public class FlashLightActivity extends AppCompatActivity {

    private Button buttonLight;
    private CameraManager cameraManager;
    private String cameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_light);

        buttonLight = (Button) findViewById(R.id.buttonLight);

        Boolean isFlashAvailable = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!isFlashAvailable) {

            AlertDialog alert = new AlertDialog.Builder(FlashLightActivity.this).create();
            alert.setTitle("Error");
            alert.setMessage("Your device doesn't support flash light!");
            alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                    System.exit(0);
                }
            });
            alert.show();
            return;
        }

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        buttonLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    light();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void light() throws CameraAccessException {


        if (buttonLight.getText().equals("On")) {
            Toast.makeText(getApplicationContext(), "打开手电筒", Toast.LENGTH_SHORT).show();
            cameraManager.setTorchMode(cameraId, true);
            buttonLight.setText("Off");
        } else {
            Toast.makeText(getApplicationContext(), "关闭手电筒", Toast.LENGTH_SHORT).show();
            cameraManager.setTorchMode(cameraId, false);
            buttonLight.setText("On");
        }
    }
}
