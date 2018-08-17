package com.example.andres.proyectofinal.Camara;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andres.proyectofinal.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class Camara extends AppCompatActivity {

    /*nuevo*/
    private static int TAKE_PICTURE = 1;
    private String name = "";
    private Button btnCamfot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        /*nuevo*/
        name = Environment.getExternalStorageDirectory() + "/test.jpg";
        Button btnAction = (Button) findViewById(R.id.btnBuscar);

        Button btnCamfoto = (Button) findViewById(R.id.btnCamfot);
    }
    private StorageReference mStorageRef;

    public void Guardar(View view) {
        ImageView imageView = (ImageView) findViewById(R.id.ivFoto);
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        String archivo = ((TextView) findViewById(R.id.etArchivo)).getText().toString();

        StorageReference fotoRef = mStorageRef.child("ProyecFinal/" + archivo);
        UploadTask uploadTask = fotoRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Toast.makeText(Camara.this, "La foto fue guardada...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    File localFile;

    public void Descargar(View view) {
        final ImageView imageView = (ImageView) findViewById(R.id.ivFoto);
        try {
            localFile = File.createTempFile("images", "jpg");

            String archivo = ((TextView) findViewById(R.id.etArchivo)).getText().toString();

            StorageReference fotoRef = mStorageRef.child("ProyecFinal/" + archivo);
            fotoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    imageView.setImageBitmap(BitmapFactory.decodeFile(localFile.getAbsolutePath().toString()));

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    // ...
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    ImageView foto_gallery;

    public void Buscar(View view) {
        foto_gallery = (ImageView) findViewById(R.id.ivFoto);

        foto_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        Toast.makeText(Camara.this, "Dar clic a la imagén de la camara.", Toast.LENGTH_SHORT).show();
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    public void Captura(View view) {
        Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        int code = TAKE_PICTURE;

        Uri output = Uri.fromFile(new File(name));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        Toast.makeText(Camara.this, "Capturar foto...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            foto_gallery.setImageURI(imageUri);
        }*/
        if (requestCode == TAKE_PICTURE) {
            if (data != null) {
                if (data.hasExtra("data")) {
                    ImageView iv = (ImageView) findViewById(R.id.ivFoto);
                    iv.setImageBitmap((Bitmap) data.getParcelableExtra("data"));
                } else {
                    ImageView iv = (ImageView) findViewById(R.id.ivFoto);
                    iv.setImageBitmap(BitmapFactory.decodeFile(name));

                    new MediaScannerConnection.MediaScannerConnectionClient() {
                        private MediaScannerConnection msc = null;

                        {
                            msc = new MediaScannerConnection(getApplicationContext(), this);
                            msc.connect();
                        }

                        public void onMediaScannerConnected() {
                            msc.scanFile(name, null);
                        }

                        public void onScanCompleted(String path, Uri uri) {
                            msc.disconnect();
                        }
                    };
                }
            }

        }
    }
}
    /*protected void onActivityResult (Intent data){
        Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        int code = TAKE_PICTURE;

        Uri output = Uri.fromFile(new File(name));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        startActivityForResult(intent, code);
    }

}*/
