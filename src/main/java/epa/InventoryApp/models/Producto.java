package epa.InventoryApp.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document("Producto")
public class Producto
{
    //--------------------------------------------------- (Variables)
    @Id
    private String id;
    private String descripcion;
    private Integer existencia;
    private BigDecimal costo;
    private BigDecimal precioUnitarioAlDetalle;
    private BigDecimal precioUnitarioAlPorMayor;
    private Integer unidadesMinimaAlPorMayor;

    //--------------------------------------------------- (Constructores)
    public Producto()
    {
    }

    public Producto(String id, String descripcion, Integer existencia, BigDecimal costo, BigDecimal precioUnitarioAlDetalle, BigDecimal precioUnitarioAlPorMayor, Integer unidadesMinimaAlPorMayor)
    {
        this.id = id;
        this.descripcion = descripcion;
        this.existencia = existencia;
        this.costo = costo;
        this.precioUnitarioAlDetalle = precioUnitarioAlDetalle;
        this.precioUnitarioAlPorMayor = precioUnitarioAlPorMayor;
        this.unidadesMinimaAlPorMayor = unidadesMinimaAlPorMayor;
    }

    //--------------------------------------------------- (Getter y Setter)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getExistencia() {
        return existencia;
    }

    public void setExistencia(Integer existencia) {
        this.existencia = existencia;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public BigDecimal getPrecioUnitarioAlDetalle() {
        return precioUnitarioAlDetalle;
    }

    public void setPrecioUnitarioAlDetalle(BigDecimal precioUnitarioAlDetalle) {
        this.precioUnitarioAlDetalle = precioUnitarioAlDetalle;
    }

    public BigDecimal getPrecioUnitarioAlPorMayor() {
        return precioUnitarioAlPorMayor;
    }

    public void setPrecioUnitarioAlPorMayor(BigDecimal precioUnitarioAlPorMayor) {
        this.precioUnitarioAlPorMayor = precioUnitarioAlPorMayor;
    }

    public Integer getUnidadesMinimaAlPorMayor() {
        return unidadesMinimaAlPorMayor;
    }

    public void setUnidadesMinimaAlPorMayor(Integer unidadesMinimaAlPorMayor) {
        this.unidadesMinimaAlPorMayor = unidadesMinimaAlPorMayor;
    }
}
