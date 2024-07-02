package mx.gob.puentesfronterizos.lineaexpres;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import mx.gob.puentesfronterizos.lineaexpres.databinding.ActivityMainBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.SQLOnInit;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main Activity";
    private static NavController navController;
    private static NavOptions navOptions;
    private AppBarConfiguration mAppBarConfiguration;

    DrawerLayout drawer;
    NavigationView navigationView;
    BottomAppBar bottomAppBar;

    UserLog userlog;
    updateData UpdateData;
    SQLOnInit sqlOnInit;
    ArrayList<String> userData;
    String User;
    String Token;
    UserLog userLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mx.gob.puentesfronterizos.lineaexpres.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
     
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.action_image);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.line_3_horizontal); // Establecer el icono de navegación deseado

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acciones a realizar cuando se hace clic en el ícono de navegación
                // Por ejemplo, puedes abrir el menú de navegación lateral
                drawer.openDrawer(GravityCompat.START);
            }
        });

        //victorlazaroc521@gmail.com//

        ImageButton img = findViewById(R.id.goToHome);

        Glide.with(this)
                .load("https://lineaexpress.desarrollosenlanube.net/wp-content/uploads/2022/07/Cabezal714x119_Color.png")
                .into(img);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#121212")));

        //Database Declarations
        userlog = new UserLog(this);
        UpdateData = new updateData(this);
        sqlOnInit = new SQLOnInit(this);

        userData = userlog.GetUserData();
        User = userData.get(0);
        Token = userData.get(1);

        getFireToken();

        ImageButton goToHome = findViewById(R.id.goToHome);
        goToHome.setOnClickListener(v -> {
            String currentLocation = navController.getCurrentDestination().getLabel().toString();
            if (!currentLocation.contains("Inicio")){
                navController.navigate(R.id.navigation_home);
            }
        });

        //public items
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        bottomAppBar = findViewById(R.id.bottomAppBar);

        //Bottom Navigation Bar
        BottomNavigationView navView = findViewById(R.id.nav_view_bottom);
        int[] navigationIds = {
                R.id.navigation_home, R.id.navigation_contactanos, R.id.navigation_derechos, R.id.navigation_recharge, R.id.navigation_callus, R.id.navigation_formalities, R.id.navigation_privacy, R.id.navigation_services,
                R.id.navigation_lineamientos, R.id.navigation_whoweare, R.id.navigation_objectives, R.id.navigation_mision, R.id.navigation_vision, R.id.navigation_process,
                R.id.navigation_notas, R.id.navigation_login, R.id.navigation_register, R.id.navigation_forgotpass, R.id.navigation_CurrentRates, R.id.navigation_subservices,
                R.id.navigation_req_inscription, R.id.navigation_req_bridges, R.id.navigation_req_change_vehicle, R.id.navigation_req_balance_transfer, R.id.navigation_req_unsubscribe,
                R.id.navigation_devmode, R.id.navigation_logout, R.id.navigation_terms, R.id.navigation_recharge_amount, R.id.navigation_banortest, R.id.navigation_congratmsg,
                R.id.navigation_changepass, R.id.navigation_profile, R.id.navigation_cameras, R.id.navigation_sendpasschange, R.id.navigation_changeemail, R.id.navigation_tramites_pend,
                R.id.navigation_fix_data, R.id.navigation_citas, R.id.navigation_crearcita, R.id.navigation_cita_change_date, R.id.navigation_current_citas, R.id.navigation_current_change_citas,
                R.id.navigation_vehiculos, R.id.navigation_cambio_veh, R.id.navigation_req_cambio_tag, R.id.navigation_req_actualizacion_poliza, R.id.navigation_req_actualizacion_placas,
                R.id.navigation_req_baja_vehiculo, R.id.navigation_req_baja_tag, R.id.navigation_req_cambio_puente, R.id.navigation_req_cambio_convenio, R.id.navigation_req_transferencia_saldo,
                R.id.navigation_facturacion, R.id.navigation_vehiculos_perfil, R.id.navigation_choose_veh_add, R.id.navigation_linea_expres_add_vehicle, R.id.navigation_telepeaje_add_vehicle,
                R.id.navigation_adp_add_vehicle, R.id.navigation_mis_cruces,
        };


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navigationIds)
                .setOpenableLayout(drawer)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //LeftSide Navigation Bar
        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        navigationView.setBackgroundResource(R.color.black);
        mAppBarConfiguration = new AppBarConfiguration.Builder(navigationIds)
                .setOpenableLayout(drawer)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                drawer.closeDrawer(GravityCompat.START);
                nav_req(itemId);
                return false;
            }
        });
