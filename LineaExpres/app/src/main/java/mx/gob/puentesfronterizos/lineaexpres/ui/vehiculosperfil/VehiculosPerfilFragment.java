package mx.gob.puentesfronterizos.lineaexpres.ui.vehiculosperfil;


import static mx.gob.puentesfronterizos.lineaexpres.ui.login.LoginFragment.convertStreamToString;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentVehiculosPerfilBinding;
import mx.gob.puentesfronterizos.lineaexpres.databinding.RecargaVehiculosPlantillaBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;


public class VehiculosPerfilFragment extends Fragment {

    ConstraintLayout MainBorraTagLayout;

    ScrollView scrollView;
    LinearLayout linearLayout;
    ArrayList<String> vehiculos;
    updateData openDb;

    ArrayList<String> userData;
    String User;
    String Token;
    UserLog userLog;


    LayoutInflater loaderInflater;
    View popupView;
    View view;
    int width;
    int height;
    PopupWindow popupWindow;
    int hasLineaVeh = 0;

    private FragmentVehiculosPerfilBinding binding;
    private String TAG;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        VehiculosPerfilViewModel callUsFragment =
                new ViewModelProvider(this).get(VehiculosPerfilViewModel.class);

        binding = FragmentVehiculosPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        openDb = new updateData(requireActivity()); //Open local db connection
        userLog = new UserLog(requireActivity()); //Open local db connection
        userData = userLog.GetUserData();
        User = userData.get(0);
        Token = userData.get(1);




        scrollView = binding.ScrollContainer;
        linearLayout = binding.LinearLayoutContainer;

        if (Token == null || Token.isEmpty()) {
            MainActivity.nav_req(R.id.navigation_login);
            root = null;
        }else {
            loaderInflater = (LayoutInflater) requireActivity().getSystemService(requireActivity().LAYOUT_INFLATER_SERVICE);
            popupView = loaderInflater.inflate(R.layout.loader, null);
            view = binding.LinearLayoutContainer.getRootView();
            width = LinearLayout.LayoutParams.MATCH_PARENT;
            height = LinearLayout.LayoutParams.MATCH_PARENT;
            popupWindow = new PopupWindow(popupView, width, height, false);
            popupWindow.setElevation(20);
            ImageView LoaderGif = popupView.findViewById(R.id.loader_gif);
            Glide.with(requireContext())
                    .load(R.drawable.linea_expres_loader)
                    .into(LoaderGif);

            popupWindow.showAtLocation(popupView, Gravity.TOP|Gravity.END, 0, 0);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        openDb.cleanVehiculos();

                        InputStream inputStream;
                        String accountActivation_url = "https://apis.fpfch.gob.mx/api/v1/vehicles";

                        URL url = new URL(accountActivation_url);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                        conn.setRequestProperty("Content-Type", "text");
                        conn.setRequestProperty("Accept","application/json");
                        conn.setRequestProperty("Authorization", "Bearer " + Token);
                        conn.setRequestMethod("GET");

                        int Status = conn.getResponseCode();
                        Log.i(TAG, "httpPostRequest: status = " + conn.getResponseCode());
                        Log.i(TAG, "httpPostRequest: msg = " + conn.getResponseMessage());

                        if (Status != 200) {
                            inputStream = new BufferedInputStream(conn.getErrorStream());
                        }else {
                            inputStream = new BufferedInputStream(conn.getInputStream());
                        }
                        String ResponseData = convertStreamToString(inputStream);
                        System.out.println("Este es el responseData de Register Fragment " + ResponseData);


                        JSONArray Result = new JSONArray(ResponseData);
                        System.out.println("Este es el Result de Register Fragment " + Result);

                        for (int i = 0; i < Result.length(); i++) {
                            JSONObject Tramites = (JSONObject) Result.get(i);

                            int tipoVeh = (int) Tramites.get("tipo");
                            String Marca = (String) Tramites.get("marca");
                            String Linea = (String) Tramites.get("linea");
                            String tag = (String) Tramites.get("tag");
                            String imgurl = (String) Tramites.get("imgurl");
                            String ctl_contract_type = Tramites.optString("ctl_contract_type", "undefined");
                            String clt_expiration_date = Tramites.optString("clt_expiration_date", "undefined");
                            String saldo = (String) Tramites.get("saldo");
                            String placa = (String) Tramites.get("placa");
                            String color = (String) Tramites.get("color");
                            String anio = (String) Tramites.get("modelo");
                            String ctl_stall_id = Tramites.optString("ctl_stall_id", "undefined");
                            String ctl_user_id = Tramites.optString("ctl_user_id", "undefined");
                            String ctl_id = Tramites.optString("ctl_id", "undefined");

                            //if (tipoVeh != 1) {
                            //    continue;
                            //}

                            openDb.insertVehicles(new String(String.valueOf(tipoVeh)), Marca, Linea, tag, imgurl, new String(String.valueOf(ctl_contract_type)), clt_expiration_date, saldo, placa, color, anio, ctl_stall_id, ctl_user_id, ctl_id);
                        }
                        conn.disconnect();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }

