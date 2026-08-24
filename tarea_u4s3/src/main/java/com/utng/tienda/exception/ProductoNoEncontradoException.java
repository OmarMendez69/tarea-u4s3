package com.utng.tienda.exception;

/**
 * Excepcion de dominio lanzada cuando se busca un producto por su codigo
 * y este no existe en el catalogo.
 */
public class ProductoNoEncontradoException extends RuntimeException {

    private final String codigo;

    /**
     * @param codigo codigo del producto que no fue encontrado
     */
    public ProductoNoEncontradoException(String codigo) {
        super("Producto no encontrado con codigo: " + codigo);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
