package pe.edu.utp.boticaapp.controller;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.utp.boticaapp.entity.Usuario;
import pe.edu.utp.boticaapp.repository.UsuarioRepository;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.util.*;

@Controller
@RequestMapping("/admin/monitor")
@RequiredArgsConstructor
public class AdminMonitorController {
    
    private final MeterRegistry meterRegistry;
    private final UsuarioRepository usuarioRepo;
    private final HealthEndpoint healthEndpoint;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String dashboard(@AuthenticationPrincipal User p, Model model) {
        if (p == null) {
            throw new AccessDeniedException("Acceso restringido");
        }
        var usuario = usuarioRepo.findByEmail(p.getUsername()).orElseThrow();
        if (usuario.getRol() != Usuario.Rol.ADMIN) {
            throw new AccessDeniedException("Acceso restringido");
        }

        HealthComponent health = healthEndpoint.health();
        model.addAttribute("healthStatus", health.getStatus().toString());

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        long heapUsed = memoryMXBean.getHeapMemoryUsage().getUsed();
        long heapMax = memoryMXBean.getHeapMemoryUsage().getMax();
        
        model.addAttribute("heapUsedMB", heapUsed / 1024 / 1024);
        model.addAttribute("heapMaxMB", heapMax / 1024 / 1024);
        model.addAttribute("heapPercentage", (int) ((heapUsed * 100) / heapMax));

        com.sun.management.OperatingSystemMXBean osBean =
            (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        // use getProcessCpuLoad() which returns a value between 0.0 and 1.0 or a negative value if not available
        double processCpuLoad = osBean.getProcessCpuLoad();
        double cpuUsage = (processCpuLoad < 0) ? 0.0 : processCpuLoad * 100.0;
        model.addAttribute("cpuUsage", String.format("%.2f", cpuUsage));

        long uptimeMs = runtimeMXBean.getUptime();
        Duration uptime = Duration.ofMillis(uptimeMs);
        long days = uptime.toDays();
        long hours = uptime.toHoursPart();
        long minutes = uptime.toMinutesPart();
        model.addAttribute("uptimeFormatted", String.format("%d days, %d hours, %d minutes", days, hours, minutes));

        int threadCount = threadMXBean.getThreadCount();
        int peakThreadCount = threadMXBean.getPeakThreadCount();
        model.addAttribute("threadCount", threadCount);
        model.addAttribute("peakThreadCount", peakThreadCount);

        try {
            long totalUsuarios = usuarioRepo.count();
            model.addAttribute("totalUsuarios", totalUsuarios);
        } catch (Exception e) {
            model.addAttribute("totalUsuarios", "N/A");
        }

        var metrics = new HashMap<String, Double>();
        meterRegistry.forEachMeter(meter -> {
            String name = meter.getId().getName();
            if (name.startsWith("botica.")) {
                try {
                    if (meter.getId().getType().name().equals("GAUGE")) {
                        metrics.put(name, ((io.micrometer.core.instrument.Gauge) meter).value());
                    }
                } catch (Exception ignored) {}
            }
        });

        model.addAttribute("customMetrics", metrics);
        model.addAttribute("appName", "BoticaApp Web MVC");
        model.addAttribute("appVersion", "0.0.1-SNAPSHOT");
        model.addAttribute("javaVersion", System.getProperty("java.version"));
        model.addAttribute("osName", System.getProperty("os.name"));
        model.addAttribute("osVersion", System.getProperty("os.version"));

        return "admin/monitor";
    }
}
