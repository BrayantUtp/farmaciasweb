package pe.edu.utp.boticaapp.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.boticaapp.service.CatalogoService;
@Controller @RequiredArgsConstructor
public class CatalogoController {
  private final CatalogoService catalogo;
  @GetMapping("/catalogo") public String listar(@RequestParam(required=false) String q, Model model){
    model.addAttribute("q", q);
    model.addAttribute("productos", catalogo.listar(q));
    return "catalogo";
  }
  @GetMapping(value="/catalogo/export", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE) @ResponseBody
  public byte[] exportar(@RequestHeader HttpHeaders headers){ return catalogo.exportarExcel(); }
}
