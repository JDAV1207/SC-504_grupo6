package project_ferretech;

import java.sql.*;
import javax.swing.SwingUtilities;

public class Project_Ferretech {

     public static Connection connection = null;

    public static void main(String[] args) {
       // Asegurarse de que la interfaz gráfica se ejecute en el hilo de eventos de Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Crear y mostrar el JFrame principal
                    new Menu().setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
