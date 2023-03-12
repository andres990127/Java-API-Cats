import com.google.gson.Gson;
import okhttp3.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GatosService {
    public static void verGatos() throws IOException {
        // Se traen los datos de la API
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://api.thecatapi.com/v1/images/search").get().build();
        Response response = client.newCall(request).execute();

        // Se obtiene la respuesta en un array de Jsons
        String jsonData = response.body().string();

        // Se corta el "[" y el "]" de la respuesta para solo quedar con el Json
        jsonData = jsonData.substring(1, jsonData.length());
        jsonData = jsonData.substring(0, jsonData.length()-1);

        // Transformamos un Json a un objeto
        Gson gson = new Gson();
        Gatos gato = gson.fromJson(jsonData,Gatos.class);

        // Se redimensiona la imagen en caso de necesitarlo
        Image image = null;
        try {
            URL url = new URL(gato.getUrl());
            HttpURLConnection httpcon = (HttpURLConnection)url.openConnection();
            httpcon.addRequestProperty("User-Agent", "");
            BufferedImage bufferedImage = ImageIO.read(httpcon.getInputStream());

            ImageIcon fondoGato = new ImageIcon(bufferedImage);
            if(fondoGato.getIconWidth() > 800){
                // Se redimensiona la imagen
                Image fondo = fondoGato.getImage();
                Image modificada = fondo.getScaledInstance(600, 600, Image.SCALE_SMOOTH);
                fondoGato = new ImageIcon(modificada);
            }

            String menu = "Opciones: \n"
                    + " 1. Ver otra imagen\n"
                    + " 2. Favorito\n"
                    + " 3. Volver\n";
            String[] botones = { "Ver otra imagen", "Favorito", "Volver"};
            String id_gato = gato.getId();
            String opcion = (String) JOptionPane.showInputDialog(null, menu, id_gato, JOptionPane.INFORMATION_MESSAGE, fondoGato, botones, botones[0]);

            int seleccion = -1;
            //validamos que opcion selecciona el usuario
            for(int i=0;i<botones.length;i++){
                if(opcion.equals(botones[i])){
                    seleccion = i;
                }
            }

            switch (seleccion){
                case 0:
                    verGatos();
                    break;
                case 1:
                    favoritoGato(gato);
                    break;
                default:
                    break;
            }

        }catch (IOException e){
            System.out.println(e);
        }
    }

    public static void favoritoGato(Gatos gato) {
        try{
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\n\t\"image_id\":\""+gato.getId()+"\"\n}");
            Request request = new Request.Builder()
                    .url("https://api.thecatapi.com/v1/favourites")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-api-key", gato.getApiKey())
                    .build();
            Response response = client.newCall(request).execute();

        }catch(IOException e){
            System.out.println(e);
        }

    }

    public static void verFavorito(String apikey) throws IOException{

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.thecatapi.com/v1/favourites")
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("x-api-key", apikey)
                .build();

        Response response = client.newCall(request).execute();

        // guardamos el string con la respuesta
        String elJson = response.body().string();

        //creamos el objeto gson
        Gson gson = new Gson();

        GatosFav[] gatosArray = gson.fromJson(elJson,GatosFav[].class);

        if(gatosArray.length > 0){
            int min = 1;
            int max  = gatosArray.length;
            int aleatorio = (int) (Math.random() * ((max-min)+1)) + min;
            int indice = aleatorio-1;

            GatosFav gatofav = gatosArray[indice];

            //redimensionar en caso de necesitar
            Image image = null;
            try{
                URL url = new URL(gatofav.image.getUrl());
                image = ImageIO.read(url);

                ImageIcon fondoGato = new ImageIcon(image);

                if(fondoGato.getIconWidth() > 800){
                    //redimensionamos
                    Image fondo = fondoGato.getImage();
                    Image modificada = fondo.getScaledInstance(800, 600, java.awt.Image.SCALE_SMOOTH);
                    fondoGato = new ImageIcon(modificada);
                }

                String menu = "Opciones: \n"
                        + " 1. ver otra imagen \n"
                        + " 2. Eliminar Favorito \n"
                        + " 3. Volver \n";

                String[] botones = { "ver otra imagen", "eliminar favorito", "volver" };
                String id_gato = gatofav.getId();
                String opcion = (String) JOptionPane.showInputDialog(null,menu,id_gato, JOptionPane.INFORMATION_MESSAGE, fondoGato, botones,botones[0]);

                int seleccion = -1;
                //validamos que opcion selecciona el usuario
                for(int i=0;i<botones.length;i++){
                    if(opcion.equals(botones[i])){
                        seleccion = i;
                    }
                }

                switch (seleccion){
                    case 0:
                        verFavorito(apikey);
                        break;
                    case 1:
                        borrarFavorito(gatofav);
                        break;
                    default:
                        break;
                }

            }catch(IOException e){
                System.out.println(e);
            }
        }
    }

    public static void borrarFavorito(GatosFav gatofav){
        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://api.thecatapi.com/v1/favourites/"+gatofav.getId()+"")
                    .delete(null)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-api-key", gatofav.getApikey())
                    .build();

            Response response = client.newCall(request).execute();
        }catch(IOException e){
            System.out.println(e);
        }

    }

}
