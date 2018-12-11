package hcmus.mdsd.itusgallery;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

public class ShareMemoryActivity extends AppCompatActivity {
    ImageView imageView;
    Button btnShare,btnCannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_memory);
        imageView=(ImageView) findViewById(R.id.image_view_notification);
        Uri path = Uri.fromFile(new File(MainActivity.pathImage));
        Glide.with(getApplicationContext())
                .load(path)
                .apply(new RequestOptions()
                        .placeholder(null).centerCrop())
                .into(imageView);

        btnShare=(Button) findViewById(R.id.share_notification);
        btnCannel =(Button) findViewById(R.id.cannel_notification);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Intent.createChooser(emailIntent(), "Share image using"));
            }
        });
        btnCannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareMemoryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private Intent emailIntent() {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        final File photoFile = new File(MainActivity.pathImage);
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getApplicationContext(), "hcmus.mdsd.itusgallery", photoFile));
        return shareIntent;
    }
}
