package project_ferretech;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import java.awt.Component;

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

    public static int obtenerNuevoID() {
        int nuevoID = -1;
        try (Connection con = ConexionOracle.getConnection(); PreparedStatement stmt = con.prepareStatement("SELECT MAX(ID_CATEGORIA) + 1 FROM CATEGORIA"); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                nuevoID = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nuevo ID de categoría: " + e.getMessage());
        }
        return nuevoID;
    }

//ELIMINAR CATEGORIA
    public static boolean eliminarCategoria(int id) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }

            try (CallableStatement stmt = con.prepareCall("{call ELIMINAR_CATEGORIA(?)}")) {
                stmt.setInt(1, id);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar categoría: " + e.getMessage());
            return false;
        }
    }

    public static void eliminarCategoriaDesdeTabla(JTable tablaCategorias, Component parentComponent) {
        int fila = tablaCategorias.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentComponent, "Seleccione una categoría.");
            return;
        }

        int id = (int) tablaCategorias.getValueAt(fila, 0);

        int confirm = JOptionPane.showConfirmDialog(
                parentComponent,
                "¿Está seguro que desea eliminar esta categoría?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean eliminada = eliminarCategoria(id);

            if (eliminada) {
                JOptionPane.showMessageDialog(parentComponent, "Categoría eliminada correctamente.");
                tablaCategorias.setModel(obtenerCategorias());
            } else {
                JOptionPane.showMessageDialog(parentComponent, "Error al eliminar la categoría.");
            }
        }
    }

//ACTUALIZAR CATEGORIA
    public static boolean actualizarCategoria(int id, String nuevoNombre) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }
            try (CallableStatement stmt = con.prepareCall("{call ACTUALIZAR_CATEGORIA(?)}")) {
                stmt.setInt(1, id);
                stmt.setString(2, nuevoNombre);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar categoría: " + e.getMessage());
            return false;
        }
    }

    public static void actualizarCategoriaDesdeTabla(JTable tablaCategorias, Component parentComponent) {
        int fila = tablaCategorias.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentComponent, "Seleccione una categoría.");
            return;
        }

        int id = (int) tablaCategorias.getValueAt(fila, 0);
        String nuevoNombre = JOptionPane.showInputDialog(parentComponent, "Nuevo nombre:", tablaCategorias.getValueAt(fila, 1));

        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            boolean actualizada = actualizarCategoria(id, nuevoNombre);

            if (actualizada) {
                JOptionPane.showMessageDialog(parentComponent, "Categoría actualizada correctamente.");
                tablaCategorias.setModel(obtenerCategorias());
            } else {
                JOptionPane.showMessageDialog(parentComponent, "Error al actualizar la categoría.");
            }
        } else {
            JOptionPane.showMessageDialog(parentComponent, "El nombre no puede estar vacío.");
        }
    }

    //Crear CATEGORIA
    public static boolean insertarCategoria(int id, String nombre) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }
            try (CallableStatement stmt = con.prepareCall("{call INSERTAR_CATEGORIA(?, ?)}")) {
                stmt.setInt(1, id);
                stmt.setString(2, nombre);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar la categoría: " + e.getMessage());
            return false;
        }
    }

    public static void crearCategoriaDesdeInput(JTable tablaCategorias, Component parentComponent) {
        String nombre = JOptionPane.showInputDialog(parentComponent, "Ingrese el nombre de la categoría:");

        if (nombre != null && !nombre.trim().isEmpty()) {
            int id = obtenerNuevoID();
            boolean creada = insertarCategoria(id, nombre);

            if (creada) {
                JOptionPane.showMessageDialog(parentComponent, "Categoría creada correctamente.");
                tablaCategorias.setModel(obtenerCategorias());
            } else {
                JOptionPane.showMessageDialog(parentComponent, "Error al crear la categoría.");
            }
        } else {
            JOptionPane.showMessageDialog(parentComponent, "El nombre no puede estar vacío.");
        }
    }

}
