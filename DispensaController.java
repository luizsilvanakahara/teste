package br.edu.cruzeirodosul.controller;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.persistence.EntityManager;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.edu.cruzeirodosul.domain.entities.Dispensa;
import br.edu.cruzeirodosul.persistence.DispensaRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(DispensaController.URL)
@Api(value = "Dispensa", tags = { "Dispensa" })
public class DispensaController {

	private static final Logger logger = LoggerFactory.getLogger(DispensaController.class);

	public static final String URL = "/dispensa";

	public static final int DURATION_TIME = 4;

	private final DispensaRepository repository;
	
    @Autowired
    private final EntityManager em;

    @Transactional
	@ApiOperation(value = "Persiste Dispensa", response = Dispensa.class)
	@ApiResponses(value = {
			@ApiResponse(code = 201,
					message = "A requisição foi bem sucedida e um novo recurso foi criado como resultado"), 
			@ApiResponse(code = 401, message = "Não autorizado para visualizar"),
			@ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
			@ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado") })
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Dispensa> save(@Valid @RequestBody final Dispensa dispensa) {
		logger.debug("Salvando Dispensa");
		Dispensa result = repository.save(dispensa);
		em.flush();
		em.clear();
		result = repository.getOne(result.getId());
		return ResponseEntity.created(URI.create(URL + "/" + result.getId())).body(result);
	}
    
    @ApiOperation(value = "Busca Dispensa por id", response = Dispensa.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),    		
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dispensa> findById(@PathVariable(required = true) Long id) {
    	
        logger.debug("Encontrando Dispensa por id");

        return repository.findById(id).map(
                t -> ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME,
                        ChronoUnit.HOURS))).body(t))
                .orElse(ResponseEntity.notFound().build());
    }
	
	@ApiOperation(value = "Busca Dispensa por (id RGM do aluno) ou (id RGM do aluno e id curso)", 
			response = Dispensa.class, responseContainer = "List")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 400, message = "Requisição incorreta."),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado") })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Dispensa>> findByCriterios(
    		@ApiParam("Id RGM do aluno. Deve ser informado para executar a busca de dispensa filtrando pelo id RGM do aluno") 
    		@RequestParam(value = "idAlunoRgm", required = false) Long idAlunoRgm,
            @ApiParam("Id curso. Quando informado irá executar a busca de dispensa filtrando pelo id do curso, sendo obigatório"
                    + " informar também o id RGM do aluno") 
    		@RequestParam(value = "idCurso", required = false) Long idCurso) {

        logger.debug("Método utilizado apenas para documentação Swagger/OpenAPI");

        return ResponseEntity.badRequest().build();
    }

	@ApiOperation(hidden = true, value = "Busca dispensa(s) por id RGM do aluno", 
			response = Dispensa.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
			@ApiResponse(code = 401, message = "Não autorizado para visualizar"),
			@ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
			@ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
	@GetMapping(params = "idAlunoRgm", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Dispensa>> findByIdAlunoRgm(
			@ApiParam("id do RGM do aluno") @RequestParam Long idAlunoRgm) {
		
		logger.debug("Buscando Dispensa(s) por idAlunoRgm");
		
		return ResponseEntity.ok()
				.cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME, ChronoUnit.HOURS)).cachePrivate())
				.body(repository.findByIdAlunoRgm(idAlunoRgm));	
								
	}
	
	@ApiOperation(hidden = true, value = "Busca dispensa por id do RGM do aluno e id do curso", 
			response = Dispensa.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),			
			@ApiResponse(code = 401, message = "Não autorizado para visualizar"),
			@ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
			@ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
	@GetMapping(params = { "idAlunoRgm", "idCurso" }, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Dispensa>> findByIdAlunoRgmAndIdCurso(
			@ApiParam("id do RGM do aluno") @RequestParam Long idAlunoRgm,
			@ApiParam("id do curso") @RequestParam Long idCurso) {
		
		logger.debug("Buscando Dispensa por idAlunoRgm e idCurso");

		return ResponseEntity.ok()
				.cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME, ChronoUnit.HOURS)).cachePrivate())
				.body(repository.findByIdAlunoRgmAndIdCurso(idAlunoRgm, idCurso));	
	}
	
	@ApiOperation(value = "Persiste codigoInstituicao, idAluRgm e idPolo Comprovante Atividades Complementares "
			+ "(update parcial utilizando método HTTP PUT e não PATCH, pois é chamado pelo legado)", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "A requisição foi bem sucedida o recurso foi atualizado"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado") })
	@ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/idAlunoRgm/{idAlunoRgmOld}/idCurso/{idCurso}", produces = MediaType.APPLICATION_JSON_VALUE)	
    public void updateIdAlunoRgmDispensa(@PathVariable(required = true) Long idAlunoRgmOld, @PathVariable(required = true) Long idCurso,
    		@RequestBody final Long idAlunoRgmNew) {
		
		logger.debug("Atualizando idAlunoRgm da dispensa do aluno");
    	
        repository.updateIdAlunoRgmDispensa(idAlunoRgmNew, idAlunoRgmOld, idCurso);  
    }

	@Transactional
	@ApiOperation(value = "Atualiza Dispensa", response = Dispensa.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
			@ApiResponse(code = 401, message = "Não autorizado para visualizar"),
			@ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
			@ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
	@PutMapping(value = { "/{id}" }, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Dispensa> update(@PathVariable(required = true) Long id,
			@Valid @RequestBody Dispensa dispensa) {

		logger.debug("Atualizando Dispensa");
		repository.save(Dispensa.builder()
				.id(id)
				.idAlunoRgm(dispensa.getIdAlunoRgm())
				.quantidadeHoras(dispensa.getQuantidadeHoras())
				.idCurso(dispensa.getIdCurso())
				.descricao(dispensa.getDescricao())
				.situacaoTemasTransversais(dispensa.getSituacaoTemasTransversais())
				.usuario(dispensa.getUsuario()). build());
		em.flush();
		em.clear();
		return ResponseEntity.ok().body(repository.getOne(id));
	}

	@ApiOperation(value = "Deleta Dispensa")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Não há conteúdo para enviar para esta solicitação"),
			@ApiResponse(code = 401, message = "Não autorizado para visualizar"),
			@ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido") })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping(value = "/{id}")
	public void deleteById(@PathVariable Long id) {
		logger.debug("Deletando Dispensa por id");
		repository.deleteById(id);
	}

}
