package com.example.camara;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    Button btnCamara;
    ImageView imageView;

    //boton para acceder a la carpeta de imagenes
    Button btnFolder;
    private static final int REQUEST_CAMERA_PERMISSION_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnFolder = findViewById(R.id.button2);
        btnFolder.setOnClickListener(v -> {
            openFolder();
        });

        btnCamara = findViewById(R.id.bt_camara);
        imageView = findViewById(R.id.imageView);


        btnCamara.setOnClickListener(v -> {
            captureImage();
        });

    }
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // El código de resultado es igual a RESULT_OK, procesa la imagen capturada.
                    Intent data = result.getData();
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            imageView.setImageBitmap(imageBitmap);
                            saveImageToGallery(imageBitmap);
                        }
                    }
                }
            }
    );


    private void saveImageToGallery(Bitmap imageBitmap) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "IMG_" + timeStamp + ".jpg";

        // Guardar la imagen en la carpeta de la galería
        String galleryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera";
        File galleryFolder = new File(galleryPath);

        if (!galleryFolder.exists()) {
            galleryFolder.mkdirs();
        }

        File imageFile = new File(galleryFolder, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // Escanear el archivo para que aparezca en la galería
            MediaScannerConnection.scanFile(this, new String[]{imageFile.getAbsolutePath()}, null, null);

            Toast.makeText(this, "Imagen guardada en la galería", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
        }}

//    private void handlePhotoTaken(Intent data) {
//        if (data != null) {
//            Bundle extras = data.getExtras();
//            if (extras != null) {
//                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                if (imageBitmap != null) {
//                    imageView.setImageBitmap(imageBitmap);
//                    saveImageToStorage(imageBitmap);
//                }
//            }
//        }
//    }

//    private void saveImageToStorage(Bitmap imageBitmap) {
//        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
//        File myDir = new File(root + "/MyAppImages");
//
//        if (!myDir.exists()) {
//            myDir.mkdirs();
//        }
//        String time = java.text.DateFormat.getTimeInstance().format(new java.util.Date());
//        String fileName = "image" + time + ".png";
//        File file = new File(myDir, fileName);
//
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//            out.flush();
//            out.close();
//            index++;
//
//            // Actualizar la galería para que la imagen sea visible
//            MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null, null);
//
//            Toast.makeText(this, "Imagen guardada en Documents", Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void captureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    public void openFolder() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/MyAppImages");
        intent.setDataAndType(uri, "*/*");

        // Abre el selector de archivos
        startActivity(Intent.createChooser(intent, "Open Folder"));
    }





}