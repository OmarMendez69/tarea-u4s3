package com.utng.tienda.service;

import com.utng.tienda.dao.ProductoDAO;
import com.utng.tienda.exception.PrecioInvalidoException;
import com.utng.tienda.exception.ProductoNoEncontradoException;
import com.utng.tienda.exception.StockInsuficienteException;
import com.utng.tienda.model.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductoServiceTest {

    private ProductoDAO dao;
    private ProductoService service;

    @BeforeEach
    void setUp() {
        dao = mock(ProductoDAO.class);
        service = new ProductoService(dao);
    }

    // ---------- Constructor ----------

    @Test
    void constructor_daoNull_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new ProductoService(null));
    }

    // ---------- registrar() ----------

    @Test
    void registrar_productoValido_invocaInsertarEnDAO() {
        Producto p = new Producto("P001", "Mouse", 250.0, 10);

        service.registrar(p);

        verify(dao, times(1)).insertar(p);
    }

    @Test
    void registrar_productoNull_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.registrar(null));
        verifyNoInteractions(dao);
    }

    @Test
    void registrar_codigoVacio_lanzaIllegalArgumentException() {
        Producto p = new Producto("", "Mouse", 250.0, 10);
        assertThrows(IllegalArgumentException.class, () -> service.registrar(p));
    }

    @Test
    void registrar_stockNegativo_lanzaIllegalArgumentException() {
        Producto p = new Producto("P002", "Teclado", 300.0, -5);
        assertThrows(IllegalArgumentException.class, () -> service.registrar(p));
    }

    @Test
    void registrar_precioNegativo_lanzaPrecioInvalidoException() {
        Producto p = new Producto("P003", "Monitor", -100.0, 5);

        PrecioInvalidoException ex = assertThrows(
                PrecioInvalidoException.class, () -> service.registrar(p));

        assertEquals(-100.0, ex.getPrecio());
        verify(dao, never()).insertar(any());
    }

    // ---------- vender() ----------

    @Test
    void vender_productoInexistente_lanzaProductoNoEncontradoException() {
        when(dao.findByCodigo("P999")).thenReturn(Optional.empty());

        ProductoNoEncontradoException ex = assertThrows(
                ProductoNoEncontradoException.class, () -> service.vender("P999", 1));

        assertEquals("P999", ex.getCodigo());
    }

    @Test
    void vender_sinStock_verificaDetallesDeExcepcion() {
        Producto p = new Producto("P004", "Audifonos", 500.0, 3);
        when(dao.findByCodigo("P004")).thenReturn(Optional.of(p));

        StockInsuficienteException ex = assertThrows(
                StockInsuficienteException.class, () -> service.vender("P004", 10));

        assertEquals("P004", ex.getCodigoProducto());
        assertEquals(3, ex.getStockActual());
        assertEquals(10, ex.getCantidadSolicitada());
        verify(dao, never()).actualizarStock(anyString(), anyInt());
    }

    @Test
    void vender_stockSuficiente_actualizaStockCorrectamente() {
        Producto p = new Producto("P005", "Bocina", 400.0, 8);
        when(dao.findByCodigo("P005")).thenReturn(Optional.of(p));

        service.vender("P005", 3);

        verify(dao, times(1)).actualizarStock("P005", 5);
    }

    @Test
    void vender_cantidadCero_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.vender("P006", 0));
        verifyNoInteractions(dao);
    }

    @Test
    void vender_codigoNull_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.vender(null, 1));
    }

    // ---------- listar() ----------

    @Test
    void listar_devuelveListaDelDAO() {
        List<Producto> lista = List.of(new Producto("P007", "Cable", 50.0, 20));
        when(dao.findAll()).thenReturn(lista);

        List<Producto> resultado = service.listar();

        assertEquals(1, resultado.size());
        assertEquals("P007", resultado.get(0).getCodigo());
    }
}
