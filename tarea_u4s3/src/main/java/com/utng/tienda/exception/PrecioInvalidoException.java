package com.utng.tienda.exception;

/**
 * Excepcion de dominio lanzada cuando se intenta registrar o actualizar
 * un producto con un precio invalido (menor a cero).
 */
public class PrecioInvalidoException extends RuntimeException {

    private final double precio;

    /**
     * @param precio valor de precio invalido que se intento asignar
     */
    public PrecioInvalidoException(double precio) {
        super(String.format(
                "Precio invalido: %.2f. Se esperaba un valor >= 0", precio));
        this.precio = precio;
    }

    public double getPrecio() {
        return precio;
    }
}
