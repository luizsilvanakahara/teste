package br.edu.cruzeirodosul.controller;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.validation.Valid;

import org.springframework.dao.EmptyResultDataAccessException;
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

import com.fasterxml.jackson.annotation.JsonView;

import br.edu.cruzeirodosul.domain.dto.QuadroAggregateDTO;
import br.edu.cruzeirodosul.domain.entities.Quadro;
import br.edu.cruzeirodosul.domain.entities.QuadroCurso;
import br.edu.cruzeirodosul.domain.entities.QuadroGrupo;
import br.edu.cruzeirodosul.domain.entities.QuadroGrupoTipoComprovante;
import br.edu.cruzeirodosul.domain.entities.view.Views;
import br.edu.cruzeirodosul.domain.enums.ExibeConstraints;
import br.edu.cruzeirodosul.persistence.QuadroCursoRepository;
import br.edu.cruzeirodosul.persistence.QuadroGrupoRepository;
import br.edu.cruzeirodosul.persistence.QuadroGrupoTipoComprovanteRepository;
import br.edu.cruzeirodosul.persistence.QuadroRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(QuadroController.URL)
@Api(value = "Quadro", tags = { "Quadro" })
public class QuadroController {

    public static final String URL = "/quadro";

    public static final int DURATION_TIME = 4;
    
    private final QuadroRepository quadroRepository;   
    private final QuadroGrupoRepository quadroGrupoRepository;   
    private final QuadroCursoRepository quadroCursoRepository;
    private final QuadroGrupoTipoComprovanteRepository quadroGrupoTipoComprovanteRepository;

    @ApiOperation(value = "Persiste Quadro", response = Quadro.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201,
                    message = "A requisição foi bem sucedida e um novo recurso foi criado como resultado"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado") })
    @ResponseStatus(HttpStatus.CREATED)
    @JsonView({Views.QuadroView.class})
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Quadro> save( @JsonView(Views.QuadroView.class) @Valid @RequestBody final Quadro quadro) {
        log.debug("Salvando Quadro");

        final Quadro result =
                quadroRepository.save(Quadro.builder().nome(quadro.getNome()).situacao(quadro.getSituacao())
                        .quadroCursos(quadro.getQuadroCursos()).quadroGrupos(quadro.getQuadroGrupos())
                        .versaoAnterior(quadro.getVersaoAnterior()).build());

        return ResponseEntity.created(URI.create(URL + "/" + result.getId())).body(result);
    }

    @ApiOperation(hidden = true, value = "Busca Quadro por Prefixo")
    @JsonView({Views.QuadroView.class})
    @GetMapping(params = { "nome" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Quadro>> findByPrefix(@RequestParam(value = "nome") String nome) {

        log.debug("Buscando Quadro por Prefixo");

        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME, ChronoUnit.HOURS)))
                .body(quadroRepository.findByPrefix(nome));
    }
    
