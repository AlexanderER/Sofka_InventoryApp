package epa.InventoryApp.drivenAdapters.repository;

import epa.InventoryApp.models.Producto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductoRepository extends ReactiveMongoRepository<Producto, String>
{
}
