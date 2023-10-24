package mx.gob.puentesfronterizos.lineaexpres.ui.fixdata;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentFixDataBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;
import mx.gob.puentesfronterizos.lineaexpres.ui.fixdata.FixDataViewModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FixDataFragment extends Fragment {
    private static final String TAG = "FixData";

    TextView TramiteTitle;
    TextView DocoumentTitle;
    TextView ReazonTitle;
    ImageView imagePrev;
    Button takePhoto;
    Button ConfirmAndUpload;

    UserLog userLog;
    updateData UpdateData;
    String PhotoUriTemp;
    String currentPhotoPath;
    ArrayList<String> FixedData;
    String Token;

    SignaturePad mSignaturePad;
    RelativeLayout SignContainer;
    Button SendSign;
    Button clear_button;

    LayoutInflater popupInflater;
    View popup_View;
    View popup_view;
    int popup_width;
    int popup_height;
    PopupWindow popup_Window;
    TextView popup_Head;
    TextView popup_Body;
    Uri photoFixed = null;

    Handler handler;
    int counter;

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private FragmentFixDataBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FixDataViewModel FixDataViewModel = new ViewModelProvider(this).get(FixDataViewModel.class);
        binding = FragmentFixDataBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userLog = new UserLog(requireContext()); //Open Users db
        UpdateData = new updateData(requireContext());

        TramiteTitle = binding.TramiteTitle;
        DocoumentTitle = binding.DocoumentTitle;
        ReazonTitle = binding.ReazonTitle;
        imagePrev = binding.imagePrev;
        takePhoto = binding.takePhoto;
        ConfirmAndUpload = binding.ConfirmAndUpload;
        mSignaturePad = binding.signaturePad;
        SendSign = binding.SendSign;
        SignContainer = binding.SignContainer;
        clear_button = binding.clearButton;

        popupInflater = (LayoutInflater) requireActivity().getSystemService(requireActivity().LAYOUT_INFLATER_SERVICE);
        popup_View = popupInflater.inflate(R.layout.popup_top, null);
        popup_view = SendSign.getRootView();
        popup_width = LinearLayout.LayoutParams.WRAP_CONTENT;
        popup_height = LinearLayout.LayoutParams.WRAP_CONTENT;
        popup_Window = new PopupWindow(popup_View, popup_width, popup_height, false);
        popup_Head = popup_View.findViewById(R.id.popupHead);
        popup_Body = popup_View.findViewById(R.id.popupBody);

        String TramTitle = "";

        Token = userLog.GetUserData().get(1);

        FixedData = UpdateData.getFixedData();
        clear_button.setOnClickListener(view -> {
            mSignaturePad.clearView();
        });


        String id_proc = FixedData.get(0);
        String id_proc_type = FixedData.get(1);
        String TipoTramite = FixedData.get(2);
        String id_file_type = FixedData.get(3);
        String TramiteDesc = FixedData.get(4);
        String TramiteComment = FixedData.get(5);

        System.out.println("Tramite de file" + FixedData);


        if (id_proc_type.equals("1")) {
            TramTitle = "Solicitud Inscripción";
        }

        TramiteTitle.setText(TramTitle);
        DocoumentTitle.setText("Documento rechazado\n" + TramiteDesc);
        ReazonTitle.setText("Razón de rechazo\n" + TramiteComment);

        takePhoto.setOnClickListener(view -> {
            takePicture("replace");
            imagePrev.setVisibility(View.VISIBLE);
        });

        if (TramiteDesc.equals("Firma")) {
            imagePrev.setVisibility(View.GONE);
            takePhoto.setVisibility(View.GONE);
            ConfirmAndUpload.setVisibility(View.GONE);
        }else {
            SignContainer.setVisibility(View.GONE);
            SendSign.setVisibility(View.GONE);
        }

        //Permissions
        if (checkPermission()) {
            Log.i("InscriptionReq", "onCreate: Permissions Ok");
        } else {
            requestPermission(); // Request Permission
        }
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult( ActivityResult result ) {
                System.out.println("yesPerm Almacenamiento");
            }
        });
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
        };
        if (!hasPermissions(requireContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, PERMISSION_ALL);
        }

        counter = 5000;
        handler = new Handler();


        final int[] Firma = {0};
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                Firma[0] = 1;
            }

            @Override
            public void onSigned() {
                Firma[0] = 1;
            }

            @Override
            public void onClear() {
                Firma[0] = 0;
            }
        });
        SendSign.setOnClickListener(view -> {
            Bitmap sign = mSignaturePad.getSignatureBitmap();


            if (Firma[0] == 0) {
                popup_Head.setText("Firma no válida");
                popup_Body.setText("Debe firmar el documento.");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }

            File a = bitmapToFile(requireContext(), sign, "Solicitud_Firma.png");
            try {
                sendFile(id_proc, String.valueOf(a), id_file_type, id_proc_type, "firm");
            } catch (Exception e) {
                e.printStackTrace();
            }

        });



        ConfirmAndUpload.setOnClickListener(view -> {

            try {
                FixedData = UpdateData.getFixedData();
                String ImageLocation = FixedData.get(6);
                sendFile(id_proc, ImageLocation, id_file_type, id_proc_type, "none");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return root;
    }


    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void show_Notification(String msgTitle, String msgBody){

        Intent intent=new Intent(requireContext(),MainActivity.class);
        String CHANNEL_ID="MYCHANNEL";
        NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"All", NotificationManager.IMPORTANCE_HIGH);
        PendingIntent pendingIntent=PendingIntent.getActivity(requireContext(),1,intent,PendingIntent.FLAG_IMMUTABLE);
        Notification notification=new Notification.Builder(requireContext(),CHANNEL_ID)
                .setContentText(msgBody)
                .setContentTitle(msgTitle)
                .setContentIntent(pendingIntent)
                .setChannelId(CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),                                                                                                 R.drawable.ic_stat_name))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .build();

        NotificationManager notificationManager=(NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(1,notification);
    }

    public void sendFile(String Tramite_ID, String FileUpdate, String FileType, String id_proc_type, String type) throws Exception {
        new Thread(() -> {
            try {
                System.out.println("Este Tramite_ID :" + Tramite_ID);
                System.out.println("Este FileUpdate :" + FileUpdate);
                System.out.println("Este FileType :" + FileType);
                Uri uri = null;
                if (photoFixed != null) {uri = photoFixed; photoFixed = null;}
                System.out.println("Este para el filePath?: " + uri);

                String filePath = null;
                String[] projection = {MediaStore.Images.Media.DATA};
                if (uri != null) {
                    if (DocumentsContract.isDocumentUri(requireContext(), uri)) {
                        String docId = DocumentsContract.getDocumentId(uri);
                        String mimeType = requireActivity().getContentResolver().getType(uri);
                        if (mimeType != null && mimeType.startsWith("image/")) {
                            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            String selection = MediaStore.Images.Media._ID + "=?";

                            String[] selectionArgs = new String[]{docId.substring(6)};
                            Cursor cursor = requireActivity().getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                                filePath = cursor.getString(columnIndex);
                                System.out.println("Este es el filePath: " + filePath);
                                RequestBody requestBody = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("id_proc", Tramite_ID)
                                        .addFormDataPart("docfile", "img", RequestBody.create(MEDIA_TYPE_PNG, new File(String.valueOf(filePath))))
                                        .addFormDataPart("filetype", FileType)
                                        .addFormDataPart("id_proc_type", id_proc_type)
                                        .build();
                                Request request = new Request.Builder()
                                        .header("Authorization", "Bearer " + Token)
                                        .url("https://lineaexpressapp.desarrollosenlanube.net/api/v1/files")
                                        .post(requestBody)
                                        .build();
                                Response response = client.newCall(request).execute();
                                if (!response.isSuccessful()) {
                                    requireActivity().runOnUiThread(() -> {
                                        ConfirmAndUpload.setFocusable(false);
                                        ConfirmAndUpload.setEnabled(false);

                                        popup_Head.setText("Ha habido un error enviando el documento, reintentalo nuevamente.");
                                        popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                                        handler.postDelayed(() -> popup_Window.dismiss(), counter);

                                        MainActivity.Navigation_Requests("BackView");
                                    });
                                    throw new IOException("Unexpected code " + response);

                                }


                                if (response.body().string().contains("Archivo recibido")) {
                                    requireActivity().runOnUiThread(() -> {
                                        ConfirmAndUpload.setFocusable(false);
                                        ConfirmAndUpload.setEnabled(false);

                                        popup_Head.setText("Documento enviado");
                                        popup_Body.setText("Puedes continuar explorando.");
                                        popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                                        handler.postDelayed(() -> popup_Window.dismiss(), counter);

                                        MainActivity.Navigation_Requests("BackView");
                                    });


                                }
                                System.out.println(response.body().string());
                                cursor.close();
                            }
                        } else if (mimeType != null && mimeType.equals("application/pdf")) {
                            try {
                                String numericPart = docId.substring(docId.indexOf(":") + 1);
                                long id = Long.parseLong(numericPart);
                                Uri contentUri = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id);
                                String selection = null;
                                String[] selectionArgs = null;
                                Cursor cursor = requireActivity().getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
                                    filePath = cursor.getString(columnIndex);

                                    RequestBody requestBody = new MultipartBody.Builder()
                                            .setType(MultipartBody.FORM)
                                            .addFormDataPart("id_proc", Tramite_ID)
                                            .addFormDataPart("docfile", "document", RequestBody.create(MediaType.parse("application/pdf"), new File(filePath)))
                                            .addFormDataPart("filetype", FileType)
                                            .addFormDataPart("id_proc_type", id_proc_type)
                                            .build();

                                    Request request = new Request.Builder()
                                            .header("Authorization", "Bearer " + Token)
                                            .url("https://lineaexpressapp.desarrollosenlanube.net/api/v1/files")
                                            .post(requestBody)
                                            .build();
                                    Response response = client.newCall(request).execute();
                                    if (!response.isSuccessful()) {
                                        requireActivity().runOnUiThread(() -> {
                                            ConfirmAndUpload.setFocusable(false);
                                            ConfirmAndUpload.setEnabled(false);

                                            popup_Head.setText("Ha habido un error enviando el documento, reintentalo nuevamente.");
                                            popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                                            handler.postDelayed(() -> popup_Window.dismiss(), counter);

                                            MainActivity.Navigation_Requests("BackView");
                                        });
                                        throw new IOException("Unexpected code " + response);

                                    }
                                    if (response.body().string().contains("Archivo recibido")) {
                                        requireActivity().runOnUiThread(() -> {
                                            ConfirmAndUpload.setFocusable(false);
                                            ConfirmAndUpload.setEnabled(false);

                                            popup_Head.setText("Documento enviado");
                                            popup_Body.setText("Puedes continuar explorando.");
                                            popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                                            handler.postDelayed(() -> popup_Window.dismiss(), counter);

                                            MainActivity.Navigation_Requests("BackView");
                                        });


                                    }
                                    System.out.println(response.body().string());
                                    cursor.close();
                                }
                            } catch (Exception e) {
                                System.out.println("Es el error: " + e);
                            }
                        }
                    }
                }else {
                    try {
                        String imageLocation = FileUpdate.substring(FileUpdate.lastIndexOf('/') + 1);
                        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/Data/mx.gob.puentesfronterizos.lineaexpres/files/Pictures/" + imageLocation);

                        //Start to change width, but firstly changing to Bitmap

                        Matrix matrix = new Matrix();
                        matrix.postRotate(0);
                        Bitmap b = BitmapFactory.decodeFile(storageDir.getAbsolutePath());

                        Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);

                        // original measurements
                        int origWidth = b2.getWidth();
                        int origHeight = b2.getHeight();

                        final int destWidth = 1024;//or the width you need

                        if(origWidth > destWidth){
                            // picture is wider than we want it, we calculate its target height
                            int destHeight = origHeight/( origWidth / destWidth ) ;
                            //Rotate again the image

                            // we create an scaled bitmap so it reduces the image, not just trim it
                            Bitmap b3 = Bitmap.createScaledBitmap(b2, destWidth, destHeight, false);
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            // compress to the format you want, JPEG, PNG...
                            // 70 is the 0-100 quality percentage
                            //b2.compress(Bitmap.CompressFormat.JPEG,70 , outStream);


                            b3.compress(Bitmap.CompressFormat.JPEG,70 , outStream);
                            // we save the file, at least until we have made use of it
                            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/Data/mx.gob.puentesfronterizos.lineaexpres/files/Pictures/" + imageLocation);
                            //File f = new File(Environment.getExternalStorageDirectory() + File.separator + "test.jpg");
                            f.createNewFile();
                            //write the bytes in file
                            FileOutputStream fo = new FileOutputStream(f);
                            fo.write(outStream.toByteArray());
                            // remember close de FileOutput
                            fo.close();
                        }
                        //Finishing to change width, but firstly changing to Bitmap

                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("id_proc", Tramite_ID)
                                .addFormDataPart("docfile", imageLocation, RequestBody.create(MEDIA_TYPE_PNG, new File(String.valueOf(storageDir))))
                                .addFormDataPart("filetype", FileType)
                                .addFormDataPart("id_proc_type", id_proc_type)
                                .build();

                        Request request = new Request.Builder()
                                .header("Authorization", "Bearer " + Token)
                                .url("https://lineaexpressapp.desarrollosenlanube.net/api/v1/files")
                                .post(requestBody)
                                .build();

                        Response response = client.newCall(request).execute();
                        if (!response.isSuccessful()) {
                            requireActivity().runOnUiThread(() -> {
                                ConfirmAndUpload.setFocusable(false);
                                ConfirmAndUpload.setEnabled(false);

                                popup_Head.setText("Ha habido un error enviando el documento, reintentalo nuevamente.");
                                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                                handler.postDelayed(() -> popup_Window.dismiss(), counter);

                                MainActivity.Navigation_Requests("BackView");
                            });
                            throw new IOException("Unexpected code " + response);

                        }
                        if (response.body().string().contains("Archivo recibido")) {
                            requireActivity().runOnUiThread(() -> {
                                ConfirmAndUpload.setFocusable(false);
                                ConfirmAndUpload.setEnabled(false);

                                popup_Head.setText("Documento enviado");
                                popup_Body.setText("Puedes continuar explorando.");
                                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                                handler.postDelayed(() -> popup_Window.dismiss(), counter);

                                MainActivity.Navigation_Requests("BackView");
                            });


                        }

                        System.out.println(response.body().string());
                    }catch (Exception e){
                        Log.e("SendFiles", "sendFile: ", e);
                    }
                }

            }catch (Exception e){
                Log.e("SendFiles", "sendFile: ", e);
            }
        }).start();
    }

    public File bitmapToFile(Context context, Bitmap bitmap, String fileNameToSave) {
        File file = null;
        try {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/Data/mx.gob.puentesfronterizos.lineaexpres/files/Pictures/" + fileNameToSave);
            file.createNewFile();
            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 , bos); // YOU can also save it in JPEG
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;
        }catch (Exception e){
            e.printStackTrace();
            return file; // it will return null
        }
    }

    //Take photo
    static final int REQUEST_TAKE_PHOTO = 1;
    private void takePicture(String imageName) {


        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png", "image/*"};

        Intent pickPdfIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickPdfIntent.setType("application/pdf");
        pickPdfIntent.putExtra(Intent.EXTRA_MIME_TYPES, "application/pdf");
        pickPhoto.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = null;
        try {
            imageFile = createImageFile(imageName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (imageFile != null) {
            Uri photoURI = FileProvider.getUriForFile(requireContext(), "mx.gob.puentesfronterizos.lineaexpres", imageFile);
            openCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            UpdateData.FixImgLocation(photoURI.toString());
            PhotoUriTemp = photoURI.toString();
            System.out.println("ImageFile" + imageFile);
            System.out.println("photouri" + photoURI);
            System.out.println("getExtras" + openCamera.getExtras());

            Intent chooserIntent = Intent.createChooser(new Intent(), "Selecciona una opción");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{openCamera, pickPhoto});

            startActivityForResult(chooserIntent, REQUEST_TAKE_PHOTO);
        }
    }
    private File createImageFile(String imageName) throws IOException {
        // Create an image file name
        String imageFilename = imageName + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFilename, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();


        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;

        if (resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                //Manejamos datos de la galeria
                Uri selectedImage = data.getData();
                photoFixed = selectedImage;

                try {
                    String mimeType = requireActivity().getContentResolver().getType(selectedImage);
                    if (mimeType != null && mimeType.equals("application/pdf")) {
                        imagePrev.setImageResource(R.drawable.pdfico);
                        UpdateData.FixImgLocation(selectedImage.toString());
                        System.out.println("Es un pdf");
                    } else if (mimeType != null && mimeType.startsWith("image/")) {
                        UpdateData.FixImgLocation(selectedImage.toString());
                        bitmap = rotateImage(MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImage), 0);

                        imagePrev.setImageBitmap(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                //Manejamos datos de la camara
                String bitmapImage = PhotoUriTemp;
                try {
                    System.out.println("Lo de la camara es: " + bitmapImage.toString());
                    bitmap = rotateImage(MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), Uri.parse(bitmapImage)), 90);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imagePrev.setImageBitmap(bitmap);
            }
        }


        if (resultCode == RESULT_CANCELED) {
            imagePrev.setImageResource(0);
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) throws IOException {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap b2 = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        // original measurements
        int origWidth = b2.getWidth();
        int origHeight = b2.getHeight();

        final int destWidth = origWidth / 3;
        final int destHeight = origHeight / 3;
        Bitmap b3 = null;
        b3 = Bitmap.createScaledBitmap(b2, destWidth, destHeight, false);

        return b3;
    }

    //Permission
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int readCheck = ContextCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE);
            int writeCheck = ContextCompat.checkSelfPermission(requireContext(), WRITE_EXTERNAL_STORAGE);
            int cameraCheck = ContextCompat.checkSelfPermission(requireContext(), CAMERA);
            return readCheck == PackageManager.PERMISSION_GRANTED && writeCheck == PackageManager.PERMISSION_GRANTED && cameraCheck == PackageManager.PERMISSION_GRANTED;
        }
    }

    private String[] permissions = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Permisos de almacenamiento")
                    .setMessage("Al dar 'Aceptar' se abrirá un menú, en el cual deberá seleccionar la aplicación y dar los permisos \n 'Permitir administrar todos los archivos'' ")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick( DialogInterface dialog, int which ) {
                            try {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                intent.addCategory("android.intent.category.DEFAULT");
                                intent.setData(Uri.parse(String.format("package:%s", requireContext().getPackageName())));
                                activityResultLauncher.launch(intent);
                            } catch (Exception e) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                activityResultLauncher.launch(intent);
                            }
                        }
                    })
                    .setCancelable(false)
                    .show();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), permissions, 30);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
