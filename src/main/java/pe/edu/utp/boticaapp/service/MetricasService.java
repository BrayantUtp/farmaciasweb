package pe.edu.utp.boticaapp.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.utp.boticaapp.repository.*;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class MetricasService {
    private final MeterRegistry meterRegistry;
    private final UsuarioRepository usuarioRepo;
    private final ProductoRepository productoRepo;
    private final PedidoRepository pedidoRepo;
    private final CarritoItemRepository carritoRepo;

    private final AtomicInteger usuariosActivos = new AtomicInteger(0);
    private final AtomicInteger productosVistos = new AtomicInteger(0);
    private final AtomicInteger pedidosCompletados = new AtomicInteger(0);

    @PostConstruct
    private void inicializarMetricas() {
        // repositorios: count() devuelve long, se usa Supplier mediante lambda
        Gauge.builder("botica.usuarios.total", usuarioRepo::count)
            .description("Total de usuarios registrados")
            .register(meterRegistry);

        Gauge.builder("botica.productos.total", productoRepo::count)
            .description("Total de productos en catálogo")
            .register(meterRegistry);

        Gauge.builder("botica.pedidos.total", pedidoRepo::count)
            .description("Total de pedidos registrados")
            .register(meterRegistry);

        Gauge.builder("botica.carrito.items.activos", carritoRepo::count)
            .description("Total de items en carritos")
            .register(meterRegistry);

        // métricas en memoria basadas en AtomicInteger: usar overload (objeto, ToDoubleFunction)
        Gauge.builder("botica.usuarios.activos", usuariosActivos, AtomicInteger::doubleValue)
            .description("Usuarios autenticados")
            .register(meterRegistry);

        Gauge.builder("botica.productos.vistos", productosVistos, AtomicInteger::doubleValue)
            .description("Productos consultados")
            .register(meterRegistry);

        Gauge.builder("botica.pedidos.completados", pedidosCompletados, AtomicInteger::doubleValue)
            .description("Pedidos completados")
            .register(meterRegistry);
    }

    public void registrarUsuarioActivo() {
        usuariosActivos.incrementAndGet();
    }

    public void registrarProductoVisto() {
        productosVistos.incrementAndGet();
    }

    public void registrarPedidoCompletado() {
        pedidosCompletados.incrementAndGet();
    }

    public void decrementarUsuarioActivo() {
        usuariosActivos.decrementAndGet();
    }

    public void registrarError(String controlador, String metodo, Exception ex) {
        Counter.builder("botica.errores")
            .tag("controlador", controlador)
            .tag("metodo", metodo)
            .tag("excepcion", ex.getClass().getSimpleName())
            .register(meterRegistry)
            .increment();
    }
}