                    requireActivity().runOnUiThread(() -> {
                        vehiculos = openDb.getProfileVehicles();
                        if (vehiculos.size() != 0) {
                            for (int i = 0; i < vehiculos.size(); i++) {
                                View plantillaView = inflater.inflate(R.layout.recarga_vehiculos_plantilla, linearLayout, false);

                                TextView type = (TextView) plantillaView.findViewById(R.id.type);
                                ImageView vehImg = (ImageView) plantillaView.findViewById(R.id.vehicleImg);
                                TextView modeloText = (TextView) plantillaView.findViewById(R.id.modeloText);
                                TextView placasText = (TextView) plantillaView.findViewById(R.id.placasText);
                                TextView tagText = (TextView) plantillaView.findViewById(R.id.tagText);
                                TextView saldoText = (TextView) plantillaView.findViewById(R.id.saldoText);
                                Button recarga = (Button) plantillaView.findViewById(R.id.btnRecarga);
                                Button misCruces = (Button) plantillaView.findViewById(R.id.btnMisCruces);
                                Button misCruces2 = (Button) plantillaView.findViewById(R.id.btnMisCruces2);
                                Button CancelarBorrarTAG = (Button) plantillaView.findViewById(R.id.btnCancelarBorrarTAG);
                                Button AceptarCancelarTAG = (Button) plantillaView.findViewById(R.id.btnAceptarCancelarTAG);


                                String[] splitArray = vehiculos.get(i).split("∑");
                                String vehType = splitArray[0];
                                String Marca = splitArray[1];
                                String Linea = splitArray[2];
                                String Tag = splitArray[3];
                                String imgUrl = splitArray[4];
                                String ctl_contract_type = splitArray[5];
                                String clt_expiration_date = splitArray[6];
                                String Saldo = splitArray[7];
                                String Placa = splitArray[8];
                                String ctl_user_id = splitArray[12];
                                String ctl_id = splitArray[13];

                                //if (vehType.equals("1")) {
                                //    hasLineaVeh++;
                                //    System.out.println("Este tiene que decir hasLineaVeh: " + hasLineaVeh);
                                //}
                                if (Objects.equals(Saldo, "0.00")) {
                                    CompletableFuture<String> future = verifyTag(Tag);
                                    try {
                                        Saldo = future.get();
                                    } catch (InterruptedException | ExecutionException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                               /* misCruces2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Obtén el contexto de la actividad actual
                                        Context context = getContext();

                                        // Crea el AlertDialog.Builder
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                        // Establece el mensaje del diálogo
                                        builder.setMessage("Seguro que quieres elminar el tag: " + Tag);

                                        // Añade un botón "Aceptar" al diálogo
                                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Puedes realizar alguna acción si es necesario al hacer clic en "Aceptar"
                                            }
                                        });

                                        // Añade un botón "Cancelar" al diálogo
                                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Puedes realizar alguna acción si es necesario al hacer clic en "Cancelar"
                                                dialog.cancel(); // Esto cancela el diálogo
                                            }
                                        });

                                        // Muestra el diálogo
                                        builder.create().show();
                                    }
                                });*/
                                //Button misCruces2 = (Button) plantillaView.findViewById(R.id.btnMisCruces2);

                                //Borrar TAG
                               ConstraintLayout MainBorraTagLayout = (ConstraintLayout) plantillaView.findViewById(R.id.MainBorraTagLayout);


                                misCruces2.setOnClickListener(v -> {
                                    MainBorraTagLayout.setVisibility(View.VISIBLE);
                                });

                                CancelarBorrarTAG.setOnClickListener(v -> {
                                    MainBorraTagLayout.setVisibility(View.GONE);
                                });

                                AceptarCancelarTAG.setOnClickListener(v -> {
                                    postDeleteTag(Tag);
                                });
                                //Fin Borrar Tag
                                recarga.setTag(Placa);
                                if (ctl_id.equals("null")) {
                                    misCruces.setVisibility(View.GONE);
                                }

                                misCruces.setTag(Placa);
                                misCruces.setOnClickListener((View v) -> {
                                    openDb.updateCarSelected(misCruces.getTag().toString());
                                    MainActivity.nav_req(R.id.navigation_mis_cruces);
                                });

                                if (vehType.equals("0")) {
                                    type.setText("Telepeaje");
                                }
                                if (vehType.equals("1")) {
                                    type.setText("Línea Exprés");
                                    String fecha = clt_expiration_date;
                                    //String fecha = "2023-08-16";
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


                                    if (!clt_expiration_date.contains("undefined")) {
                                        if (ctl_contract_type.equals("V") || ctl_contract_type.equals("M")) {
                                            try {

                                                Date fechaEspecifica = dateFormat.parse(fecha);
                                                Date fechaActual = new Date();

                                                Calendar calendarEspecifica = Calendar.getInstance();
                                                calendarEspecifica.setTime(fechaEspecifica);

                                                Calendar calendarActual = Calendar.getInstance();
                                                calendarActual.setTime(fechaActual);

                                                long diferenciaMilisegundos = calendarEspecifica.getTimeInMillis() - calendarActual.getTimeInMillis();
                                                long daysRemaining = diferenciaMilisegundos / (24 * 60 * 60 * 1000);
                                                System.out.println("La diferencia en días es: " + daysRemaining);
                                                System.out.println("Vamos a ver los daysreamin: " + daysRemaining);
                                                if (daysRemaining >= 90) {
                                                    System.out.println("Este contrato se puede renovar en " + (daysRemaining - 90) + " días");
                                                    if (daysRemaining - 90 == 1) {
                                                        recarga.setText("Contrato renovable en " + (daysRemaining - 90) + " día");
                                                        recarga.setOnClickListener(null);
                                                    } else if (daysRemaining - 90 == 0) {
                                                        recarga.setText("Renovar");
                                                        recarga.setOnClickListener((View v) -> {
                                                            openDb.updateCarSelected(recarga.getTag().toString());
                                                            MainActivity.nav_req(R.id.navigation_recharge);
                                                        });
                                                    } else {
                                                        System.out.println("En días");
                                                        recarga.setText("Contrato renovable en " + (daysRemaining - 90) + " días");
                                                        recarga.setOnClickListener(null);
                                                    }
                                                } else {
                                                    System.out.println("Ya puede renovar " + (daysRemaining - 90));
                                                    recarga.setText("Renovar");
                                                    recarga.setOnClickListener((View v) -> {
                                                        openDb.updateCarSelected(recarga.getTag().toString());
                                                        MainActivity.nav_req(R.id.navigation_recharge);
                                                    });

                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }else {
                                            recarga.setOnClickListener((View v) -> {
                                                openDb.updateCarSelected(recarga.getTag().toString());
                                                MainActivity.nav_req(R.id.navigation_recharge);
                                            });
                                        }
                                    }else {
                                        recarga.setOnClickListener((View v) -> {
                                            openDb.updateCarSelected(recarga.getTag().toString());
                                            MainActivity.nav_req(R.id.navigation_recharge);
                                        });
                                    }



                                }
                                if (vehType.equals("2")) {
                                    type.setText("Acceso Digital Peatonal");
                                    Glide.with(requireContext())
                                            .load(R.drawable.adp)
                                            .into(vehImg);
                                    recarga.setOnClickListener((View v) -> {
                                        openDb.updateCarSelected(recarga.getTag().toString());
                                        MainActivity.nav_req(R.id.navigation_recharge);
                                    });
                                }else if (vehType.equals("0")){
                                    Glide.with(requireContext())
                                            .load(imgUrl)
                                            .into(vehImg);
                                    recarga.setOnClickListener((View v) -> {
                                        openDb.updateCarSelected(recarga.getTag().toString());
                                        MainActivity.nav_req(R.id.navigation_recharge);
                                    });
                                }
                                modeloText.setText(Marca + " " + Linea);
                                placasText.setText(Placa);
                                tagText.setText("Tag: " + Tag);
                                if (ctl_contract_type.equals("V")) {
                                    saldoText.setText("Contrato vence: " + clt_expiration_date);
                                }else {
                                    saldoText.setText("Saldo: $" + Saldo + " MXN");
                                }

                                // Agregar la vista al LinearLayout
                                linearLayout.addView(plantillaView);

                            }

                            //if (hasLineaVeh == 0) {
                            //    requireActivity().runOnUiThread(() -> {
                            //        Toast.makeText(requireContext(), "No tienes vehículos, agrega alguno", Toast.LENGTH_SHORT).show();
                            //        MainActivity.nav_req(R.id.navigation_profile);
                            //    });
                            //}
                        }else {
                            MainActivity.nav_req(R.id.navigation_profile);
                            Toast.makeText(requireContext(), "No tienes vehículos, agrega alguno" , Toast.LENGTH_SHORT).show();
                        }


                        popupWindow.dismiss();
                    });
                }
            });

            thread.start();


        }

