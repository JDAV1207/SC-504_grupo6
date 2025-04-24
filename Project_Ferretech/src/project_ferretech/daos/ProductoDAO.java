package project_ferretech.daos;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import java.awt.Component;
import javax.swing.JFrame;
import project_ferretech.ConexionOracle;

public class ProductoDAO {

    // Método para obtener todos los productos
    public static DefaultTableModel obtenerProductos() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Descripción");
        modelo.addColumn("Precio");
        modelo.addColumn("Stock");
        modelo.addColumn("ID Categoría");

        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("Error: No se pudo establecer conexión con la base de datos.");
                return modelo;
            }

            try (CallableStatement stmt = con.prepareCall("{CALL LISTAR_PRODUCTOS(?)}")) {
                stmt.registerOutParameter(1, OracleTypes.CURSOR);
                stmt.execute();

                try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                    while (rs.next()) {
                        modelo.addRow(new Object[]{
                            rs.getInt("ID_PRODUCTO"),
                            rs.getString("NOMBRE"),
                            rs.getString("DESCRIPCION"),
                            rs.getDouble("PRECIO"),
                            rs.getInt("STOCK"),
                            rs.getInt("ID_CATEGORIA")
                        });
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
        return modelo;
    }

    private static int obtenerNuevoID() {
        int nuevoID = 1;
        try (Connection con = ConexionOracle.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("SELECT NVL(MAX(ID_PRODUCTO), 0) + 1 FROM PRODUCTOS")) {
            if (rs.next()) {
                nuevoID = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nuevoID;
    }

    //INSERTAR
    public static void crearProducto(JFrame parentFrame, JTable tablaProducto) {
        String nombre = JOptionPane.showInputDialog("Ingrese el nombre del producto:");
        String descripcion = JOptionPane.showInputDialog("Ingrese descripción del producto:");
        String precioStr = JOptionPane.showInputDialog("Ingrese el precio:");
        String stockStr = JOptionPane.showInputDialog("Ingrese la cantidad en stock:");
        String idCategoriaStr = JOptionPane.showInputDialog("Ingrese el ID de la categoría:");

        if (nombre != null && !nombre.trim().isEmpty()
                && descripcion != null && !descripcion.trim().isEmpty()
                && precioStr != null && !precioStr.trim().isEmpty()
                && stockStr != null && !stockStr.trim().isEmpty()
                && idCategoriaStr != null && !idCategoriaStr.trim().isEmpty()) {

            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL Pk_Procedimiento_Producto.INSERTAR_PRODUCTO(?, ?, ?, ?, ?, ?)}")) {

                stmt.setInt(1, obtenerNuevoID());
                stmt.setString(2, nombre);
                stmt.setString(3, descripcion);
                stmt.setDouble(4, Double.parseDouble(precioStr));
                stmt.setInt(5, Integer.parseInt(stockStr));
                stmt.setInt(6, Integer.parseInt(idCategoriaStr));
                stmt.execute();

                JOptionPane.showMessageDialog(parentFrame, "Producto agregado correctamente.");
                tablaProducto.setModel(obtenerProductos());

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error al agregar el producto.");
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Por favor, complete todos los espacios.");
        }
    }

    //EDITAR
    public static void editarProducto(JFrame parentFrame, JTable tablaProducto) {
        int fila = tablaProducto.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Seleccione un producto.");
            return;
        }

        int id = (int) tablaProducto.getValueAt(fila, 0);
        String nuevoNombre = JOptionPane.showInputDialog("Nuevo nombre:", tablaProducto.getValueAt(fila, 1));
        String nuevaDescripcion = JOptionPane.showInputDialog("Nueva descripción:", tablaProducto.getValueAt(fila, 2));
        String nuevoPrecioStr = JOptionPane.showInputDialog("Nuevo precio:", tablaProducto.getValueAt(fila, 3));
        String nuevoStockStr = JOptionPane.showInputDialog("Nuevo stock:", tablaProducto.getValueAt(fila, 4));
        String nuevaCategoriaStr = JOptionPane.showInputDialog("Nuevo ID de categoría:", tablaProducto.getValueAt(fila, 5));

        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()
                && nuevaDescripcion != null && !nuevaDescripcion.trim().isEmpty()
                && nuevoPrecioStr != null && !nuevoPrecioStr.trim().isEmpty()
                && nuevoStockStr != null && !nuevoStockStr.trim().isEmpty()
                && nuevaCategoriaStr != null && !nuevaCategoriaStr.trim().isEmpty()) {

            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL Pk_Procedimiento_Producto.ACTUALIZAR_PRODUCTO(?, ?, ?, ?, ?, ?)}")) {

                stmt.setInt(1, id);
                stmt.setString(2, nuevoNombre);
                stmt.setString(3, nuevaDescripcion);
                stmt.setDouble(4, Double.parseDouble(nuevoPrecioStr));
                stmt.setInt(5, Integer.parseInt(nuevoStockStr));
                stmt.setInt(6, Integer.parseInt(nuevaCategoriaStr));
                stmt.execute();

                JOptionPane.showMessageDialog(parentFrame, "Producto actualizado.");
                tablaProducto.setModel(obtenerProductos()); // Actualizar tabla

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error al actualizar el producto.");
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Por favor, complete todos los campos.");
        }
    }

    //ELIMINAR
    public static void eliminarProducto(JFrame parentFrame, JTable tablaProducto) {
        int fila = tablaProducto.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Seleccione un producto.");
            return;
        }

        int id = (int) tablaProducto.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(parentFrame, "¿Seguro que desea eliminar este producto?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL Pk_Procedimiento_Producto.ELIMINAR_PRODUCTO(?)}")) {

                stmt.setInt(1, id);
                stmt.execute();

                JOptionPane.showMessageDialog(parentFrame, "Producto eliminado correctamente.");
                tablaProducto.setModel(obtenerProductos()); // Actualizar tabla

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error al eliminar el producto.");
            }
        }
    }
}
