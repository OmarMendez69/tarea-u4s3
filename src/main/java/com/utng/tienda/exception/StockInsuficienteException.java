package com.utng.tienda.exception;

/**
 * Excepcion de dominio lanzada cuando se intenta vender una cantidad
 * de producto mayor al stock disponible en inventario.
 */
public class StockInsuficienteException extends RuntimeException {

    private final String codigoProducto;
    private final int stockActual;
    private final int cantidadSolicitada;

    /**
     * @param codigoProducto codigo del producto sobre el que se intento la venta
     * @param stockActual stock actualmente disponible en inventario
     * @param cantidadSolicitada cantidad que el cliente solicito comprar
     */
    public StockInsuficienteException(String codigoProducto, int stockActual, int cantidadSolicitada) {
        super(String.format(
                "Stock insuficiente para el producto %s: disponible=%d, solicitado=%d",
                codigoProducto, stockActual, cantidadSolicitada));
        this.codigoProducto = codigoProducto;
        this.stockActual = stockActual;
        this.cantidadSolicitada = cantidadSolicitada;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public int getStockActual() {
        return stockActual;
    }

    public int getCantidadSolicitada() {
        return cantidadSolicitada;
    }
}
