package infiniteconnections.inficon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.firebase.client.Firebase;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static com.google.android.gms.internal.zzbfq.NULL;


public class AppStart extends AppCompatActivity {
    public static final String Firebase_Server_URL = "https://inficon-9f65e.firebaseio.com/";
    Firebase firebase;
    JSONObject jresponse;
    String ghotresponse;

    DatabaseReference databaseReference;
    public static final String Database_Path = "Signup_Details";
    FirebaseStorage storage;
    StorageReference storageReference;
    Kairos myKairos = new Kairos();
    int CAMERA_REQUEST = 1;
    InputStream image_stream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);
        Firebase.setAndroidContext(AppStart.this);
        // instantiate a new kairos instance


// set authentication
        String app_id = "1504730d";
        String api_key = "0d094d888f18f07e3b3281a2a3a0350f";
        myKairos.setAuthentication(this, app_id, api_key);

        firebase = new Firebase(Firebase_Server_URL);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        //Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        //startActivity(intent);


// Alternatively way to get download URL
        Button button = (Button) findViewById(R.id.button_show);
        final TextView textView = (TextView) findViewById(R.id.show_uri);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*storageReference.child("images/"+FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getEmail().replace(".",",")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Toast.makeText(AppStart.this,uri.toString(), Toast.LENGTH_SHORT).show();
                        KairosListener listener = new KairosListener() {

                            @Override
                            public void onSuccess(String response) {
                                // your code here!
                                Toast.makeText(AppStart.this, response, Toast.LENGTH_SHORT).show();
                                Log.d("KAIROS DEMO", response);
                            }

                            @Override
                            public void onFail(String response) {
                                // your code here!
                                Log.d("KAIROS DEMO", response);
                            }
                        };
                        String image = uri.toString();

                        try{myKairos.detect(image, null, null, listener);}

                        catch (UnsupportedEncodingException e){}
                        catch (JSONException j){}
                        // Got the download URL for 'users/me/profile.png'
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });*/
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, CAMERA_REQUEST);
            }
        });



    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(AppStart.this,
                                    "You have been signed out.",
                                    Toast.LENGTH_LONG)
                                    .show();

                            // Close activity
                            /*Intent intent =  new Intent(AppStart.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("EXIT", true);
                            startActivity(intent);*/
                            finishAffinity();
                            startActivity(new Intent(AppStart.this, MainActivity.class));

                        }
                    });
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
             Uri uri = data.getData();

            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            KairosListener listener = new KairosListener() {
            String message=null;
                JSONObject jsonObject1;
                @Override
                public void onSuccess(String response) {
                    try {
                        jresponse = new JSONObject(response);
                        JSONArray jsonArray = jresponse.getJSONArray("images");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        jsonObject1 = jsonObject.getJSONObject("transaction");
                        Log.d("sadasdasda",jsonObject1.toString());
                        String string= jsonObject1.getString("subject_id");

                        Toast.makeText(AppStart.this, string, Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        Toast.makeText(AppStart.this, "Match not found", Toast.LENGTH_LONG).show();
                    }
                    Log.d("KAIROS DEMO", response);

                }

                @Override
                public void onFail(String response) {
                    // your code here!
                    Log.d("KAIROS DEMO", response);
                }
            };
            //String image = "http://media.kairos.com/liz.jpg";
            try{ image_stream = getContentResolver().openInputStream(uri);}
            catch (Exception e){}
            Bitmap bitmap= BitmapFactory.decodeStream(image_stream );
            String galleryId = "RTC";
            try{myKairos.recognize(bitmap, galleryId, null, null, null, null, listener);}
            catch (Exception e){}



            //encodeBitmapAndSaveToFirebase(imageBitmap);
        }
    }
}
/*+ FirebaseAuth.getInstance()
        .getCurrentUser()
        .getEmail().replace(".",",")*/
