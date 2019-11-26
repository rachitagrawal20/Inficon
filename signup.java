package infiniteconnections.inficon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class signup extends AppCompatActivity{

    private static final int CAMERA_REQUEST = 1;
    private ImageView imageView;
    EditText name,contactno,facebookURL,codechef,linkedin;
    String name1,contactno1,facebookURL1,codechef1,linkedin1;
    public static final String Firebase_Server_URL = "https://inficon-9f65e.firebaseio.com/";
    Firebase firebase;

    DatabaseReference databaseReference;
    public static final String Database_Path = "Signup_Details";
    String imageEncoded,p;
    ArrayList<String> Userlist;
    FirebaseStorage storage;
    StorageReference storageReference;
    Uri uri;
    Kairos myKairos = new Kairos();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_signup);
        firebase = new Firebase(Firebase_Server_URL);
        databaseReference = FirebaseDatabase.getInstance().getReference("Signup_Details/email");
        String app_id = "1504730d";
        String api_key = "0d094d888f18f07e3b3281a2a3a0350f";
        myKairos.setAuthentication(this, app_id, api_key);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Userlist = new ArrayList<String>();
                //Signupdetails signup;

                Toast.makeText(signup.this, dataSnapshot.getValue(String.class), Toast.LENGTH_SHORT).show();
                //if (dataSnapshot.getChildren()==null)
                    //Toast.makeText(signup.this, "Khaali h bhaaauuuu", Toast.LENGTH_SHORT).show();


                for ( DataSnapshot dsp :dataSnapshot.getChildren() ) {
                    //signup =
                    //String s  = signup.getEmail();
                    Log.d("hello", "everyone");
                    Toast.makeText(signup.this, dsp.getValue(String.class)+"hsuhsudhsu", Toast.LENGTH_SHORT).show();
                    Userlist.add(dsp.getValue(String.class)); //add result into array list

                }
                Toast.makeText(signup.this,Userlist.size()+"",Toast.LENGTH_SHORT).show();

                for (int i = 0 ; i<Userlist.size();i++){
                    p = Userlist.get(i);
                    if(p.equals(FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getEmail())){
                        Log.d("sadasdsd","Sdsadasd");
                    Intent myIntent1 = new Intent(signup.this, AppStart.class);
                    startActivity(myIntent1);
                }

            }}
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        imageView = (ImageView) findViewById(R.id.profile_img);
        ImageButton pickImageButton = (ImageButton) findViewById(R.id.imgbutton);
        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);*/
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, CAMERA_REQUEST);

            }
        });
        Firebase.setAndroidContext(signup.this);




        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        name = (EditText) findViewById(R.id.name);
        facebookURL = (EditText) findViewById(R.id.facebook);
        linkedin = (EditText) findViewById(R.id.linkedin);
        codechef = (EditText) findViewById(R.id.codechef);
        contactno = (EditText) findViewById(R.id.contact);
        Button button = (Button) findViewById(R.id.signup);
        final String s = FirebaseAuth.getInstance()
                .getCurrentUser()
                .getEmail();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                GetDataFromEditText();
                Signupdetails signupdetails = new Signupdetails();
                signupdetails.setEmail(s);
                signupdetails.setName(name1);
                signupdetails.setCodechef(codechef1);
                signupdetails.setContactno(contactno1);
                signupdetails.setFacebookURL(facebookURL1);
                signupdetails.setLinkedin(linkedin1);
               // signupdetails.setImageURL(imageEncoded);
                String s = databaseReference.push().getKey();
                databaseReference.child(FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getEmail().replace(".",",")).setValue(signupdetails);
                if(uri != null)
                {
                    final ProgressDialog progressDialog = new ProgressDialog(signup.this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();

                    StorageReference ref = storageReference.child("images/"+ FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getEmail().replace(".",","));
                    ref.putFile(uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(signup.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                    //databaseReference.child("ImageURL").setValue(uri.toString());
                                    databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path + "/"+FirebaseAuth.getInstance()
                                            .getCurrentUser()
                                            .getEmail().replace(".",","));
                                    storageReference.child("images/"+FirebaseAuth.getInstance()
                                            .getCurrentUser()
                                            .getEmail().replace(".",",")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            //Toast.makeText(AppStart.this,uri.toString(), Toast.LENGTH_SHORT).show();
                                            KairosListener listener = new KairosListener() {

                                                @Override
                                                public void onSuccess(String response) {
                                                    // your code here!
                                                    Toast.makeText(signup.this, response, Toast.LENGTH_SHORT).show();
                                                    Log.d("KAIROS DEMO", response);
                                                }

                                                @Override
                                                public void onFail(String response) {
                                                    // your code here!
                                                    Log.d("KAIROS DEMO", response);
                                                }
                                            };
                                            String image = uri.toString();
                                            String subjectId = FirebaseAuth.getInstance()
                                                    .getCurrentUser()
                                                    .getEmail();
                                            String galleryId = "RTC";
                                            try{myKairos.enroll(image, subjectId, galleryId, null, null, null, listener);}
                                            catch (Exception j){}

                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                                      @Override
                                                                      public void onFailure(@NonNull Exception e) {
                                                                          progressDialog.dismiss();
                                                                          Toast.makeText(signup.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                      }
                                                                  });

                                            databaseReference.child("ImageURL").setValue(uri.toString());
                                            // Got the download URL for 'users/me/profile.png'
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle any errors
                                        }
                                    })

                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                            .getTotalByteCount());
                                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                                }
                            });
                }
                Toast.makeText(signup.this,"Data Inserted Successfully into Firebase Database", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(signup.this, AppStart.class);
                startActivity(myIntent);
            }});


    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            uri = data.getData();

            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageURI(uri);

            //encodeBitmapAndSaveToFirebase(imageBitmap);
        }
        }
    public void GetDataFromEditText(){

        name1 = name.getText().toString().trim();
        contactno1 = contactno.getText().toString().trim();
        facebookURL1 = facebookURL.getText().toString().trim();
        codechef1 = codechef.getText().toString().trim();
        linkedin1 = linkedin.getText().toString().trim();

    }
    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

    }}
