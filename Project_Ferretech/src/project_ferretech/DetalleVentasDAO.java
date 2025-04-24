package project_ferretech;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;

public class DetalleVentasDAO {

    public static DefaultTableModel obtenerDetalleVentas() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID Detalle");
        modelo.addColumn("ID Venta");
        modelo.addColumn("ID Producto");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Subtotal");

        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL LISTAR_DETALLE_VENTA(?)}")) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                while (rs.next()) {
                    modelo.addRow(new Object[]{
                        rs.getInt("ID_DETALLE"),
                        rs.getInt("ID_VENTA"),
                        rs.getInt("ID_PRODUCTO"),
                        rs.getInt("CANTIDAD"),
                        rs.getDouble("SUBTOTAL")
                    });
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modelo;
    }

    //CREAR DETALLE vENTAS
    public static boolean insertarDetalleVenta(int idDetalle, int idVenta, int idProducto, int cantidad, double subtotal) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }
            try (CallableStatement stmt = con.prepareCall("{call INSERTAR_DETALLE_VENTA(?, ?, ?, ?, ?)}")) {
                stmt.setInt(1, idDetalle);
                stmt.setInt(2, idVenta);
                stmt.setInt(3, idProducto);
                stmt.setInt(4, cantidad);
                stmt.setDouble(5, subtotal);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar detalle de venta: " + e.getMessage());
            return false;
        }
    }

    //EDITAR DETALLE vETNAS
    public static boolean actualizarDetalleVenta(int idDetalle, int nuevaCantidad, double nuevoSubtotal) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }
            try (CallableStatement stmt = con.prepareCall("{call ACTUALIZAR_DETALLE_VENTA(?, ?, ?, ?, ?)}")) {
                stmt.setInt(1, idDetalle);
                stmt.setInt(2, nuevaCantidad);
                stmt.setDouble(3, nuevoSubtotal);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar detalle de venta: " + e.getMessage());
            return false;
        }
    }

    //ELIMINAR DET VENTAS
    public static boolean eliminarDetalleVenta(int idDetalle) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }

            try (CallableStatement stmt = con.prepareCall("{CALL ELIMINAR_DETALLE_VENTA(?)}")) {
                stmt.setInt(1, idDetalle);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar detalle de venta: " + e.getMessage());
            return false;
        }
    }

}
