package epa.InventoryApp.models.dto;

import epa.InventoryApp.models.TipoMovimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientosDTO
{
    //--------------------------------------------------- (Variables)
    //private String id;
    private LocalDateTime fecha;
    private String idProducto;
    private TipoMovimiento tipo;
    private Integer cantidad;
    private Integer existenciaInicial;
    private Integer existenciaFinal;
    private BigDecimal costo;
    private BigDecimal precio;

    //--------------------------------------------------- (Constructor)
    public MovimientosDTO()
    {
    }

    public MovimientosDTO(LocalDateTime fecha, String idProducto, TipoMovimiento tipo, Integer cantidad, Integer existenciaInicial, Integer existenciaFinal, BigDecimal costo, BigDecimal precio) {
        this.fecha = fecha;
        this.idProducto = idProducto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.existenciaInicial = existenciaInicial;
        this.existenciaFinal = existenciaFinal;
        this.costo = costo;
        this.precio = precio;
    }

    //--------------------------------------------------- (Setter y Getter)


    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getExistenciaInicial() {
        return existenciaInicial;
    }

    public void setExistenciaInicial(Integer existenciaInicial) {
        this.existenciaInicial = existenciaInicial;
    }

    public Integer getExistenciaFinal() {
        return existenciaFinal;
    }

    public void setExistenciaFinal(Integer existenciaFinal) {
        this.existenciaFinal = existenciaFinal;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}
