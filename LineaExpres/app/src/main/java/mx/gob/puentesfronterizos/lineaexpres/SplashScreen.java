package mx.gob.puentesfronterizos.lineaexpres;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import mx.gob.puentesfronterizos.lineaexpres.localDB.SQLOnInit;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

public class SplashScreen extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

       SQLOnInit sqlOnInit = new SQLOnInit(SplashScreen.this); //Open SQLOnInit Connection
       checkInternet CheckInt = new checkInternet();
       Boolean checkInternetAccess = CheckInt.waitForInternetAvailability(this);
       if (checkInternetAccess) {
           sqlOnInit.cleanOnInit();
           sqlOnInit.setCardIDIfNotExists();
           sqlOnInit.InsertTramiteFixOnInit();
           sqlOnInit.InsertTramitesOnInit();
           sqlOnInit.InsertCitasOnInit();
           sqlOnInit.NoteHelperOnInit();
           sqlOnInit.SubServiceHelperOnInit();
           sqlOnInit.UserSetIdOnInit();
           sqlOnInit.CarSelecterSetIdOnInit();
           sqlOnInit.cleanOnFire("ReporteDePuentes");
           startDownload();
       }else {
           runOnUiThread(() -> {
               Toast.makeText(this, "No hay acceso a internet.", Toast.LENGTH_SHORT).show();
               new MainActivity();
               Intent MainActivity = new Intent(SplashScreen.this, MainActivity.class);
               SplashScreen.this.startActivity(MainActivity);
           });
       }


       //shortcut();

    }

    public void shortcut() {
        new MainActivity();
        Intent MainActivity = new Intent(SplashScreen.this, MainActivity.class);
        SplashScreen.this.startActivity(MainActivity);
    }

    public void startDownload() {
        updateData DownloadData = new updateData(SplashScreen.this); //Open local db connection
        new Thread(() -> {
            //Descargando las notas
            String jsonURL = "https://noticias.fpfch.gob.mx/wp-json/wp/v2/posts?per_page=10&categories=18&_embed";
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
                // Aquí puedes mostrar un mensaje de error al usuario, por ejemplo:
                // Toast.makeText(context, "Error al cargar los datos. Inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }

            // Downloading "Servicios"
            String Servicios = "https://noticias.fpfch.gob.mx/wp-json/wp/v2/posts?categories=15&_embed";
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
            String Lineamientos = "https://noticias.fpfch.gob.mx/wp-json/wp/v2/pages/1305?_embed";
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
            //posible error
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
            String TerminosYCondiciones = "https://noticias.fpfch.gob.mx/wp-json/wp/v2/pages/1309?_embed";
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
            String Objetivo = "https://noticias.fpfch.gob.mx/wp-json/wp/v2/pages/662?_embed";
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
            String Mision = "https://noticias.fpfch.gob.mx/wp-json/wp/v2/pages/724?_embed";
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
            String Vision = "https://noticias.fpfch.gob.mx/wp-json/wp/v2/pages/730?_embed";
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
            String Privacy = "https://noticias.fpfch.gob.mx/wp-json/wp/v2/pages/3?_embed";
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
            String CurrentRates = "https://noticias.fpfch.gob.mx/wp-json/wp/v2/pages/1119?_embed";
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
            runOnUiThread(() -> {
                new MainActivity();
                Intent MainActivity = new Intent(SplashScreen.this, MainActivity.class);
                SplashScreen.this.startActivity(MainActivity);
            });
        }).start();
    }
}
