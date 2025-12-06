package pe.edu.utp.boticaapp.service.impl;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import pe.edu.utp.boticaapp.repository.ProductoRepository;
import pe.edu.utp.boticaapp.entity.Producto;
import pe.edu.utp.boticaapp.service.CatalogoService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
@Service @RequiredArgsConstructor
public class CatalogoServiceImpl implements CatalogoService {
  private final ProductoRepository repo;
  @Override public List<Producto> listar(String q){ return (q==null||q.isBlank())? repo.findAll(): repo.findByNombreContainingIgnoreCase(q); }
  @Override public byte[] exportarExcel(){
    try(var wb=new XSSFWorkbook(); var out=new ByteArrayOutputStream()){ 
      var sh=wb.createSheet("Productos"); int r=0;
      var h=sh.createRow(r++); h.createCell(0).setCellValue("ID"); h.createCell(1).setCellValue("Nombre"); h.createCell(2).setCellValue("Precio"); h.createCell(3).setCellValue("Categoria");
      for(Producto p: repo.findAll()){ var row=sh.createRow(r++); row.createCell(0).setCellValue(p.getId()); row.createCell(1).setCellValue(p.getNombre()); 
        row.createCell(2).setCellValue(p.getPrecio().doubleValue()); row.createCell(3).setCellValue(p.getCategoria()); }
      wb.write(out); return out.toByteArray();
    } catch (IOException e){ throw new RuntimeException(e); }
  }
}
