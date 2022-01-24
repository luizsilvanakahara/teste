package br.edu.cruzeirodosul.controller;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import br.edu.cruzeirodosul.domain.entities.QuadroGrupoTipoComprovante;
import br.edu.cruzeirodosul.domain.entities.TipoComprovante;
import br.edu.cruzeirodosul.domain.enums.SituacaoConstraint;
import br.edu.cruzeirodosul.persistence.QuadroGrupoTipoComprovanteRepository;
import br.edu.cruzeirodosul.persistence.TipoComprovanteRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(TipoComprovanteController.URL)
@Api(value = "TipoComprovante", tags = { "TipoComprovante" })
public class TipoComprovanteController {

    private static final Logger logger = LoggerFactory.getLogger(TipoComprovanteController.class);

    public static final String URL = "/tipo-comprovante";

    public static final int DURATION_TIME = 4;
    
    private final TipoComprovanteRepository repository;
    private final QuadroGrupoTipoComprovanteRepository quadroGrupoTipoComprovanteRepository;

    @ApiOperation(value = "Persiste TipoComprovante", response = TipoComprovante.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201,
                    message = "A requisição foi bem sucedida e um novo recurso foi criado como resultado"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado") })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TipoComprovante> save(@Valid @RequestBody final TipoComprovante tipoComprovante) {
        logger.debug("Salvando TipoComprovante");

        final TipoComprovante result = repository.save(tipoComprovante);

        return ResponseEntity.created(URI.create(URL + "/" + result.getId())).body(result);
    }

    @ApiOperation(value = "Atualiza TipoComprovante", response = TipoComprovante.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 201,
                    message = "A requisição foi bem sucedida e um novo recurso foi criado como resultado"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @PutMapping(value = { "/{id}" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TipoComprovante> update(@PathVariable(required = true) Long id,
            @Valid @RequestBody TipoComprovante tipoComprovante) {

        logger.debug("Atualizando TipoComprovante");

        return ResponseEntity.ok().body(repository.save(TipoComprovante.builder().id(id).nome(tipoComprovante.getNome())
                .situacao(tipoComprovante.getSituacao()).build()));
    }

    @ApiOperation(value = "Busca TipoComprovante por id", response = TipoComprovante.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TipoComprovante> findById(@PathVariable(required = true) Long id) {
        logger.debug("Encontrando TipoComprovante por id");

        return repository.findById(id).map(
                t -> ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME,
                        ChronoUnit.HOURS))).body(t))
                .orElse(ResponseEntity.notFound().build());
    }       

    @ApiOperation(value = "Busca todos os TipoComprovantes por Prefixo", response = TipoComprovante.class,
            responseContainer = "List")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TipoComprovante>> findByPrefix(
            @ApiParam("Prefixo do nome do tipo comprovante") @RequestParam(value = "nome", required = true) String nome,
            @ApiParam("Situacao do tipo comprovante.") @RequestParam(value = "situacao",
                    defaultValue = "ATIVO") SituacaoConstraint[] situacoes) {
        logger.debug("Busca TipoComprovantes por prefixo e situacao");

        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME,
                ChronoUnit.HOURS)))
                .body(repository.findByPrefix(nome, situacoes));
    }
    
    @ApiOperation(value = "Busca quadro grupo do tipo do comprovante por id", response = QuadroGrupoTipoComprovante.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
    		@ApiResponse(code = 204, message = "Não há conteúdo para enviar para esta solicitação"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @GetMapping(value = "/quadro-grupo-tipo-comprovante/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuadroGrupoTipoComprovante> findQuadroGrupoTipoComprovanteById(
    		@PathVariable(name = "id") Long idQuadroGrupoTipoComprovante) {
    	
    	logger.debug("Buscando QuadroGrupoTipoComprovante por id");
    	
    	return quadroGrupoTipoComprovanteRepository.findById(idQuadroGrupoTipoComprovante)
    			.map(qgtc -> ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME, ChronoUnit.HOURS)))
    					.body(qgtc)).orElse(ResponseEntity.noContent().build());    	    
    }

    @ApiOperation(value = "Deleta TipoComprovantes")
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Não há conteúdo para enviar para esta solicitação"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido") })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{id}")
    public void deleteById(@PathVariable Long id) {
        logger.debug("Deletando TipoComprovante por id");
        repository.deleteById(id);
    }

}
