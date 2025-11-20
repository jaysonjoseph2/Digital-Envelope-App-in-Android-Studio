package com.example.digital_envelope;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import java.io.OutputStream;

public class EncryptActivity extends AppCompatActivity {

    private byte[] encryptedBlob;

    ActivityResultLauncher<Intent> filePicker =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        try {
                            Uri uri = result.getData().getData();
                            InputStream is = getContentResolver().openInputStream(uri);

                            byte[] original = IOUtils.readAllBytes(is);

                            CryptoUtils.ensureRSAKeyExists();
                            EnvelopeFile env = CryptoUtils.encryptData(original);

                            encryptedBlob = env.toBytes();

                            Toast.makeText(this, "File encrypted. Now save it.", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error encrypting", Toast.LENGTH_SHORT).show();
                        }
                    });

    ActivityResultLauncher<Intent> savePicker =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        try {
                            Uri uri = result.getData().getData();
                            OutputStream os = getContentResolver().openOutputStream(uri);
                            os.write(encryptedBlob);
                            os.close();
                            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);

        Button chooseBtn = findViewById(R.id.btnChooseFile);
        Button saveBtn = findViewById(R.id.btnSaveEncrypted);

        chooseBtn.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("*/*");
            filePicker.launch(i);
        });

        saveBtn.setOnClickListener(v -> {
            if (encryptedBlob == null) {
                Toast.makeText(this, "Encrypt first!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            i.setType("application/octet-stream");
            i.putExtra(Intent.EXTRA_TITLE, "encrypted.envpkg");
            savePicker.launch(i);
        });
    }
}
