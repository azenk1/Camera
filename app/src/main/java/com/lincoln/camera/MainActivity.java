package com.lincoln.camera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    StorageReference mStorageRef;
    String currentPhotoPath;
    Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Storage reference for Firebase
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public void pictureBtnClick(View view) {
        dispatchPictureIntent();
    }

    static final int REQUEST_TAKE_PHOTO = 1;
    public void dispatchPictureIntent() {

        //New Intent instance. ACTION_IMAGE_CAPTURE is intent action sent as parameter
        //to instruct com.lincoln.camera application of target environment to capture an image and
        //return it.
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            //Is a com.lincoln.camera activity available to handle this intent?
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                //.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);


                File photoFile = null;
                try{
                    photoFile = createImageFile();
                } catch (IOException ex)
                {

                    Context context = getApplicationContext();
                    CharSequence text = "Unable to create image file. Contact Al.";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

                //Was photo file created successfully?
                if (photoFile != null)
                {
                    photoURI = FileProvider.getUriForFile(this,
                            "com.lincoln.camera.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

                }
            }
        } catch(Exception ex)
        {
            Context context = getApplicationContext();
            CharSequence text = ex.getMessage();
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageBitmap(imageBitmap);

        }
    }
*/
    private File createImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_ " + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();

        //Return image created using String imageFileName, the .jpg suffix, and File storageDir that
        // points to the pictures directory of the target environment.
        return image;
    }
}