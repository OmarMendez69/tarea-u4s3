package com.utng.tienda.dao;

import com.utng.tienda.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoDAO {

    void insertar(Producto producto);

    Optional<Producto> findByCodigo(String codigo);

    List<Producto> findAll();

    void actualizarStock(String codigo, int nuevoStock);
}
