# Tienda UTNG

Proyecto integrador — Programacion Orientada a Objetos, UTNG.
Sistema de gestion de productos con persistencia JDBC, pruebas
automatizadas (JUnit 5 + Mockito) y manejo profesional de errores
mediante excepciones propias del dominio.

## Unidades cubiertas

- **U4S1** — Control de versiones con Git.
- **U4S2** — Suite de pruebas automatizadas con JUnit.
- **U4S3** — Excepciones propias del dominio y cierre de Unidad IV.

## Excepciones

El proyecto define tres excepciones propias, todas `RuntimeException`,
ubicadas en el paquete `com.utng.tienda.exception`.

### `PrecioInvalidoException`

- **Cuándo se lanza:** al registrar un producto con `precio < 0`, dentro
  de `ProductoService.validarProducto()`.
- **Datos que expone:** `getPrecio()` — el valor invalido recibido.
- **Cómo manejarla en el llamador:** capturarla para mostrar al usuario
  que el precio debe ser mayor o igual a cero antes de reintentar el
  registro.

```java
try {
    productoService.registrar(producto);
} catch (PrecioInvalidoException e) {
    System.out.println("Precio invalido: " + e.getPrecio());
}
```

### `ProductoNoEncontradoException`

- **Cuándo se lanza:** al vender un producto cuyo `codigo` no existe en
  el catalogo, dentro de `ProductoService.vender()` (via `orElseThrow`).
- **Datos que expone:** `getCodigo()` — el codigo buscado.
- **Cómo manejarla en el llamador:** capturarla para informar que el
  codigo de producto no existe y solicitar uno valido.

```java
try {
    productoService.vender(codigo, cantidad);
} catch (ProductoNoEncontradoException e) {
    System.out.println("No existe el producto: " + e.getCodigo());
}
```

### `StockInsuficienteException`

- **Cuándo se lanza:** al vender una `cantidad` mayor al stock
  disponible, dentro de `ProductoService.vender()`.
- **Datos que expone:** `getCodigoProducto()`, `getStockActual()`,
  `getCantidadSolicitada()`.
- **Cómo manejarla en el llamador:** capturarla para mostrar cuánto
  stock hay realmente disponible y permitir ajustar la cantidad
  solicitada.

```java
try {
    productoService.vender(codigo, cantidad);
} catch (StockInsuficienteException e) {
    System.out.printf("Solo hay %d unidades de %s (pediste %d)%n",
        e.getStockActual(), e.getCodigoProducto(), e.getCantidadSolicitada());
}
```

## Persistencia (`ProductoDAOImpl`)

Todos los metodos que usan JDBC (`insertar`, `findByCodigo`, `findAll`,
`actualizarStock`) usan **try-with-resources** sobre `Connection`,
`PreparedStatement` y `ResultSet`. No hay ningun bloque `finally`
cerrando recursos manualmente.

## Pruebas

`ProductoServiceTest` cubre, con `ProductoDAO` simulado (Mockito):

- Registro valido y sus validaciones (`IllegalArgumentException`,
  `PrecioInvalidoException`).
- Venta exitosa, venta con producto inexistente
  (`ProductoNoEncontradoException`) y venta sin stock suficiente
  (`StockInsuficienteException`, verificando `stockActual` y
  `cantidadSolicitada`).
- Constructor de `ProductoService` con DAO nulo.
- Listado de productos.

Total: 12 tests, todos en verde.
