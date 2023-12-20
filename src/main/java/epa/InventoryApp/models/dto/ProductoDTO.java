package epa.InventoryApp.models.dto;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.Objects;

public class ProductoDTO
{
    //--------------------------------------------------- (Variables)
    private String id;
    private String descripcion;
    private Integer existencia;
    private BigDecimal costo;
    private BigDecimal precioUnitarioAlDetalle;
    private BigDecimal precioUnitarioAlPorMayor;
    private Integer unidadesMinimaAlPorMayor;


    //--------------------------------------------------- (Constructores)
    public ProductoDTO()
    {
    }

    public ProductoDTO(String id, String descripcion, Integer existencia, BigDecimal costo, BigDecimal precioUnitarioAlDetalle, BigDecimal precioUnitarioAlPorMayor, Integer unidadesMinimaAlPorMayor)
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

    //--------------------------------------------------- (Equals y HashCode)

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ProductoDTO that = (ProductoDTO) object;
        return Objects.equals(id, that.id) && Objects.equals(descripcion, that.descripcion) && Objects.equals(existencia, that.existencia) && Objects.equals(costo, that.costo) && Objects.equals(precioUnitarioAlDetalle, that.precioUnitarioAlDetalle) && Objects.equals(precioUnitarioAlPorMayor, that.precioUnitarioAlPorMayor) && Objects.equals(unidadesMinimaAlPorMayor, that.unidadesMinimaAlPorMayor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, descripcion, existencia, costo, precioUnitarioAlDetalle, precioUnitarioAlPorMayor, unidadesMinimaAlPorMayor);
    }
}
