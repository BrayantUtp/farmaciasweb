package pe.edu.utp.boticaapp.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.boticaapp.entity.Stock;
import pe.edu.utp.boticaapp.service.CatalogoService;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
@Controller @RequiredArgsConstructor
public class CatalogoController {
  private final CatalogoService catalogo;
  @GetMapping("/catalogo") public String listar(@RequestParam(required=false) String q, Model model){
    model.addAttribute("q", q);
    var productos = catalogo.listar(q);
    model.addAttribute("productos", productos);
    // construir mapa productoId -> lista de hasta 3 stocks con cantidad>0 ordenadas por precio
    Map<Long, List<Stock>> stocksMap = new HashMap<>();
    // mapa productoId -> precio formateado de la mejor oferta (String), se rellena incluso si no hay stock
    Map<Long, String> bestPriceMap = new HashMap<>();
    for(var p: productos){
      var stocks = catalogo.topStockPorProducto(p.getId(), 3);
      stocksMap.put(p.getId(), stocks);
      if(stocks != null && !stocks.isEmpty()){
        var precio = stocks.get(0).getPrecio();
        bestPriceMap.put(p.getId(), String.format(Locale.US, "%.2f", precio.doubleValue()));
      } else {
        bestPriceMap.put(p.getId(), String.format(Locale.US, "%.2f", p.getPrecio().doubleValue()));
      }
    }
    model.addAttribute("stocksMap", stocksMap);
    model.addAttribute("bestPriceMap", bestPriceMap);
    return "catalogo";
  }
  @GetMapping(value="/catalogo/export", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE) @ResponseBody
  public byte[] exportar(@RequestHeader HttpHeaders headers){ return catalogo.exportarExcel(); }
}