<<<<<<< Updated upstream
=======

        // aviso para las versiones de prueba
        /*
       Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Version de prueba de la app Puentes Fronterizos 170624");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }

        });
        builder.create().show();
        */

>>>>>>> Stashed changes
        //Floatin Action Button
        String WhtieColor = "#ffffff";
        FloatingActionButton a = findViewById(R.id.fab_button);
        a.setColorFilter(Color.parseColor(WhtieColor)); //Cerrado
        a.setOnClickListener(v -> {
            nav_req(R.id.navigation_vehiculos_perfil);
        });

        navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.le_fade)
                .setExitAnim(R.anim.le_fade)
                .build();

        navView.setOnItemSelectedListener(item -> {
            int Destination = item.getItemId();
            int currentLocation = navController.getCurrentDestination().getId();
            switch (Destination) {
                case R.id.navigation_home:
                    if (currentLocation != Destination) {
                        navController.navigate(R.id.navigation_home, null, navOptions);
                    }
                    break;
                case R.id.navigation_recharge:
                    if (currentLocation != Destination) {
                        navController.navigate(R.id.navigation_vehiculos_perfil, null, navOptions);
                    }
                    break;
                case R.id.navigation_callus:
                    if (currentLocation != Destination) {
                        navController.navigate(R.id.navigation_callus, null, navOptions);
                    }
                    break;
                case R.id.navigation_formalities:
                    if (currentLocation != Destination) {
                        navController.navigate(R.id.navigation_vehiculos, null, navOptions);
                    }
                    break;
                case R.id.navigation_profile:
                    if (currentLocation != Destination) {
                        navController.navigate(R.id.navigation_profile, null, navOptions);
                    }
                    break;

            }




            return true;
        });

       // downloadReporteDePuentesInfo();

       //int TimerDelay = 300000; // 5 Minutes
       //Handler handler = new Handler();
       //Runnable runnable = new Runnable()
       //{
       //    @Override
       //    public void run() {
       //       // downloadReporteDePuentesInfo();
       //        handler.postDelayed(this, TimerDelay);
       //    }
       //};
       //handler.postDelayed(runnable, TimerDelay);


    }



    public void getFireToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed" + task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        System.out.println("MainAct Token: " + token);
                        FirebaseMessaging.getInstance().setAutoInitEnabled(true);


                        userlog.UserFireToken("set", token);
                    }
                });
    }

    public static void Formalities_Requests(int destination) {
        try {
            int currentLocation = navController.getCurrentDestination().getId();
            switch (destination) {
                case 1:
                    if (currentLocation != destination) {
                        navController.navigate(R.id.navigation_req_inscription, null, navOptions);
                    }
                    break;
                case 2:
                    if (currentLocation != destination) {
                        navController.navigate(R.id.navigation_req_bridges, null, navOptions);
                    }
                    break;
                case 3:
                    if (currentLocation != destination) {
                        navController.navigate(R.id.navigation_cambio_veh, null, navOptions);
                    }
                    break;
                case 4:
                    if (currentLocation != destination) {
                        navController.navigate(R.id.navigation_req_balance_transfer, null, navOptions);
                    }
                    break;
                case 5:
                    if (currentLocation != destination) {
                        navController.navigate(R.id.navigation_req_unsubscribe, null, navOptions);
                    }
                    break;
            }


        }catch (Exception e) {
            Log.e(TAG, "Formalities_Requests: ", e);
        }
    }

    public static void nav_req(Integer destination) {
        try {
            navController.navigate(destination, null, navOptions);
        }catch (Exception e) {
            throw e;
        }
    }

    public static void Navigation_Requests(String destination) {
       try {
           if ("BackView".equals(destination)) {
               navController.popBackStack();
           }
       }catch (Exception e) {
           Log.e(TAG, "Navigation_Requests: ", e);
       }
    }

    public void downloadReporteDePuentesInfo() {
        new Thread(() -> {
            URL url = null;
            try {
                url = new URL("http://lineaexpressapp.desarrollosenlanube.net/bwt.json");

                URLConnection request = url.openConnection();
                request.connect();
                JsonObject completeJsonObject = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent())).getAsJsonObject();
                JsonArray portJsonArray = completeJsonObject.getAsJsonArray("port");

                sqlOnInit.cleanOnFire("ReporteDePuentes");

                System.out.println("Refrescando reporte de puentes");


                String PortName = "";
                String CrossingName = "";
                String GeneralDate = "";
                String PortStatus = "";
                String MaxPassengerLanes = "";
                String VehicleStandardUpdateTime = "";
                String VehicleStandardDelayMinutes = "";
                String VehicleStandardLanesOpen = "";
                String VehicleReadyUpdateTime = "";
                String VehicleReadyDelayMinutes = "";
                String VehicleReadyLanesOpen = "";
                String MaxPedestrianLanes = "";
                String PedestrianUpdateTime = "";
                String PedestrianDelayMinutes = "";
                String PedestrianLanesOpen = "";

                for (int i = 0; i < portJsonArray.size(); i++) {

                    JsonObject PassengerVehicleLanes = null;
                    JsonObject PassengerVehicleReady = null;
                    JsonObject PassengerVehicleStandard = null;

                    JsonObject jsonObject = portJsonArray.get(i).getAsJsonObject();

                    String PortNumber = jsonObject.get("port_number").getAsString();
                    PortName = jsonObject.get("port_name").getAsString();

                    try {
                        CrossingName = jsonObject.get("crossing_name").getAsString();
                    }catch (Exception e) {
                    }

                    GeneralDate = jsonObject.get("date").getAsString();

                    PortStatus = jsonObject.get("port_status").getAsString();


                    try {
                        PassengerVehicleLanes = jsonObject.get("passenger_vehicle_lanes").getAsJsonObject();
                    } catch (Exception e) {
                    }

                    try {
                        MaxPassengerLanes = PassengerVehicleLanes.get("maximum_lanes").getAsString();
                    }catch (Exception e) {
                        MaxPassengerLanes = "0";
                    }

                    try {
                        PassengerVehicleStandard = PassengerVehicleLanes.get("standard_lanes").getAsJsonObject();
                    }catch (Exception e) {
                    }

                    try {
                        VehicleStandardUpdateTime = PassengerVehicleStandard.get("update_time").getAsString();
                    }catch (Exception e) {
                        VehicleStandardUpdateTime = "Closed";
                    }

                    try {
                        VehicleStandardDelayMinutes = PassengerVehicleStandard.get("delay_minutes").getAsString();
                    }catch (Exception e) {
                        VehicleStandardDelayMinutes = "Closed";
                    }

                    try {
                        VehicleStandardLanesOpen = PassengerVehicleStandard.get("lanes_open").getAsString();
                    }catch (Exception e) {
                        VehicleStandardLanesOpen = "0";
                    }

                    try {
                        PassengerVehicleReady = PassengerVehicleLanes.get("ready_lanes").getAsJsonObject();
                    }catch (Exception e) {

                    }

                    try {
                        VehicleReadyUpdateTime = PassengerVehicleReady.get("update_time").getAsString();
                    }catch (Exception e) {
                        VehicleReadyUpdateTime = "Closed";

                    }

                    try {
                        VehicleReadyDelayMinutes = PassengerVehicleReady.get("delay_minutes").getAsString();
                    }catch (Exception e) {
                        VehicleReadyDelayMinutes = "Closed";
                    }

                    try {
                        VehicleReadyLanesOpen = PassengerVehicleReady.get("lanes_open").getAsString();
                    }catch (Exception e) {
                        VehicleReadyLanesOpen = "0";
                    }

                    UpdateData.InsertReportePuentes(PortNumber, PortName, CrossingName, GeneralDate, PortStatus, MaxPassengerLanes, VehicleStandardUpdateTime, VehicleStandardDelayMinutes, VehicleStandardLanesOpen, VehicleReadyUpdateTime, VehicleReadyDelayMinutes, VehicleReadyLanesOpen, MaxPedestrianLanes, PedestrianUpdateTime, PedestrianDelayMinutes, PedestrianLanesOpen);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nav_req(R.id.navigation_home);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        System.out.println("La aplicación ha sido resumida");
        checkInternet CheckInt = new checkInternet();
        Boolean checkInternetAccess = CheckInt.waitForInternetAvailability(this);
        if (checkInternetAccess) {
            sqlOnInit.cleanNotes();
            sqlOnInit.setCardIDIfNotExists();
            sqlOnInit.InsertTramiteFixOnInit();
            sqlOnInit.InsertTramitesOnInit();
            sqlOnInit.InsertCitasOnInit();
            sqlOnInit.NoteHelperOnInit();
            sqlOnInit.SubServiceHelperOnInit();
            sqlOnInit.UserSetIdOnInit();
            sqlOnInit.CarSelecterSetIdOnInit();
            startDownload();
        }else {
            Toast.makeText(this, "No hay acceso a internet", Toast.LENGTH_SHORT).show();
            nav_req(R.id.navigation_home);
        }
    }

    public void startDownload() {
        System.out.println("Aplicación resumida, estamos recargando todo again");
        updateData DownloadData = new updateData(MainActivity.this); //Open local db connection
        new Thread(() -> {
            //Downloading last 10 notes
            String jsonURL = "https://lineaexpress.desarrollosenlanube.net/wp-json/wp/v2/posts?per_page=10&categories=18&_embed";
            URL url;
            try {
                url = new URL(jsonURL);
                URLConnection request = url.openConnection();
                request.connect();
                JsonArray jsonArray = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent())).getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject(); //Converting Json Array to JsonObjects
                    JsonObject jsonArrays = jsonObject.get("_embedded").getAsJsonObject(); //Converting "_embedded" section to objects
                    JsonArray wpFeatureMediaArray = jsonArrays.getAsJsonArray("wp:featuredmedia"); // Converting "wp:featuredmedia" to Objects
                    JsonObject wpFMA_Object = wpFeatureMediaArray.get(0).getAsJsonObject(); // Initializing wpFeatureMediaArray
                    String wpFMA_Object_Link = Optional.ofNullable(wpFMA_Object.get("source_url"))
                            .map(JsonElement::getAsString)
                            .orElse("");

                    JsonObject titleArrays = jsonObject.get("title").getAsJsonObject(); //Converting "title" section to objects
                    String tA_String = titleArrays.get("rendered").getAsString(); // getting title
                    String jOId = jsonObject.get("id").getAsString(); // getting id
                    String jOLink = jsonObject.get("link").getAsString(); // getting Link to article
                    JsonObject bodyArray = jsonObject.get("content").getAsJsonObject(); //Converting "Body" section to objects
                    String bA_string = bodyArray.get("rendered").getAsString(); // getting Body

                    DownloadData.updateCarousel(jOId, tA_String, bA_string, wpFMA_Object_Link);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Downloading "Servicios"
            String Servicios = "https://lineaexpress.desarrollosenlanube.net/wp-json/wp/v2/posts?categories=15&_embed";
            URL urlServicios;
            try {
                urlServicios = new URL(Servicios);
                URLConnection requestServicios = urlServicios.openConnection();
                requestServicios.connect();
                JsonArray jsonArray_Servicios = JsonParser.parseReader(new InputStreamReader((InputStream) requestServicios.getContent())).getAsJsonArray();
                for (int i = 0; i < jsonArray_Servicios.size(); i++) {
                    JsonObject jsonObject_Servicios = jsonArray_Servicios.get(i).getAsJsonObject(); //Converting Json Array to JsonObjects
                    String json_Servicios_ID = jsonObject_Servicios.get("id").getAsString(); // getting id
                    String json_Servicios_Status = jsonObject_Servicios.get("status").getAsString(); // getting status
                    //Getting title
                    JsonObject titleContent = jsonObject_Servicios.get("title").getAsJsonObject(); //Converting Json Array to JsonObjects
                    String titleArrayTitle = titleContent.get("rendered").getAsString(); // getting title
                    //Getting ImageLink
                    JsonObject _EmbeddedA = jsonObject_Servicios.get("_embedded").getAsJsonObject(); //Converting _embedded to jsonObjects
                    JsonArray wp_featuredMedia = _EmbeddedA.get("wp:featuredmedia").getAsJsonArray(); //Converting wp:featuredMedia to jsonArray
                    JsonObject wp_featuredMedia_toObject = wp_featuredMedia.get(0).getAsJsonObject(); // Converting wp:featuredMedia to object
                    JsonObject media_details = wp_featuredMedia_toObject.get("media_details").getAsJsonObject(); //Getting Media_Details
                    JsonObject Sizes = media_details.get("sizes").getAsJsonObject(); //Getting Sizes
                    JsonObject fullImage = Sizes.get("full").getAsJsonObject(); //Getting FullImage
                    String fullImageLink = fullImage.get("source_url").getAsString(); //Getting FullImageLink as String
                    //Getting Body
                    JsonObject BodyContent = jsonObject_Servicios.get("content").getAsJsonObject(); //Getting Body Content
                    String BodyText = BodyContent.get("rendered").getAsString(); // getting BodyText

                    DownloadData.InsertServicios(json_Servicios_ID, json_Servicios_Status, titleArrayTitle, fullImageLink, BodyText);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Downloading "Servicios"
            String Lineamientos = "https://lineaexpress.desarrollosenlanube.net/wp-json/wp/v2/pages/1305?_embed";
            URL urlLineamientos;
            try {
                urlLineamientos = new URL(Lineamientos);
                URLConnection requestLineamientos = urlLineamientos.openConnection();
                requestLineamientos.connect();
                JsonObject jsonArray_Lineamientos = JsonParser.parseReader(new InputStreamReader((InputStream) requestLineamientos.getContent())).getAsJsonObject();
                //Getting title
                JsonObject titleContent = jsonArray_Lineamientos.get("title").getAsJsonObject(); //Converting Json Array to JsonObjects
                String titleArrayTitle = titleContent.get("rendered").getAsString(); // getting title
                //Getting Body
                JsonObject BodyContent = jsonArray_Lineamientos.get("content").getAsJsonObject(); //Getting Body Content
                String BodyText = BodyContent.get("rendered").getAsString(); // getting BodyText
                DownloadData.sqlLineamientos(titleArrayTitle, BodyText, "set");
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Downloading "WhoWeAre"
            String QuienesSomos = "https://lineaexpress.desarrollosenlanube.net/wp-json/wp/v2/pages/647?_embed";
            URL urlQuienesSomos;
            try {
                urlQuienesSomos = new URL(QuienesSomos);
                URLConnection requestQuienesSomos = urlQuienesSomos.openConnection();
                requestQuienesSomos.connect();
                JsonObject jsonArray_QuienesSomos = JsonParser.parseReader(new InputStreamReader((InputStream) requestQuienesSomos.getContent())).getAsJsonObject();

                JsonObject titleContent_QuienesSomos = jsonArray_QuienesSomos.get("title").getAsJsonObject(); //Converting Json Array to JsonObjects
                JsonObject BodyContent_QuienesSomos = jsonArray_QuienesSomos.get("content").getAsJsonObject(); //Getting Body Content

                String titleArrayTitle_QuienesSomos = titleContent_QuienesSomos.get("rendered").getAsString(); // getting title
                String BodyText_QuienesSomos = BodyContent_QuienesSomos.get("rendered").getAsString(); // getting BodyText

                DownloadData.InsertQuienesSomos(titleArrayTitle_QuienesSomos, BodyText_QuienesSomos);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Downloading "Terminos y condiciones"
            String TerminosYCondiciones = "https://lineaexpress.desarrollosenlanube.net/wp-json/wp/v2/pages/1309?_embed";
            URL urlTerminosYCondiciones;
            try {
                urlTerminosYCondiciones = new URL(TerminosYCondiciones);
                URLConnection requestTerminosYCondiciones = urlTerminosYCondiciones.openConnection();
                requestTerminosYCondiciones.connect();
                JsonObject jsonTerminosYCondiciones = JsonParser.parseReader(new InputStreamReader((InputStream) requestTerminosYCondiciones.getContent())).getAsJsonObject();

                JsonObject titleContent_TerminosYCondiciones = jsonTerminosYCondiciones.get("title").getAsJsonObject(); //Converting Json Array to JsonObjects
                JsonObject BodyContent_TerminosYCondiciones = jsonTerminosYCondiciones.get("content").getAsJsonObject(); //Getting Body Content

                String titleArrayTitle_TerminosYCondiciones = titleContent_TerminosYCondiciones.get("rendered").getAsString(); // getting title
                String BodyText_TerminosYCondiciones = BodyContent_TerminosYCondiciones.get("rendered").getAsString(); // getting BodyText

                DownloadData.InsertTermsAndCond(titleArrayTitle_TerminosYCondiciones, BodyText_TerminosYCondiciones);
            } catch (IOException e) {
                e.printStackTrace();
            }



            //Downloading Objetivo
            String Objetivo = "https://lineaexpress.desarrollosenlanube.net/wp-json/wp/v2/pages/662?_embed";
            URL url_Objetivo;
            try {
                url_Objetivo = new URL(Objetivo);
                URLConnection requestObjetivo = url_Objetivo.openConnection();
                requestObjetivo.connect();
                JsonObject jsonArray_Objetivo = JsonParser.parseReader(new InputStreamReader((InputStream) requestObjetivo.getContent())).getAsJsonObject();

                JsonObject titleContent_Objetivo = jsonArray_Objetivo.get("title").getAsJsonObject(); //Converting Json Array to JsonObjects
                JsonObject BodyContent_titleContent_Objetivo = jsonArray_Objetivo.get("content").getAsJsonObject(); //Getting Body Content

                String titleArrayContent_Objetivo = titleContent_Objetivo.get("rendered").getAsString(); // getting title
                String BodyText_Content_Objetivo = BodyContent_titleContent_Objetivo.get("rendered").getAsString(); // getting BodyText

                DownloadData.InsertObjetivo(titleArrayContent_Objetivo, BodyText_Content_Objetivo);

            } catch (IOException e) {
                e.printStackTrace();
            }

            //Downloading Mision
            String Mision = "https://lineaexpress.desarrollosenlanube.net/wp-json/wp/v2/pages/724?_embed";
            URL url_Mision;
            try {
                url_Mision = new URL(Mision);
                URLConnection requestMision = url_Mision.openConnection();
                requestMision.connect();
                JsonObject jsonArray_Mision = JsonParser.parseReader(new InputStreamReader((InputStream) requestMision.getContent())).getAsJsonObject();

                JsonObject titleContent_Mision = jsonArray_Mision.get("title").getAsJsonObject(); //Converting Json Array to JsonObjects
                JsonObject BodyContent_titleContent_Mision= jsonArray_Mision.get("content").getAsJsonObject(); //Getting Body Content
                String titleArrayContent_Mision = titleContent_Mision.get("rendered").getAsString(); // getting title
                String BodyText_Content_Mision = BodyContent_titleContent_Mision.get("rendered").getAsString(); // getting BodyText

                DownloadData.InsertMision(titleArrayContent_Mision, BodyText_Content_Mision);

            } catch (IOException e) {
                e.printStackTrace();
            }

            //Downloading Vision
            String Vision = "https://lineaexpress.desarrollosenlanube.net/wp-json/wp/v2/pages/730?_embed";
            URL url_Vision;
            try {
                url_Vision = new URL(Vision);
                URLConnection requestVision = url_Vision.openConnection();
                requestVision.connect();
                JsonObject jsonArray_Vision = JsonParser.parseReader(new InputStreamReader((InputStream) requestVision.getContent())).getAsJsonObject();

                JsonObject titleContent_Vision = jsonArray_Vision.get("title").getAsJsonObject(); //Converting Json Array to JsonObjects
                JsonObject BodyContent_titleContent_Vision= jsonArray_Vision.get("content").getAsJsonObject(); //Getting Body Content
                String titleArrayContent_Vision = titleContent_Vision.get("rendered").getAsString(); // getting title
                String BodyText_Content_Vision = BodyContent_titleContent_Vision.get("rendered").getAsString(); // getting BodyText

                DownloadData.InsertVision(titleArrayContent_Vision, BodyText_Content_Vision);

            } catch (IOException e) {
                e.printStackTrace();
            }

            //Downloading "Privacy"
            String Privacy = "https://lineaexpress.desarrollosenlanube.net/wp-json/wp/v2/pages/3?_embed";
            URL urlPrivacy;
            try {
                urlPrivacy = new URL(Privacy);
                URLConnection requestPrivacy = urlPrivacy.openConnection();
                requestPrivacy.connect();
                JsonObject jsonArray_Privacy = JsonParser.parseReader(new InputStreamReader((InputStream) requestPrivacy.getContent())).getAsJsonObject();

                JsonObject titleContent_Privacy = jsonArray_Privacy.get("title").getAsJsonObject(); //Converting Json Array to JsonObjects
                JsonObject BodyContent_Privacy = jsonArray_Privacy.get("content").getAsJsonObject(); //Getting Body Content

                String titleArrayTitle_Privacy = titleContent_Privacy.get("rendered").getAsString(); // getting title
                String BodyText_Privacy = BodyContent_Privacy.get("rendered").getAsString(); // getting BodyText

                DownloadData.InsertPrivacy(titleArrayTitle_Privacy, BodyText_Privacy);

            } catch (IOException e) {
                e.printStackTrace();
            }

            //Downloading "CurrentRates"
            String CurrentRates = "https://lineaexpress.desarrollosenlanube.net/wp-json/wp/v2/pages/1119?_embed";
            URL urlCurrentRates;
            try {
                urlCurrentRates = new URL(CurrentRates);
                URLConnection requestCurrentRates = urlCurrentRates.openConnection();
                requestCurrentRates.connect();
                JsonObject jsonArray_CurrentRates = JsonParser.parseReader(new InputStreamReader((InputStream) requestCurrentRates.getContent())).getAsJsonObject();
                JsonObject titleContent_CurrentRates = jsonArray_CurrentRates.get("title").getAsJsonObject(); //Converting Json Array to JsonObjects
                JsonObject BodyContent_CurrentRates = jsonArray_CurrentRates.get("content").getAsJsonObject(); //Getting Body Content
                String titleArrayTitle_CurrentRates = titleContent_CurrentRates.get("rendered").getAsString(); // getting title
                String BodyText_CurrentRates = BodyContent_CurrentRates.get("rendered").getAsString(); // getting BodyText
                DownloadData.InsertCurrentRates(titleArrayTitle_CurrentRates, BodyText_CurrentRates);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}


