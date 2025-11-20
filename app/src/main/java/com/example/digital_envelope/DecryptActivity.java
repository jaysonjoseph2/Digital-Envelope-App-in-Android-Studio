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

public class DecryptActivity extends AppCompatActivity {

    private byte[] decryptedBlob;

    ActivityResultLauncher<Intent> openPicker =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        try {
                            Uri uri = result.getData().getData();
                            InputStream is = getContentResolver().openInputStream(uri);
                            byte[] envelopeBytes = IOUtils.readAllBytes(is);

                            EnvelopeFile env = EnvelopeFile.fromBytes(envelopeBytes);

                            decryptedBlob = CryptoUtils.decryptData(env);

                            Toast.makeText(this, "Decrypted. Save output!", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error decrypting", Toast.LENGTH_SHORT).show();
                        }
                    });

    ActivityResultLauncher<Intent> savePicker =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        try {
                            Uri uri = result.getData().getData();
                            OutputStream os = getContentResolver().openOutputStream(uri);
                            os.write(decryptedBlob);
                            os.close();
                        } catch (Exception e) {}
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt);

        Button loadBtn = findViewById(R.id.btnLoadEncrypted);
        Button saveBtn = findViewById(R.id.btnSaveDecrypted);

        loadBtn.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("*/*");
            openPicker.launch(i);
        });

        saveBtn.setOnClickListener(v -> {
            if (decryptedBlob == null) {
                Toast.makeText(this, "Load encrypted file first!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            i.setType("application/octet-stream");
            i.putExtra(Intent.EXTRA_TITLE, "decrypted_output.bin");
            savePicker.launch(i);
        });
    }
}
