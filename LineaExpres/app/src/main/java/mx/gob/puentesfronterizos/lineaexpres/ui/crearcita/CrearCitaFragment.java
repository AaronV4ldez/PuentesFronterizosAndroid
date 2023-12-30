package mx.gob.puentesfronterizos.lineaexpres.ui.crearcita;


import static mx.gob.puentesfronterizos.lineaexpres.ui.login.LoginFragment.convertStreamToString;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentCrearcitaBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;


public class CrearCitaFragment extends Fragment {

    //Database declaration
    UserLog userLog;
    updateData UpdateData;

    EditText DateChecker;
    Button checkDate;
    Button confirmDate;
    Spinner DataHour;

    String Token;

    LayoutInflater popupInflater;
    View popup_View;
    View popup_view;
    int popup_width;
    int popup_height;
    PopupWindow popup_Window;
    TextView popup_Head;
    TextView popup_Body;

    Handler handler;
    int counter;

    final String TAG = "Crear Cita";
    final Calendar myCalendar= Calendar.getInstance();
    private FragmentCrearcitaBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       CrearCitaViewModel FormalitiesViewModel = new ViewModelProvider(this).get(CrearCitaViewModel.class);
        binding = FragmentCrearcitaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        DateChecker = binding.DateChecker;
        checkDate = binding.checkDate;
        DataHour = binding.DataHour;
        confirmDate = binding.confirmDate;
        popupInflater = (LayoutInflater) requireActivity().getSystemService(requireActivity().LAYOUT_INFLATER_SERVICE);
        popup_View = popupInflater.inflate(R.layout.popup_top, null);
        popup_view = DateChecker.getRootView();
        popup_width = LinearLayout.LayoutParams.WRAP_CONTENT;
        popup_height = LinearLayout.LayoutParams.WRAP_CONTENT;
        popup_Window = new PopupWindow(popup_View, popup_width, popup_height, false);
        popup_Head = popup_View.findViewById(R.id.popupHead);
        popup_Body = popup_View.findViewById(R.id.popupBody);

        counter = 5000;
        handler = new Handler();
        userLog = new UserLog(requireContext()); //Open Users db
        UpdateData = new updateData(requireContext()); //Open Users db
        Token = userLog.GetUserData().get(1);

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };

        DateChecker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(requireActivity(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        checkDate.setOnClickListener(view -> {
            if (DateChecker.getText().toString().isEmpty()) {
                popup_Head.setText("Fecha");
                popup_Body.setText("Elija una fecha.");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }
            getDates(DateChecker.getText().toString());
        });

        confirmDate.setOnClickListener(view -> {
            if (DateChecker.getText().toString().isEmpty()) {
                popup_Head.setText("Fecha");
                popup_Body.setText("Elija una fecha.");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }

            ArrayList<String> ids = UpdateData.getCitaId();
            String id_proc = ids.get(0);
            String id_proc_type = ids.get(1);
            String Hora = DataHour.getSelectedItem().toString();
            String[] sp = Hora.split(" ");

            System.out.println(sp[1]);
            setDatee(id_proc, id_proc_type, DateChecker.getText().toString(), sp[1]);
        });

        return root;
    }
    private void updateLabel(){
        String myFormat="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        DateChecker.setText(dateFormat.format(myCalendar.getTime()));
    }

    public void getDates(String Date){
        new Thread(() -> {
            try {

                InputStream inputStream;
                String accountActivation_url = "https://apis.fpfch.gob.mx/api/v1/appointments/available";

                URL url = new URL(accountActivation_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setRequestProperty("Authorization", "Bearer " + Token);
                conn.setRequestMethod("POST");

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("date", Date);

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

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

                ArrayList<String> Horas = new ArrayList<>();
                for (int i = 0; i < Result.length(); i++) {
                    JSONObject DatesObj = (JSONObject) Result.get(i);
                    String HoraInicio = (String) DatesObj.get("inicio");
                    String HoraFinal = (String) DatesObj.get("fin");

                    String InnerHours = "Inicia " + HoraInicio + " Termina " + HoraFinal;
                    Horas.add(InnerHours);


                }
                requireActivity().runOnUiThread(() -> {
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1,Horas);
                    DataHour.setAdapter(arrayAdapter);
                });



                conn.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public void setDatee(String id_proc, String id_proc_type, String Date, String Hour){
        new Thread(() -> {
            try {

                InputStream inputStream;
                String accountActivation_url = "https://apis.fpfch.gob.mx/api/v1/appointments/create";

                URL url = new URL(accountActivation_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setRequestProperty("Authorization", "Bearer " + Token);
                conn.setRequestMethod("POST");

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("date", Date);
                jsonParam.put("time", Hour);
                jsonParam.put("id_proc", id_proc);
                jsonParam.put("id_proc_type", id_proc_type);

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

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

                if (Status == 400) {
                    JSONObject Result = new JSONObject(ResponseData);
                    requireActivity().runOnUiThread(() -> {
                        popup_Head.setText("Error");
                        try {
                            popup_Body.setText((CharSequence) Result.get("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                        handler.postDelayed(() -> popup_Window.dismiss(), counter);
                    });
                    return;
                }

                if (Status == 200) {
                    JSONObject Result = new JSONObject(ResponseData);
                    requireActivity().runOnUiThread(() -> {
                        popup_Head.setText("Exito");
                        try {
                            popup_Body.setText((CharSequence) Result.get("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                        handler.postDelayed(() -> popup_Window.dismiss(), counter);
                        MainActivity.nav_req(R.id.navigation_profile);
                    });
                    return;
                }







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