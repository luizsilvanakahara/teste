package br.edu.cruzeirodosul.controller;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.edu.cruzeirodosul.domain.entities.Regulamento;
import br.edu.cruzeirodosul.persistence.RegulamentoRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(RegulamentoController.URL)
@Api(value = "Regulamento", tags = { "Regulamento" })
@Slf4j
public class RegulamentoController {

	public static final String URL = "/regulamento";
	
    public static final int DURATION_TIME = 4;    

	@Autowired
	private RegulamentoRepository repository;

	@ApiOperation(value = "Persiste regulamento", response = Regulamento.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Operação realizada com sucesso"),
			@ApiResponse(code = 401, message = "Não autorizado para visualizar"),
			@ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
			@ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado"),
			@ApiResponse(code = 412, message = "Os dados enviados não atendem todas as condições necessárias") })
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = { RequestMethod.POST, RequestMethod.PUT }, 
		produces = MediaType.APPLICATION_JSON_VALUE, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Regulamento> save(@Valid @RequestBody Regulamento regulamento) {
		
		log.debug("Salvando Regulamento {}", regulamento);

		return ResponseEntity.ok(repository.save(Regulamento.builder()
				.id(Regulamento.DEFAULT_ID_VALUE)
				.dataAtualizacao(regulamento.getDataAtualizacao())
				.link(regulamento.getLink())
				.idAnexo(regulamento.getIdAnexo())
				.exibeQuadro(regulamento.isExibeQuadro())
				.build()));

	}

	@ApiOperation(value = "Busca regulamento", response = Regulamento.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
			@ApiResponse(code = 401, message = "Não autorizado para visualizar"),
			@ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
			@ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado"),
			@ApiResponse(code = 412, message = "Os dados enviados não atendem todas as condições necessárias") })
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Regulamento> getRegulamento() {
		
		log.debug("Buscando regulamento");
						
		return repository.findById(Regulamento.DEFAULT_ID_VALUE)
				.filter(r -> !StringUtils.isEmpty(r.getLink()) || r.getIdAnexo() != null)
				.map(r -> ResponseEntity.ok()
						.cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME, ChronoUnit.HOURS))).body(r))
    			.orElseThrow(() -> new EmptyResultDataAccessException(0));   
	}
	
}
