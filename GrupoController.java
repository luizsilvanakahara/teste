package br.edu.cruzeirodosul.controller;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.edu.cruzeirodosul.domain.entities.Grupo;
import br.edu.cruzeirodosul.domain.enums.SituacaoConstraint;
import br.edu.cruzeirodosul.persistence.GrupoRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(GrupoController.URL)
@Api(value = "Grupo", tags = { "Grupo" })
public class GrupoController {

    private static final Logger logger = LoggerFactory.getLogger(GrupoController.class);

    public static final String URL = "/grupo";

    public static final int DURATION_TIME = 4;

    @Autowired
    private GrupoRepository grupoRepository;

    @ApiOperation(value = "Persiste Grupo", response = Grupo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201,
                    message = "A requisição foi bem sucedida e um novo recurso foi criado como resultado"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado") })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Grupo> save(@Valid @RequestBody final Grupo grupo) {
        logger.debug("Salvando Grupo");

        final Grupo result = grupoRepository.save(grupo);

        return ResponseEntity.created(URI.create(URL + "/" + result.getId())).body(result);
    }

    @ApiOperation(value = "Busca todos os Grupo por Prefixo", response = Grupo.class, responseContainer = "List")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Grupo>> findByPrefix(
            @ApiParam("Prefixo do nome do grupo.") @RequestParam(value = "nome", required = true) String nome,
            @ApiParam("Situacao do grupo.") @RequestParam(value = "situacao",
                    defaultValue = "ATIVO") SituacaoConstraint[] situacoes) {
        logger.debug("Busca Grupo por Prefixo e situacao");

        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME, ChronoUnit.HOURS)))
                .body(grupoRepository.findByPrefix(nome, situacoes));
    }
}
