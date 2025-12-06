package pe.edu.utp.boticaapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.boticaapp.service.CatalogoService;
import pe.edu.utp.boticaapp.service.StockService;

@Controller
@RequiredArgsConstructor
public class CatalogoController {
    
    private final CatalogoService catalogo;
    private final StockService stockService;
    
    @GetMapping("/catalogo")
    public String listar(@RequestParam(required = false) String q, Model model) {
        var productos = catalogo.listar(q);
        
        // Agregar stock disponible para cada producto
        productos.forEach(producto -> {
            Integer stockDisponible = stockService.obtenerCantidadTotalDisponible(producto.getId());
            producto.setStock(stockDisponible); // Temporal en memoria
        });
        
        model.addAttribute("q", q);
        model.addAttribute("productos", productos);
        
        return "catalogo";
    }
    
    @GetMapping(value = "/catalogo/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public byte[] exportar(@RequestHeader HttpHeaders headers) {
        return catalogo.exportarExcel();
    }
}