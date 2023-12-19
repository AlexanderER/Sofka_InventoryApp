package epa.InventoryApp.models.dto;

import epa.InventoryApp.models.TipoMovimiento;

import java.time.LocalDateTime;

public class AgregarInventarioDTO
{
    //--------------------------------------------------- (Variables)
    private String idProducto;
    private Integer cantidad;


    //--------------------------------------------------- (Constructores)
    public AgregarInventarioDTO()
    {
    }

    public AgregarInventarioDTO(String idProducto, Integer cantidad) {
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
