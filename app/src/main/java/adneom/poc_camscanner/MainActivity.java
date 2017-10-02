package adneom.poc_camscanner;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import adneom.poc_camscanner.Utils.Utils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button normal;
    private Button camScanner;
    private ImageView preview;

    private final String ACTION_SCAN = "com.intsig.camscanner.ACTION_SCAN";
    private final String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/sourceScan.jpg";
    private final String PATH_ORG = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ORGScan.jpg";

    private boolean isNormal = false;

    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        normal = (Button) findViewById(R.id.process_normal);
        camScanner = (Button) findViewById(R.id.process_scam_scanner);
        preview = (ImageView) findViewById(R.id.preview);
        normal.setOnClickListener(this);
        camScanner.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.process_normal:
                isNormal = true;
                if (Utils.isCamScannerInstalled(this)) {
                    createDialog();
                   // Utils.launchApplication(Utils.PACKAGE_NAME_CAM_SCANNER, this);
                } else {
                    //market
                    Utils.redirectToMarket(Utils.PACKAGE_NAME_CAM_SCANNER, this);
                }
                break;
            case R.id.process_scam_scanner:
                isNormal = false;
                if (Utils.isCamScannerInstalled(this)) {
                    //API
                    /*if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        ActivityCompat.requestPermissions(this,perms,Utils.REQUEST_CODE_PERMISSIONS);

                    }else{
                        launchCamScannerAPI();
                    }*/
                    launchCamScannerAPI();
                } else {
                    //market
                    Utils.redirectToMarket(Utils.PACKAGE_NAME_CAM_SCANNER, this);
                }
                break;
        }
    }

    private void launchCamScannerAPI() {
        Intent intent = new Intent(ACTION_SCAN);
        // Or content uri picked from gallery
        File file = new File(PATH);
        Uri uri = FileProvider.getUriForFile(this, getPackageName(), file);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra("scanned_image", PATH);
        intent.putExtra("pdf_path", PATH);
        intent.putExtra("org_image", PATH_ORG);
        startActivityForResult(intent, Utils.REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Test",""+isNormal);
        if (isNormal) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this, perms, Utils.PERMISSION_READ_EXTERNAL);

            } else {
                redirectToGallery();
            }
        }
    }

    private void redirectToGallery() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intentGallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intentGallery.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intentGallery.setType("*/*");

        startActivityForResult(intentGallery, Utils.CODE_GALLERY_RESULT);

    }

    private void createDialog(){
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog);

        Button btn = (Button)dialog.findViewById(R.id.btn_ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                    Utils.launchApplication(Utils.PACKAGE_NAME_CAM_SCANNER, MainActivity.this);
                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Utils.REQUEST_CODE) {
            Log.i("Test", "all is OK :)");
        }

        if (requestCode == Utils.CODE_GALLERY_RESULT) {
            isNormal = false;
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Log.i("Test","uri is "+uri.toString());
                camScanner.setVisibility(View.GONE);
                normal.setVisibility(View.GONE);
                preview.setVisibility(View.VISIBLE);
                Picasso.with(this).load(uri).into(preview);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 11:
                if (grantResults.length > 0) {
                    launchCamScannerAPI();
                }
                break;
            case 12:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createDialog();
                }
                break;
        }
    }
}
