package epa.InventoryApp.models.dto;

import epa.InventoryApp.models.TipoMovimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientosDTObuilder
{
    //--------------------------------------------------- (Variables)
    MovimientosDTO movimiento;


    //--------------------------------------------------- (Constructor)
    public MovimientosDTObuilder()
    {
        movimiento = new MovimientosDTO();
        movimiento.setFecha(LocalDateTime.now());
    }


    //--------------------------------------------------- (Build)
    public  MovimientosDTO build()
    {
        return this.movimiento;
    }


    //--------------------------------------------------- (Setter)
    public MovimientosDTObuilder setIdProducto(String idProducto)
    {
        movimiento.setIdProducto(idProducto);
        return this;
    }

    public MovimientosDTObuilder setTipo(TipoMovimiento tipo)
    {
        movimiento.setTipo(tipo);
        return this;
    }

    public MovimientosDTObuilder setCantidad(Integer cantidad)
    {
        movimiento.setCantidad(cantidad);
        return this;
    }

    public MovimientosDTObuilder setExistenciaInicial(Integer existenciaInicial)
    {
        movimiento.setExistenciaInicial(existenciaInicial);
        return this;
    }

    public MovimientosDTObuilder setExistenciaFinal(Integer existenciaFinal)
    {
        movimiento.setExistenciaFinal(existenciaFinal);
        return this;
    }

    public MovimientosDTObuilder setCosto(BigDecimal costo)
    {
        movimiento.setCosto(costo);
        return this;
    }

    public MovimientosDTObuilder setPrecio(BigDecimal precio)
    {
        movimiento.setPrecio(precio);
        return this;
    }

}
