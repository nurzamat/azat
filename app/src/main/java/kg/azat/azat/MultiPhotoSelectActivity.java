package kg.azat.azat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import kg.azat.azat.adapter.GalleryImageAdapter;
import kg.azat.azat.helpers.Constants;
import kg.azat.azat.helpers.GlobalVar;
import kg.azat.azat.helpers.ItemOffsetDecoration;

/**
 * Gallery
 */
public class MultiPhotoSelectActivity extends AppCompatActivity {

    private GalleryImageAdapter imageAdapter;
    private String TAG = MultiPhotoSelectActivity.class.getSimpleName();
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private int columnWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //toolbar.setNavigationIcon(R.drawable.ic_exit);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        populateImagesFromGallery();
    }

    public void btnChoosePhotosClick()
    {
        ArrayList<String> selectedItems = imageAdapter.getCheckedItems();
        //Toast.makeText(MultiPhotoSelectActivity.this, "Total photos selected:: " + selectedItems.size(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Selected Items: " + selectedItems.toString());

        final int len = selectedItems.size();
        int cnt = 0;
        String selectImages = "";
        //GlobalVar._bitmaps.clear();
        GlobalVar.image_paths.clear();
        for (int i =0; i<len; i++)
        {
            cnt++;
            if(i < Constants.MAX_IMAGES){

                String path = selectedItems.get(i);
                //GlobalVar._bitmaps.add(decodeFile(path, columnWidth, columnWidth));
                GlobalVar.image_paths.add(path);
            }
        }
        if (cnt == 0){
            Toast.makeText(getApplicationContext(),
                    "Please select at least one image",
                    Toast.LENGTH_LONG).show();
        } else {

            Log.d("SelectedImages", selectImages);
            finish();
        }
    }

    private void populateImagesFromGallery() {
        if (!isStoragePermissionGranted()) {
            return;
        }

        ArrayList<String> imageUrls = loadPhotosFromNativeGallery();
        initializeRecyclerView(imageUrls);
        //initColumnWidth();
    }

    private ArrayList<String> loadPhotosFromNativeGallery() {
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        Cursor imagecursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy + " DESC");

        ArrayList<String> imageUrls = new ArrayList<String>();

        for (int i = 0; i < imagecursor.getCount(); i++) {
            imagecursor.moveToPosition(i);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            imageUrls.add(imagecursor.getString(dataColumnIndex));

            //System.out.println("=====> Array path => "+imageUrls.get(i));
        }

        return imageUrls;
    }

    private void initializeRecyclerView(ArrayList<String> imageUrls) {
        imageAdapter = new GalleryImageAdapter(this, imageUrls);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),4);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.item_offset));
        recyclerView.setAdapter(imageAdapter);
    }

    public void initColumnWidth()
    {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                Constants.GRID_PADDING, r.getDisplayMetrics());

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        columnWidth = (int) ((screenWidth - ((3 + 1) * padding)) / 3);
    }

    public Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
        try {

            File f = new File(filePath);

            BitmapFactory.Options o = new BitmapFactory.Options();
            // o.inJustDecodeBounds = true;
            o.inJustDecodeBounds = false;
            o.inPreferredConfig = Bitmap.Config.RGB_565;
            o.inDither = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            Intent in = new Intent(MultiPhotoSelectActivity.this, MultiPhotoSelectActivity.class);
            MultiPhotoSelectActivity.this.startActivity(in);
            finish();
        }
         */
        try
        {
            GlobalVar.mSparseBooleanArray.clear();
            Context context = AddPostActivity.context;
            Intent in = new Intent(context, MultiPhotoSelectActivity.class);
            context.startActivity(in);
            finish();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){

            Intent in = new Intent(this, MultiPhotoSelectActivity.class);
            startActivity(in);
        }
        finish();
    }

    public  boolean isStoragePermissionGranted()
    {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_camera)
        {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            return true;
        }

        if (id == R.id.action_select)
        {
            btnChoosePhotosClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}