        return root;
    }



    public CompletableFuture<String> verifyTag(String Tag) {
        CompletableFuture<String> future = new CompletableFuture<>();
        CompletableFuture.supplyAsync(() -> {
            try {
                InputStream inputStream;
                String accountActivation_url = "https://apis.fpfch.gob.mx/api/v1/tags/exists/" + Tag;

                URL url = new URL(accountActivation_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "text");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + Token);
                conn.setRequestMethod("GET");

                int Status = conn.getResponseCode();

                Log.i(TAG, "httpPostRequest: status = " + conn.getResponseCode());
                Log.i(TAG, "httpPostRequest: msg = " + conn.getResponseMessage());

                if (Status != 200) {
                    inputStream = new BufferedInputStream(conn.getErrorStream());
                } else {
                    inputStream = new BufferedInputStream(conn.getInputStream());
                }
                String ResponseData = convertStreamToString(inputStream);
                System.out.println("Este es el responseData de Register Fragment " + ResponseData);

                JSONObject Result = new JSONObject(ResponseData);
                conn.disconnect();

                String saldazo = "";
                JSONObject a = Result.getJSONObject("tp");

                try {
                    System.out.println("El saldo desde la api " + a.getString("saldoActual"));
                    saldazo = (a.getString("saldoActual"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


                return saldazo;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return "";
            }
        }).thenAccept(future::complete);
        return future;
    }

    /*public void postDeleteTag(String Tag){
        new Thread(() -> {
            try {
                InputStream inputStream;
                String accountActivation_url = "https://apis.fpfch.gob.mx/api/v1/tags/exists/" + Tag;
                URL url = new URL(accountActivation_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "text");
                conn.setRequestProperty("Accept","application/json");
                conn.setRequestProperty("Authorization", "Bearer " + Token);
                conn.setRequestMethod("DELETE");

                int Status = conn.getResponseCode();

                Log.i(TAG, "httpPostRequest: status = " + conn.getResponseCode());
                Log.i(TAG, "httpPostRequest: msg = " + conn.getResponseMessage());

                if (Status != 200) {
                    inputStream = new BufferedInputStream(conn.getErrorStream());
                }else {
                    inputStream = new BufferedInputStream(conn.getInputStream());
                }
                String ResponseData = convertStreamToString(inputStream);
                System.out.println("Este es el responseData de delete account " + ResponseData);

                JSONObject Result = new JSONObject(ResponseData);
                String message = (String) Result.get("message");

                if (message.contains("Usuario eliminado exitosamente.") || message.contains("La petición la realizó un usuario no válido.")) {
                    requireActivity().runOnUiThread(() -> {
                        MainActivity.nav_req(R.id.navigation_logout);
                    });
                }
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                });

                System.out.println(message);



                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }).start();
    }*/

//eliminar tag api
    public void postDeleteTag(String Tag) {

        new Thread(() -> {
            try {
                InputStream inputStream;
                String accountActivation_url = "https://apis.fpfch.gob.mx/api/v1/tags/exists/" + Tag;

                URL url = new URL(accountActivation_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "text");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + Token);
                conn.setRequestMethod("DELETE");

                int Status = conn.getResponseCode();

                Log.i(TAG, "httpPostRequest: status = " + conn.getResponseCode());
                Log.i(TAG, "httpPostRequest: msg = " + conn.getResponseMessage());

                if (Status != 200) {
                    inputStream = new BufferedInputStream(conn.getErrorStream());
                } else {
                    inputStream = new BufferedInputStream(conn.getInputStream());
                }
                String ResponseData = convertStreamToString(inputStream);
                System.out.println("Este es el responseData de Register Fragment " + ResponseData);

                JSONObject Result = new JSONObject(ResponseData);
                conn.disconnect();

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }




}