    @ApiOperation(hidden = true, value = "Busca Quadro por idCurso")
    @GetMapping(params = { "idCurso" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Quadro>> findByCurso(@RequestParam(value = "idCurso") Long idCurso) {

        log.debug("Buscando Quadro por idCurso");

        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME, ChronoUnit.HOURS)))
                .body(quadroRepository.findByCurso(idCurso));
    }
      
    @ApiOperation(hidden = true, value = "Busca Quadro por idCurso e Status do quadro = ATIVO")    
    @GetMapping( value = "/contador",params = { "idCurso"  }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuadroAggregateDTO> countQuadroByIdCursoAndStatus(@RequestParam(value = "idCurso") Long idCurso) {

	    log.debug("Buscando Quadro por idCurso e Status do quadro Ativo");
	    
	    return quadroRepository.countQuadroByIdCursoAndStatus(idCurso).map(ResponseEntity::ok)
	    		.orElse(ResponseEntity.noContent().build());
    
    }
   
    @ApiOperation(hidden = true, value = "Busca Quadro por idGrupo")
    @JsonView({Views.QuadroView.class})
    @GetMapping(params = { "idGrupo" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Quadro>> findByGrupo(@RequestParam(value = "idGrupo") Long idGrupo) {

        log.debug("Buscando Quadros por idGrupo");

        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME, ChronoUnit.HOURS)))
                .body(quadroRepository.findByGrupo(idGrupo));
    }

    @ApiOperation(hidden = true, value = "Busca Quadro por idTipoComprovante")
    @JsonView({Views.QuadroView.class})
    @GetMapping(params = { "idTipoComprovante" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Quadro>> findByTipoComprovante(
            @RequestParam(value = "idTipoComprovante") Long idTipoComprovante) {

        log.debug("Buscando Quadros por idTipoComprovante");

        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME, ChronoUnit.HOURS)))
                .body(quadroRepository.findByTipoComprovante(idTipoComprovante));
    }
    
    @ApiOperation(hidden = true, value = "Busca Quadro por idQuadroGrupoTipoComprovante")
    @JsonView({Views.QuadroView.class})
    @GetMapping(params = { "idQuadroGrupoTipoComprovante" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Quadro>> findByQuadroGrupoTipoComprovante(
            @RequestParam(value = "idQuadroGrupoTipoComprovante") Long idQuadroGrupoTipoComprovante) {

        log.debug("Buscando Quadros por idQuadroGrupoTipoComprovante {}", idQuadroGrupoTipoComprovante);

        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME, ChronoUnit.HOURS)))
                .body(quadroRepository.findByQuadroGrupoTipoComprovante(idQuadroGrupoTipoComprovante));
    }

    @ApiOperation(hidden = true, value = "Busca Quadro por idGrupo e idTipoComprovante")
    @JsonView({Views.QuadroView.class})
    @GetMapping(params = { "idGrupo", "idTipoComprovante" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Quadro>> findByGrupoAndTipoComprovante(@RequestParam(value = "idGrupo") Long idGrupo,
            @RequestParam(value = "idTipoComprovante") Long idTipoComprovante) {
        log.debug("Buscando Quadro por idGrupo e idTipoComprovante");

        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME, ChronoUnit.HOURS)))
                .body(quadroRepository.findByGrupoAndTipoComprovante(idGrupo, idTipoComprovante));
    }

    @ApiOperation(value = "Busca Quadro por id", response = Quadro.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),            
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado")
    })
    @JsonView({Views.QuadroView.class})
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Quadro> findById(@PathVariable Long id) {
        log.debug("Buscando Quadro por Id");                           	   
    	
    	return quadroRepository.findById(id).map(q -> ResponseEntity.ok()
    			.cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME, ChronoUnit.HOURS))).body(q))
    			.orElseThrow(() -> new EmptyResultDataAccessException(0));    	               
    }
    
    @ApiOperation(hidden = true, value = "Busca Quadro ativo por idCurso")
    @GetMapping(value = "/quadro-curso/{idCurso}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Quadro> findByIdCurso(@PathVariable Long idCurso) {

    	log.debug("Buscando Quadro por idCurso");
    	
    	Quadro result = quadroRepository.findByCurso(idCurso).stream()
    			.findAny().orElse(null);
    	
    	if (result == null) {
    		return ResponseEntity.noContent().build();
    	}

        return ResponseEntity.ok(result);        		
    }

    @ApiOperation(value = "Busca Quadros por nome ou idGrupo e/ou idTipoComprovante ou idQuadroGrupoTipoComprovante ou idCurso",
            response = Quadro.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 400, message = "Requisição incorreta."),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado") })
    @JsonView({Views.QuadroView.class})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Quadro>> findByCriterios(
            @ApiParam("ID do quadro. " +
                    "Se for informado executa busca de quadros ativos pelo id do quadro e " +
                    "não pode ser informado em conjunto com outros argumentos.")
            @RequestParam(value = "idQuadro", required = false)
                    Long idQuadro,
            @ApiParam("Prefixo do nome do quadro. " +
                    "Se for informado executa busca de quadros ativos por prefixo de nome e os " +
                    "demais parametros não podem ser preenchidos.")
            @RequestParam(value = "nome", required = false)
                    String nome,
            @ApiParam("ID do grupo. " +
                    "Só pode ser preenchido para busca de quadros ativos por grupo e para busca de quadro " +
                    "ativos por grupo e tipo comprovante (se informado ID do " +
                    "tipo comprovante (idTipoComprovante).")
            @RequestParam(value = "idGrupo", required = false)
                    Long idGrupo,
            @ApiParam("ID do tipo comprovante. " +
                    "Só pode ser preenchido para busca de quadros ativos por tipo comprovante e " +
                    "para busca de quadro ativos por grupo e tipo comprovante " +
                    "(se informado ID do grupo (idgrupo).")
            @RequestParam(value = "idTipoComprovante", required = false)
                    Long idTipoComprovante,
            @ApiParam("ID do quadro grupo tipo comprovante. " +
                    "Só pode ser preenchido para busca de quadros ativos por quadro grupo tipo comprovante.")
            @RequestParam(value = "idQuadroGrupoTipoComprovante", required = false)
                    Long idQuadroGrupoTipoComprovante,
            @ApiParam("ID do curso. " +
                    "Se for informado executa busca de quadros ativos por curso e " +
                    "não pode ser informado em conjunto com outros argumentos.")
            @RequestParam(value = "idCurso", required = false)
                    Long idCurso)
    {

        log.debug("Método utilizado apenas para documentação Swagger/OpenAPI");

        return ResponseEntity.badRequest().build();
    }

    @ApiOperation(value = "Busca QuadroGrupo por QuadroID",
            response = QuadroGrupo.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 204, message = "Não há conteúdo para enviar para esta solicitação"),
            @ApiResponse(code = 400, message = "Requisição incorreta."),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido")})
    @JsonView({Views.QuadroView.class})
    @GetMapping(value = "/{id}/quadro-grupo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<QuadroGrupo>> findQuadroGrupoByQuadroId(
            @PathVariable("id") Long idQuadro) {

        log.debug("Buscando QuadroGrupo por idQuadro");
        
        List<QuadroGrupo> quadroGrupos = quadroGrupoRepository.findByQuadroId(idQuadro);
             
        if (quadroGrupos.isEmpty()) {
    		return ResponseEntity.noContent().build();    		
    	}                       

        return ResponseEntity.ok(quadroGrupos);
    }
    
    @ApiOperation(value = "Busca QuadroGrupos por id do Curso", response = QuadroGrupo.class,
            responseContainer = "List")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @JsonView({Views.QuadroView.class})
    @GetMapping(value = "/quadro-grupo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<QuadroGrupo>> findByQuadroGrupoByCursoId(
    		@ApiParam("id do curso pertencente ao quadro") @RequestParam(value = "idCurso", required = true) Long idCurso,
    		@ApiParam("exibe aluno") @RequestParam(required = false, defaultValue = "SIM") ExibeConstraints exibeAluno,
            @ApiParam("exibe secretaria") @RequestParam(required = false, defaultValue = "SIM") ExibeConstraints exibeSecretaria,
            @ApiParam("aproveitamento em trilhas") @RequestParam(required = false, defaultValue = "NAO") ExibeConstraints aproveitamentoTrilha){
    	
    	log.debug("Buscando QuadroGrupos por Id do Curso");
    	
    	return ResponseEntity.ok(quadroGrupoRepository.findByCursoId(idCurso,exibeAluno,exibeSecretaria,aproveitamentoTrilha));    			
    }

	@ApiOperation(value = "Busca QuadroCurso por QuadroID", response = QuadroCurso.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
			@ApiResponse(code = 204, message = "Não há conteúdo para enviar para esta solicitação"),
			@ApiResponse(code = 400, message = "Requisição incorreta."),
			@ApiResponse(code = 401, message = "Não autorizado para visualizar"),
			@ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido")
	})
	@JsonView({ Views.QuadroView.class })
	@GetMapping(value = "/{id}/quadro-curso", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<QuadroCurso>> findQuadroCursoByQuadroId(@PathVariable("id") Long idQuadro) {

		log.debug("Buscando QuadroGrupo por idQuadro");

		List<QuadroCurso> quadroCursos = quadroCursoRepository.findByQuadroId(idQuadro);

		if (quadroCursos.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(quadroCursos);				
	}
	
	@ApiOperation(value = "Busca QuadroCurso por id do curso", response = QuadroCurso.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
			@ApiResponse(code = 204, message = "Não há conteúdo para enviar para esta solicitação"),
			@ApiResponse(code = 400, message = "Requisição incorreta."),
			@ApiResponse(code = 401, message = "Não autorizado para visualizar"),
			@ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido")
	})	
	@GetMapping(value = "/quadro-curso", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<QuadroCurso>> findQuadroCursoByIdCurso(@RequestParam Long idCurso) {

		log.debug("Buscando QuadroGrupo por idCurso {}", idCurso);

		List<QuadroCurso> quadroCursos = quadroCursoRepository.findByIdCurso(idCurso);

		if (quadroCursos.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(quadroCursos);				
	}
	
	
	@ApiOperation(value = "Busca QuadroGrupoTipoComprovante por id", response = QuadroGrupoTipoComprovante.class)
    	@ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"), 
    		@ApiResponse(code = 204, message = "Não há conteúdo para enviar para esta solicitação"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado")
    })	    
	@JsonView({ Views.QuadroView.class })
    @GetMapping(value = "/quadro-grupo-tipo-comprovante/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuadroGrupoTipoComprovante> findQuadroGrupoTipoComprovanteById(@PathVariable Long id) {
        log.debug("Buscando QuadroGrupoTipoComprovante por id {}", id);                           	   
    	
    	return quadroGrupoTipoComprovanteRepository.findById(id)
    			.map(ResponseEntity::ok)
    			.orElse(ResponseEntity.noContent().build());    	               
    }

    @ApiOperation(value = "Atualiza Quadro", response = Quadro.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @JsonView({Views.QuadroView.class})
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)    
    public ResponseEntity<Quadro> update(@PathVariable(required = true) Long id, @JsonView(Views.QuadroView.class) @Valid @RequestBody Quadro quadro) {
        log.debug("Atualizando Quadro");           
        
        Quadro quadroUpdate = Quadro.builder()
        		.id(id)
        		.nome(quadro.getNome())
        		.situacao(quadro.getSituacao())
        		.quadroGrupos(quadro.getQuadroGrupos())
        		.quadroCursos(quadro.getQuadroCursos())
        		.versaoAnterior(quadro.getVersaoAnterior())
        		.build();                  
         
         return ResponseEntity.ok(quadroRepository.save(quadroUpdate));
    }


    @ApiOperation(value = "Deleta Quadro")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Não há conteúdo para enviar para esta solicitação"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido") })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{id}")
    public void deleteById(@PathVariable Long id) {
        log.debug("Deletando Quadro por id");
        quadroRepository.deleteById(id);
    }
}
