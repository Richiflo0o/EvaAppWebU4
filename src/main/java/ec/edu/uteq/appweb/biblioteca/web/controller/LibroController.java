package ec.edu.uteq.appweb.biblioteca.web.controller;

import ec.edu.uteq.appweb.biblioteca.domain.Libro;
import ec.edu.uteq.appweb.biblioteca.service.LibroService;
import ec.edu.uteq.appweb.biblioteca.web.dto.ApiResponse;
import ec.edu.uteq.appweb.biblioteca.web.dto.LibroResponse;
import ec.edu.uteq.appweb.biblioteca.web.dto.PageMeta;
import ec.edu.uteq.appweb.biblioteca.web.mapper.LibroMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/libros")
public class LibroController {

    private final LibroService libroService;
    private final LibroMapper libroMapper;

    public LibroController(LibroService libroService, LibroMapper libroMapper) {
        this.libroService = libroService;
        this.libroMapper = libroMapper;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<LibroResponse>>> listar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Integer anioDesde,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<Libro> libros = libroService.buscar(titulo, categoriaId, anioDesde, pageable);

        List<LibroResponse> respuesta = libros.getContent().stream()
                .map(libroMapper::aRespuesta)
                .toList();

        PageMeta meta = PageMeta.de(libros);

        return ResponseEntity.ok(ApiResponse.ok(respuesta, "Libros listados", meta));
    }
}
