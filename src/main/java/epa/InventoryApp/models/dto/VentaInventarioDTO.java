package epa.InventoryApp.models.dto;

public class VentaInventarioDTO
{
    //--------------------------------------------------- (Variables)
    private String idProducto;
    private Integer cantidad;


    //--------------------------------------------------- (Constructores)
    public VentaInventarioDTO()
    {
    }

    public VentaInventarioDTO(String idProducto, Integer cantidad)
    {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    //--------------------------------------------------- (Getter y Setter)
    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
