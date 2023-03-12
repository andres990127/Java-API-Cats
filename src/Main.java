/*
    Agregar la dependencia com.squareup.okhttp3:okhttp:4.10.0 --> Para hacer peticiones HTTP
    Agregar la dependencia com.google.code.gson:gson:2.10.1   --> Para convertir respuestas JSON en objetos Java
*/

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        int opcion_menu = -1;
        String[] botones = {" 1. Ver gatos", "2. Ver favoritos","3. Salir"};

        do{
            //menu principal
            String opcion = (String) JOptionPane.showInputDialog(null, "Gatitos java", "Menu principal", JOptionPane.INFORMATION_MESSAGE,
                    null, botones,botones[0]);

            //validamos que opcion selecciona el usuario
            for(int i=0;i<botones.length;i++){
                if(opcion.equals(botones[i])){
                    opcion_menu = i;
                }
            }

            switch(opcion_menu){
                case 0:
                    GatosService.verGatos();
                    break;
                case 1:
                    Gatos gato = new Gatos();
                    GatosService.verFavorito(gato.getApiKey());
                default:
                    break;
            }
        }while(opcion_menu != 1);

    }

}