package epa.InventoryApp.models;

public enum TipoMovimiento
{
    CREACION_PRODUCTO,
    ACTUALIZACION_UNIDAD,
    ACTUALIZACION_LOTE,
    VENTA_AL_DETALLE,
    VENTA_AL_POR_MAYOR;


    @Override
    public String toString()
    {
        String sTipo = "INDEFINIDO";

        switch (this)
        {
            case CREACION_PRODUCTO:    sTipo = "CREACION_PRODUCTO";    break;
            case ACTUALIZACION_UNIDAD: sTipo = "ACTUALIZACION_UNIDAD"; break;
            case ACTUALIZACION_LOTE:   sTipo = "ACTUALIZACION_LOTE";   break;
            case VENTA_AL_DETALLE:     sTipo = "VENTA_AL_DETALLE";     break;
            case VENTA_AL_POR_MAYOR:   sTipo = "VENTA_AL_POR_MAYOR";   break;
        }

        return sTipo;
    }
}
