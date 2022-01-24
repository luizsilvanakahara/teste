package br.edu.cruzeirodosul.controller;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import br.edu.cruzeirodosul.domain.dto.ComprovanteAtividadeComplementarTrilhaAlunoDTO;
//import br.edu.cruzeirodosul.persistence.ComprovanteAtividadeComplementarTrilhaRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//@Slf4j
//@RequiredArgsConstructor
//@RestController
//@RequestMapping(ComprovanteAtividadeComplementarTrilhaController.URL)
//@Api(value = "Comprovante Atividades Complementares", tags = { "Comprovante Atividades Complementares" })
//public class ComprovanteAtividadeComplementarTrilhaController {
//
//	public static final String URL = "comprovante/trilha";
//
//	public static final int DURATION_TIME = 4;
//
//	private final ComprovanteAtividadeComplementarTrilhaRepository repository;
//	
//
//	@ApiOperation(value = "Busca trilha por id do RGM do aluno e id do curso", 
//			response = ComprovanteAtividadeComplementarTrilhaAlunoDTO.class, responseContainer = "List")
//	@ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),			
//			@ApiResponse(code = 401, message = "Não autorizado para visualizar"),
//			@ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
//			@ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
//	@GetMapping(params = { "idAlunoRgm", "idCurso" }, produces = MediaType.APPLICATION_JSON_VALUE)
//	
//
//	public ResponseEntity<List<ComprovanteAtividadeComplementarTrilhaAlunoDTO>> findByIdAlunoRgmAndIdCurso(
//			@ApiParam("id do RGM do aluno") @RequestParam Long idAlunoRgm,
//			@ApiParam("id do curso") @RequestParam Long idCurso) {
//		
//		log.debug("Buscando Trilha por idAlunoRgm {} e idCurso {}", idAlunoRgm, idCurso);
//
//		return ResponseEntity.ok()
//				.cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME, ChronoUnit.HOURS)).cachePrivate())
//				.body(repository.findByIdAlunoRgmAndIdCurso(idAlunoRgm, idCurso));	
//	}	
//
//}
