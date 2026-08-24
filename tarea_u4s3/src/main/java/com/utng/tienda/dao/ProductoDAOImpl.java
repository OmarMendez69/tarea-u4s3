package com.utng.tienda.dao;

import com.utng.tienda.model.Producto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion JDBC de ProductoDAO. Todos los metodos usan
 * try-with-resources para garantizar el cierre de Connection,
 * PreparedStatement y ResultSet, sin bloques finally manuales.
 */
public class ProductoDAOImpl implements ProductoDAO {

    private final String urlConexion;

    public ProductoDAOImpl(String urlConexion) {
        this.urlConexion = urlConexion;
    }

    private Connection abrirConexion() throws SQLException {
        return DriverManager.getConnection(urlConexion);
    }

    @Override
    public void insertar(Producto producto) {
        String sql = "INSERT INTO Producto (codigo, nombre, precio, stock) VALUES (?, ?, ?, ?)";
        try (Connection conn = abrirConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getNombre());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setInt(4, producto.getStock());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar producto: " + producto.getCodigo(), e);
        }
    }

    @Override
    public Optional<Producto> findByCodigo(String codigo) {
        String sql = "SELECT codigo, nombre, precio, stock FROM Producto WHERE codigo = ?";
        try (Connection conn = abrirConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearProducto(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar producto: " + codigo, e);
        }
    }

    @Override
    public List<Producto> findAll() {
        String sql = "SELECT codigo, nombre, precio, stock FROM Producto";
        List<Producto> productos = new ArrayList<>();

        try (Connection conn = abrirConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
            return productos;

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar productos", e);
        }
    }

    @Override
    public void actualizarStock(String codigo, int nuevoStock) {
        String sql = "UPDATE Producto SET stock = ? WHERE codigo = ?";
        try (Connection conn = abrirConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nuevoStock);
            stmt.setString(2, codigo);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar stock del producto: " + codigo, e);
        }
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getDouble("precio"),
                rs.getInt("stock")
        );
    }
}
