package com.utng.tienda.service;

import com.utng.tienda.dao.ProductoDAO;
import com.utng.tienda.exception.PrecioInvalidoException;
import com.utng.tienda.exception.ProductoNoEncontradoException;
import com.utng.tienda.exception.StockInsuficienteException;
import com.utng.tienda.model.Producto;

import java.util.List;

public class ProductoService {

    private final ProductoDAO dao;

    public ProductoService(ProductoDAO dao) {
        if (dao == null) {
            throw new IllegalArgumentException("El DAO no puede ser null");
        }
        this.dao = dao;
    }

    /**
     * Registra un nuevo producto en el catalogo, validando sus datos
     * antes de persistirlo.
     */
    public void registrar(Producto p) {
        if (p == null) {
            throw new IllegalArgumentException("El producto no puede ser null");
        }
        validarProducto(p);
        dao.insertar(p);
    }

    /**
     * Realiza la venta de una cantidad de producto, descontando stock.
     */
    public void vender(String codigo, int cantidad) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El codigo no puede ser null ni vacio");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }

        Producto producto = dao.findByCodigo(codigo)
                .orElseThrow(() -> new ProductoNoEncontradoException(codigo));

        if (producto.getStock() < cantidad) {
            throw new StockInsuficienteException(codigo, producto.getStock(), cantidad);
        }

        dao.actualizarStock(codigo, producto.getStock() - cantidad);
    }

    public List<Producto> listar() {
        return dao.findAll();
    }

    /**
     * Valida los datos de negocio de un producto antes de persistirlo.
     * Lanza PrecioInvalidoException para precio negativo e
     * IllegalArgumentException para datos nulos o stock negativo.
     */
    private void validarProducto(Producto p) {
        if (p.getCodigo() == null || p.getCodigo().isBlank()) {
            throw new IllegalArgumentException("El codigo del producto no puede ser null ni vacio");
        }
        if (p.getNombre() == null || p.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto no puede ser null ni vacio");
        }
        if (p.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        if (p.getPrecio() < 0) {
            throw new PrecioInvalidoException(p.getPrecio());
        }
    }
}
