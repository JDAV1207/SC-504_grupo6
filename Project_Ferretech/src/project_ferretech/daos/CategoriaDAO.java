package project_ferretech.daos;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import java.awt.Component;
import javax.swing.JFrame;
import project_ferretech.ConexionOracle;

public class CategoriaDAO {

    public static DefaultTableModel obtenerCategorias() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");

        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL LISTAR_CATEGORIAS(?)}")) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.execute();
            ResultSet rs = (ResultSet) stmt.getObject(1);

            while (rs.next()) {
                modelo.addRow(new Object[]{rs.getInt("ID_CATEGORIA"), rs.getString("NOMBRE_CATEGORIA")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modelo;
    }

    private static int obtenerNuevoID() {
        int nuevoID = 1;
        try (Connection con = ConexionOracle.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("SELECT NVL(MAX(ID_CATEGORIA), 0) + 1 FROM CATEGORIAS")) {
            if (rs.next()) {
                nuevoID = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nuevoID;
    }

//ELIMINAR CATEGORIA
    public static void eliminarCategoria(JFrame parentFrame, JTable tablaCategorias) {
        int fila = tablaCategorias.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Seleccione una categoría.");
            return;
        }

        int id = (int) tablaCategorias.getValueAt(fila, 0);

        int confirm = JOptionPane.showConfirmDialog(parentFrame, "¿Seguro que desea eliminar esta categoría?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL ELIMINAR_CATEGORIA(?)}")) {

                stmt.setInt(1, id);
                stmt.execute();

                JOptionPane.showMessageDialog(parentFrame, "Categoría eliminada.");
                tablaCategorias.setModel(obtenerCategorias());
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error al eliminar la categoría: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

//ACTUALIZAR CATEGORIA
    public static void editarCategoria(JFrame parentFrame, JTable tablaCategorias) {
        int fila = tablaCategorias.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Seleccione una categoría.");
            return;
        }

        int id = (int) tablaCategorias.getValueAt(fila, 0);
        String nuevoNombre = JOptionPane.showInputDialog("Nuevo nombre:", tablaCategorias.getValueAt(fila, 1));

        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL ACTUALIZAR_CATEGORIA(?,?)}")) {

                stmt.setInt(1, id);
                stmt.setString(2, nuevoNombre);
                stmt.execute();

                JOptionPane.showMessageDialog(parentFrame, "Categoría actualizada.");
                tablaCategorias.setModel(obtenerCategorias());
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error al actualizar la categoría: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

//CREAR CATEGORIA
    public static void crearCategoria(JFrame parentFrame, JTable tablaCategorias) {
        String nombre = JOptionPane.showInputDialog("Ingrese el nombre de la categoría:");

        if (nombre != null && !nombre.trim().isEmpty()) {
            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL INSERTAR_CATEGORIA(?,?)}")) {

                stmt.setInt(1, obtenerNuevoID()); // Asegúrate de que este método esté accesible aquí
                stmt.setString(2, nombre);
                stmt.execute();

                JOptionPane.showMessageDialog(parentFrame, "Categoría creada.");
                tablaCategorias.setModel(obtenerCategorias());
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error al crear la categoría: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
