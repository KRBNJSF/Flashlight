package cz.reindl.flashlight;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private ImageButton toggleButton, toggleSecret;
    private EditText editText;
    private RelativeLayout mainLayout;
    private boolean isOn;
    private String status;
    private String[] values;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        editText = (EditText) findViewById(R.id.editText);
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);

        editText.setOnClickListener(l -> {
            if (!editText.isActivated()) {
                values = String.valueOf(editText.getText()).split("/");
                //showKeyboard(editText);
            }
        });

        toggleSecret = findViewById(R.id.toggleSecret);
        toggleSecret.setOnClickListener(l -> {
            try {
                for (int i = 0; i < 1; i++) {
                    Log.d("Autonomni blik blik", "Auto is enabled");
                    try {
                        setAuto(Boolean.parseBoolean(values[0]), Integer.parseInt(values[1]));
                        toggleFlashlight(false);
                        Thread.sleep(200);
                        toggleFlashlight(true);
                        Thread.sleep(250);
                        toggleFlashlight(false);
                    } catch (Exception e) {
                        editText.setActivated(false);
                        hideKeyboard();
                        Snackbar.make(mainLayout, "Wrong format!", Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, "Insert values in this format:\npause/milliseconds/repeats \n true/1000/1", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                editText.setActivated(false);
                hideKeyboard();
                Snackbar.make(mainLayout, "Wrong format!", Snackbar.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "Insert values in this format:\npause/milliseconds/repeats \n true/1000/1", Toast.LENGTH_SHORT).show();
            }
        });

        toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(l -> {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                checkState();
                try {
                    toggleFlashlight(isOn);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MainActivity.this, "No flashlight available", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void toggleFlashlight(boolean isOn) throws CameraAccessException {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraManager.setTorchMode(cameraManager.getCameraIdList()[0], isOn);
        }
        Snackbar.make(mainLayout, status, Snackbar.LENGTH_SHORT).show();
    }

    private void checkState() {
        if (isOn) {
            isOn = false;
            status = State.OFF.status;
            toggleButton.setImageResource(R.drawable.bolt_off);
        } else {
            isOn = true;
            status = State.ON.status;
            toggleButton.setImageResource(R.drawable.bolt_on);
        }
    }

    private void setAuto(boolean enable, int milliseconds) throws CameraAccessException, InterruptedException {
        toggleFlashlight(enable);
        Thread.sleep(milliseconds);
    }

    private void showKeyboard(EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.requestFocus();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    private void hideKeyboard() {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mgr.isActive()) mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public boolean onTouchEvent(MotionEvent event) {
        hideKeyboard();
        editText.setText("");
        return super.onTouchEvent(event);
    }

}
