package br.edu.cruzeirodosul.controller;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.validation.Valid;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import br.edu.cruzeirodosul.config.PageableConfig;
import br.edu.cruzeirodosul.config.properties.PageableProperties;
import br.edu.cruzeirodosul.domain.dto.AlunoCursoComprovanteAggregateDTO;
import br.edu.cruzeirodosul.domain.dto.ComprovanteAtividadeComplementarInstituicaoPoloIdAluRgmDTO;
import br.edu.cruzeirodosul.domain.dto.ComprovanteAtividadeComplementarPoloDTO;
import br.edu.cruzeirodosul.domain.dto.ComprovanteAtividadeComplementarTrilhaAlunoDTO;
import br.edu.cruzeirodosul.domain.dto.ComprovanteAtividadeComplementarUsuarioDTO;
import br.edu.cruzeirodosul.domain.entities.ComprovanteAtividadeComplementar;
import br.edu.cruzeirodosul.domain.entities.page.ComprovantePage;
import br.edu.cruzeirodosul.domain.entities.view.Views;
import br.edu.cruzeirodosul.domain.enums.AlgoritmoDigest;
import br.edu.cruzeirodosul.domain.enums.ExibeConstraints;
import br.edu.cruzeirodosul.domain.enums.StatusComprovanteConstraint;
import br.edu.cruzeirodosul.persistence.ComprovanteAtividadeComplementarRepository;
import br.edu.cruzeirodosul.persistence.ComprovanteAtividadeComplementarTrilhaRepository;
import br.edu.cruzeirodosul.util.DateUtil;
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
@RequestMapping(ComprovanteAtividadeComplementarController.URL)
@Api(value = "Comprovante Atividades Complementares", tags = { "Comprovante Atividades Complementares" })
public class ComprovanteAtividadeComplementarController {

    public static final String URL = "/comprovante";

    public static final int DURATION_TIME = 4;  
    
    public static final String ALL_USERS= "allUsers";

    private final ComprovanteAtividadeComplementarRepository repository;
  	private final ComprovanteAtividadeComplementarTrilhaRepository trilhaRepository;

    private final EntityManager em;           

    private final PageableProperties pageable;              

    @Transactional
    @ApiOperation(value = "Persiste Comprovante Atividades Complementares",
            response = ComprovanteAtividadeComplementar.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201,
                    message = "A requisição foi bem sucedida e um novo recurso foi criado como resultado"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado") })
    @ResponseStatus(HttpStatus.CREATED)
    @JsonView(Views.ComprovanteAtividadeComplementarView.class)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComprovanteAtividadeComplementar> save(
            @JsonView(Views.ComprovanteAtividadeComplementarView.class) 
            @Valid @RequestBody final ComprovanteAtividadeComplementar comprovanteAtividadeComplementar) {

        log.debug("Salvando Comprovante Atividades Complementares");

        ComprovanteAtividadeComplementar result = repository.save(comprovanteAtividadeComplementar);
        
        em.flush();
		em.clear();
		
		result = repository.getOne(result.getId());

        return ResponseEntity.created(URI.create(URL + "/" + result.getId())).body(result);
    }

    @ApiOperation(value = "Busca Comprovante por id", response = ComprovanteAtividadeComplementar.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),            
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado")
    })
    @JsonView(Views.ComprovanteAtividadeComplementarView.class)
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)    
    public ResponseEntity<ComprovanteAtividadeComplementar> findById(@PathVariable Long id) {
        log.debug("Buscando Comprovante por id");                           	   
    	
    	return repository.findById(id).map(q -> ResponseEntity.ok()
    			.cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME, ChronoUnit.HOURS))).body(q))
    			.orElseThrow(() -> new EmptyResultDataAccessException(0));    	               
    }      

    @ApiOperation(
            value = "Consulta total de horas e quantidade de comprovantes postados do aluno por tipo de comprovante",
            response = AlunoCursoComprovanteAggregateDTO.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 204, message = "Não há conteúdo para enviar para esta solicitação"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @GetMapping(value = "/total-horas-aluno-tipo-comprovante", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AlunoCursoComprovanteAggregateDTO> sumHorasAndQuantidadePostadasAlunoTipoComprovante(
            @ApiParam("id RGM do aluno") @RequestParam Long idAlunoRgm,
            @ApiParam("id do curso") @RequestParam Long idCurso,
            @ApiParam("id do tipo comprovante") @RequestParam Long idTipoComprovante) {

        log.debug("Busca total de horas e quantidade de comprovantes postado por idAlunoRgm {}"
                + ", idCurso {} e idTipoComprovante {}", idAlunoRgm, idCurso, idTipoComprovante);

        return repository
                .sumHorasAndQuantidadeByIdAlunoRgmAndIdCursoAndTipoComprovanteId(idAlunoRgm, idCurso, idTipoComprovante)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
    
    @ApiOperation(
            value = "Consulta total de horas e quantidade de comprovantes/temas transversais/trilhas que foram "
            		+ "postadas e aprovadas do aluno: "
            		+ "quando informado apenas idAlunoRgm e idCurso irá retornar o total em comprovantes, "
            		+ "quando informado idAlunoRgm, idCurso, exibeAluno=NAO e exibeSecretaria=NAO irá retornar "
            		+ "o total em Temas Tranversais, "
            		+ "quando informado idAlunoRgm, idCurso, exibeAluno=NAO, exibeSecretaria=NAO e "
            		+ "aproveitamentoTrilha=SIM irá retornar o total em Trilhas",
            response = AlunoCursoComprovanteAggregateDTO.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 204, message = "Não há conteúdo para enviar para esta solicitação"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @GetMapping(value = "/total-horas-realizadas-aluno", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AlunoCursoComprovanteAggregateDTO> sumHorasAndQuantidadeWithStatusAprovado(
            @ApiParam("id RGM do aluno") @RequestParam Long idAlunoRgm,
            @ApiParam("id do curso") @RequestParam Long idCurso,
            @ApiParam("exibe aluno") @RequestParam(required = false, defaultValue = "SIM") ExibeConstraints exibeAluno,
            @ApiParam("exibe secretaria") @RequestParam(required = false, defaultValue = "SIM") ExibeConstraints exibeSecretaria,
            @ApiParam("aproveitamento em trilhas") @RequestParam(required = false, defaultValue = "NAO") ExibeConstraints aproveitamentoTrilha) {

        log.debug("Busca total de horas e quantidade de comprovantes postadas e aprovadas por"
        		+ " idAlunoRgm {}, idCurso {}, exibeAluno {} e exibeSecretaria {}, aproveitamentoTrilha {}", 
        		idAlunoRgm, idCurso, exibeAluno, exibeSecretaria, aproveitamentoTrilha);

        return repository
                .sumHorasAndQuantidadeByIdAlunoRgmAndIdCursoAndExibeAlunoAndExibeSecretariaWithStatusAprovado(
                		idAlunoRgm, idCurso, exibeAluno, exibeSecretaria, aproveitamentoTrilha)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
        
    @ApiOperation(value = "Busca comprovante com paginação por critérios, sendo possível efetuar diversas combinações de filtros",
            response = ComprovantePage.class, responseContainer = "List")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 400, message = "Requisição incorreta."),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado") })
    @JsonView(Views.ComprovanteAtividadeComplementarView.class)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComprovantePage> findByCriterios(
    		@ApiParam("Id do Rgm do aluno. Irá executar a busca filtrando por id do aluno RGM. Possíveis combinações:\n\n"                        
            + "| idAlunoRgm, idCurso, digestArquivo (opcional) e tipoDigestArquivo |\n\n"
            + "| idAlunoRgm e idCurso e status (opcional) |\n\n"
            + "| idAlunoRgm e statusComprovante |\n\n"
            + "| idAlunoRgm, idCurso e StatusComprovante |")            
    		@RequestParam(value = "idAlunoRgm", required = false) Long idAlunoRgm,
    		
            @ApiParam("Id do curso. Irá executar a busca filtrando por id do curso. Possíveis combinações:\n\n"
            		+ "| idAlunoRgm, idCurso, digestArquivo (opcional) e tipoDigestArquivo |\n\n"
            		+ "| idAlunoRgm e idCurso e status (opcional) |\n\n"
            		+ "| idAlunoRgm, idCurso e statusComprovante |\n\n"
            		+ "| idPolo, idCurso, DataPostagemInicial, dataPostagemFinal e statusComprovante |\n\n"
            		+ "| idCurso, dataPostagemInicial, dataPostagemFinal e statusComprovante |\n\n"
            		+ "| idCurso, idTurma e statusComprovante |\n\n"
            		+ "| idCurso, idTurma, DataPostagemIncial, dataPostagemFinal e statusComprovante |\n\n"
            		+ "| codigoInstituicao, idCurso, e statusComprovante |") 
    		@RequestParam(value = "idCurso", required = false) Long idCurso,
    		
            @ApiParam("Id do polo. Irá executar a busca filtrando por id do polo. Possíveis combinações:\n\n"            		
            		+ "| idPolo, dataPostagemInicial, dataPostagemFinal e statusComprovante |\n\n"
            		+ "| idPolo, idCurso, dataPostagemInicial, dataPostagemFinal e statusComprovante |")    		
    		@RequestParam(value = "idPolo", required = false) Long idPolo,
            
            @ApiParam("Id da turma. Irá executar a busca filtrando por id da turma. Possíveis combinações:\n\n"
            		+ "| idCurso, idTurma e statusComprovante |\n\n"  
            		+ "| idCurso, idTurma, dataPostagemInicial, dataPostagemFinal e statusComprovante |")    		
    		@RequestParam(value = "idTurma", required = false) Long[] idTurma,
            
            @ApiParam("Data inicial de postagem (yyyy-MM-dd). Irá executar a busca filtrando por data de criação. Possíveis combinações:\n\n"
            		+ "| idPolo, dataPostagemInicial, dataPostagemFinal e statusComprovante |\n\n"
            		+ "| idPolo, idCurso, dataPostagemInicial, dataPostagemFinal e statusComprovante |\n\n"
            		+ "| idCurso, dataPostagemInicial, dataPostagemFinal e statusComprovante |\n\n"
            		+ "| idCurso, idTurma, dataPostagemInicial, dataPostagemFinal e statusComprovante |")    		
    		@RequestParam(value = "dataPostagemInicial", required = false) String dataPostagemInicial,
    		
            @ApiParam("Data final de postagem (yyyy-MM-dd). Irá executar a busca filtrando por data de criação. Possíveis combinações:\n\n"
            		+ "| idPolo, dataPostagemInicial, dataPostagemFinal e statusComprovante |\n\n"
            		+ "| idPolo, idCurso, dataPostagemInicial, dataPostagemFinal e statusComprovante |\n\n"
            		+ "| idCurso, dataPostagemInicial, dataPostagemFinal e statusComprovante |\n\n"
            		+ "| idCurso, idTurma, dataPostagemInicial, dataPostagemFinal e statusComprovante |")
    		@RequestParam(value = "dataPostagemFinal", required = false) Date dataPostagemFinal,
    		
            @ApiParam("Status do comprovante. Irá executar a busca filtrando por status. Possíveis combinações:\n\n"
            		+ "| statusComprovante |\n\n"
            		+ "| idAlunoRgm e statusComprovante |\n\n"
            		+ "| idAlunoRgm, idCurso e statusComprovante |\n\n"
            		+ "| idPolo, dataPostagemInicial, dataPostagemFinal e statusComprovante |\n\n"
            		+ "| idPolo, idCurso, dataPostagemInicial, dataPostagemFinal e statusComprovante |\n\n"
            		+ "| idCurso, dataPostagemInicial, dataPostagemFinal e statusComprovante | \n\n"
            		+ "| idCurso, idTurma e statusComprovante |\n\n"
            		+ "| idCurso, idTurma, dataPostagemInicial, dataPostagemFinal e statusComprovante |\n\n"
            		+ "| codigoInstituicao, idCurso e statusComprovante |")    	
    		@RequestParam(value = "statusComprovante", required = false) StatusComprovanteConstraint[] statusComprovante,
            
            @ApiParam("Digest do arquivo. Irá executar a busca filtrando por digest do arquivo. Possíveis combinações:\n\n"
            		+ "| idAlunoRgm, idCurso, digestArquivo e tipoDigestArquivo (opcional) |")    		
    		@RequestParam(value = "digestArquivo", required = false) String digestArquivo,
    		
            @ApiParam("Tipo digest do arquivo. Irá executar a busca filtrando por tipo digest do arquivo, "
            		+ "quando não informado valor default será 'MD5'. Possíveis combinações:\n\n "
            		+ "| idAlunoRgm, idCurso, digestArquivo e tipoDigestArquivo (opcional) |")    		
            @RequestParam(value = "tipoDigestArquivo", required = false) AlgoritmoDigest tipoDigestArquivo,
    
		    @ApiParam("Código da instituição. Irá executar a busca filtrando por código da instituição. Possíveis combinações:\n\n"		    		
		    		+ "| codigoInstituicao, idCurso, e statusComprovante |") 
			@RequestParam(value = "codigoInstituicao", required = false) Long codigoInstituicao,
			
			@ApiParam("Página. Quando informado irá retornar a paginação com base na página informada, "
			 		+ "quando não informado o valor padrão será 1, não é válido para a consulta "
			 		+ "filtrando por idAlunoRgm e idCurso e digestArquivo e/ou tipoDigestArquivo:\n\n"		    		
			    		+ "| quantidade de itens retornados por página " + PageableProperties.MAX_PAGE_SIZE_DEFAULT + " |") 
			@RequestParam(value = "pagina", required = false) Integer pagina) {

        log.debug("Método utilizado apenas para documentação Swagger/OpenAPI");

        return ResponseEntity.badRequest().build();
    }

    @ApiOperation(hidden = true,
            value = "Busca Comprovante de Atividade Complementar por id RGM do aluno"
                    + ", id do curso, digest do arquivo e tipo digest do arquivo do comprovante",
            response = ComprovanteAtividadeComplementar.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 204, message = "Não há conteúdo para enviar para esta solicitação"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @JsonView(Views.ComprovanteAtividadeComplementarView.class)
    @GetMapping(params = { "idAlunoRgm", "idCurso", "digestArquivo" },
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ComprovanteAtividadeComplementar>> findByIdAlunoRgmAndIdCursoAndDigestArquivoAndTipoDigestArquivo(
            @RequestParam Long idAlunoRgm, @RequestParam Long idCurso, @RequestParam String digestArquivo,
            @RequestParam(required = false, defaultValue = "MD5") AlgoritmoDigest tipoDigestArquivo) {

        log.debug(
                "Busca comprovante atividade complementar por idAlunoRgm e idCurso e digestArquivo e tipoDigestArquivo");

        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME, ChronoUnit.HOURS)))
                .body(repository.findByIdAlunoRgmAndIdCursoAndDigestArquivoAndTipoDigestArquivo(idAlunoRgm, idCurso,
                        digestArquivo, tipoDigestArquivo));
    }

    @ApiOperation(hidden = true,
            value = "Busca Comprovante de Atividade Complementar por id RGM do aluno, id do curso e opcionalmente status",
            response = ComprovanteAtividadeComplementar.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 204, message = "Não há conteúdo para enviar para esta solicitação"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @JsonView(Views.ComprovanteAtividadeComplementarView.class)
    @GetMapping(params = { "idAlunoRgm", "idCurso" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComprovantePage> findByIdAlunoRgmAndIdCurso(
            @RequestParam(name = "idAlunoRgm")  Long idAlunoRgm, 
            @RequestParam(name = "idCurso") Long idCurso,
            @RequestParam(name = "status", required = false, defaultValue = "APROVADO,REPROVADO" ) StatusComprovanteConstraint[] status,
            @RequestParam(value = "pagina", required = false, defaultValue = "1") Integer pagina) {
    	
        log.debug("Busca comprovante atividade complementar por idAlunoRgm, idCurso e array de status comprovante");        
        
        ComprovantePage comprovantePage = new ComprovantePage(repository.findByIdAlunoRgmAndIdCursoAndStatusComprovante(
        		idAlunoRgm, idCurso, status, PageableConfig.sortedPageableByDataCriacaoDesc(pageable, pagina)));
                        
        return ResponseEntity.ok().body(comprovantePage);
    }           
    
    @ApiOperation(hidden = true, value = "Busca Comprovantes por status")
    @JsonView(Views.ComprovanteAtividadeComplementarView.class)
    @GetMapping(params = { "statusComprovante" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComprovantePage> findByStatusComprovante(
            @RequestParam(value = "statusComprovante") StatusComprovanteConstraint statusComprovante,
            @RequestParam(value = "pagina", required = false, defaultValue = "1") Integer pagina) {
        
        log.debug("Buscando Comprovante por status {}", statusComprovante);     
                 
        ComprovantePage comprovantePage = new ComprovantePage(repository.findByStatusComprovante(
        				statusComprovante, PageableConfig.sortedPageableByDataCriacao(pageable, pagina)));
                                                       
        return ResponseEntity.ok().body(comprovantePage);
    }
    
    @ApiOperation(hidden = true, value = "Busca Comprovantes por id RGM do aluno e status")
    @JsonView(Views.ComprovanteAtividadeComplementarView.class)
    @GetMapping(params = { "idAlunoRgm", "statusComprovante" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComprovantePage> findByIdAlunoRgmAndStatusComprovante(
            @RequestParam(value = "idAlunoRgm") Long idAlunoRgm, 
            @RequestParam(value = "statusComprovante") StatusComprovanteConstraint statusComprovante,
            @RequestParam(value = "pagina", required = false, defaultValue = "1") Integer pagina ) {
        
        log.debug("Buscando Comprovante por idAlunoRgm {} e status {}", idAlunoRgm, statusComprovante);
      
        ComprovantePage comprovantePage = new ComprovantePage(repository.findByIdAlunoRgmAndStatusComprovante(
        		idAlunoRgm,	statusComprovante, PageableConfig.sortedPageableByDataCriacao(pageable, pagina)));
        return ResponseEntity.ok().body(comprovantePage);

    }

    @ApiOperation(hidden = true, value = "Busca Comprovantes por id RGM do aluno, id do curso e status")
    @JsonView(Views.ComprovanteAtividadeComplementarView.class)
    @GetMapping(params = { "idAlunoRgm", "idCurso", "statusComprovante" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComprovantePage> findByIdAlunoRgmAndIdCursoAndStatusComprovante(
            @RequestParam(value = "idAlunoRgm") Long idAlunoRgm,
            @RequestParam(value = "idCurso")  Long idCurso,
            @RequestParam(value = "statusComprovante") StatusComprovanteConstraint statusComprovante,
            @RequestParam(value = "pagina", required = false, defaultValue = "1") Integer pagina) {
        
        log.debug("Buscando Comprovante por idAlunoRgm {}, idCurso {} e status {}", 
        		idAlunoRgm, idCurso, statusComprovante);
        
        StatusComprovanteConstraint[] status= { statusComprovante };
        
        ComprovantePage comprovantePage = new ComprovantePage(repository.findByIdAlunoRgmAndIdCursoAndStatusComprovante(
        		idAlunoRgm,	idCurso, status, PageableConfig.sortedPageableByDataCriacao(pageable, pagina)));
        return ResponseEntity.ok().body(comprovantePage);
    }
    
    @ApiOperation(hidden = true, value = "Busca Comprovantes por id do polo, data de postagem e status")
    @JsonView(Views.ComprovanteAtividadeComplementarView.class)
    @GetMapping(params = { "idPolo", "dataPostagemInicial", "dataPostagemFinal", "statusComprovante" },
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComprovantePage> findByIdPoloAndDataPostagemAndStatusComprovante(
            @RequestParam(value = "idPolo") Long idPolo,
            @RequestParam(value = "dataPostagemInicial") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataPostagemInicial,
            @RequestParam(value = "dataPostagemFinal")  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataPostagemFinal,
            @RequestParam(value = "statusComprovante") StatusComprovanteConstraint statusComprovante,
            @RequestParam(value = "pagina", required = false, defaultValue = "1") Integer pagina) {
        
        log.debug("Buscando Comprovante por idPolo {}, dataPostagemInicial {}, dataPostagemFinal {} e statusComprovante {}", 
        		idPolo, dataPostagemInicial, dataPostagemFinal, statusComprovante);

        ComprovantePage comprovantePage = new ComprovantePage(repository.findByIdPoloAndDataPostagemAndStatusComprovante(
        		idPolo, DateUtil.asLocalDateTimeMinTime(dataPostagemInicial), DateUtil.asLocalDateTimeMaxTime(dataPostagemFinal), 
        		statusComprovante, PageableConfig.sortedPageableByDataCriacao(pageable, pagina)));
        return ResponseEntity.ok().body(comprovantePage);

    }
    
    @ApiOperation(hidden = true, value = "Busca Comprovantes por id do polo, id do curso, data de postagem e status")
    @JsonView(Views.ComprovanteAtividadeComplementarView.class)
    @GetMapping(params = { "idPolo", "idCurso", "dataPostagemInicial", "dataPostagemFinal", "statusComprovante" },
            produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<ComprovantePage> findByIdPoloAndIdCursoAndDataPostagemAndStatusComprovante(
            @RequestParam(value = "idPolo") Long idPolo,
            @RequestParam(value = "idCurso") Long idCurso,
            @RequestParam(value = "dataPostagemInicial") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataPostagemInicial,
            @RequestParam(value = "dataPostagemFinal") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataPostagemFinal,
            @RequestParam(value = "statusComprovante") StatusComprovanteConstraint statusComprovante,
            @RequestParam(value = "pagina", required = false, defaultValue = "1") Integer pagina) {
        
        log.debug("Buscando Comprovante por idPolo {} , idCurso {}, dataPostagemInicial{}, dataPostagemFinal{} e statusComprovante{}",
        		idPolo, idCurso, dataPostagemInicial, dataPostagemFinal, statusComprovante);

         ComprovantePage comprovantePage = new ComprovantePage(repository.findByIdPoloAndIdCursoAndDataPostagemAndStatusComprovante(
        		idPolo,idCurso, DateUtil.asLocalDateTimeMinTime(dataPostagemInicial), DateUtil.asLocalDateTimeMaxTime(dataPostagemFinal), 
        		statusComprovante, PageableConfig.sortedPageableByDataCriacao(pageable, pagina)));
        return ResponseEntity.ok().body(comprovantePage);
    }

    @ApiOperation(hidden = true, value = "Busca Comprovantes por id do curso, data de postagem e status")
    @JsonView(Views.ComprovanteAtividadeComplementarView.class)
    @GetMapping(params = { "idCurso", "dataPostagemInicial", "dataPostagemFinal", "statusComprovante" },
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComprovantePage> findByIdCursoAndDataPostagemAndStatusComprovante(
            @RequestParam(value = "idCurso") Long idCurso,
            @RequestParam(value = "dataPostagemInicial") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataPostagemInicial,
            @RequestParam(value = "dataPostagemFinal") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataPostagemFinal,
            @RequestParam(value = "statusComprovante") StatusComprovanteConstraint statusComprovante,
            @RequestParam(value = "pagina", required = false, defaultValue = "1") Integer pagina) {
        
        log.debug("Buscando Comprovante por idCurso {}, dataPostagemInicial{}, dataPostagemFinal{} e statusComprovante{}", 
        		idCurso, dataPostagemInicial, dataPostagemFinal, statusComprovante);

         ComprovantePage comprovantePage = new ComprovantePage(repository.findByIdCursoAndDataPostagemAndStatusComprovante(
        		idCurso, DateUtil.asLocalDateTimeMinTime(dataPostagemInicial), DateUtil.asLocalDateTimeMaxTime(dataPostagemFinal), 
        		statusComprovante, PageableConfig.sortedPageableByDataCriacao(pageable, pagina)));
        return ResponseEntity.ok().body(comprovantePage);
    }
    
    @ApiOperation(hidden = true, value = "Busca Comprovantes por id do curso, id da turma, data de postagem e status")
    @JsonView(Views.ComprovanteAtividadeComplementarView.class)
    @GetMapping(params = { "idCurso", "idTurma", "dataPostagemInicial", "dataPostagemFinal", "statusComprovante" },
    produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<ComprovantePage> findByIdCursoAndIdTurmaAndDataPostagemAndStatusComprovante(
    							
            @RequestParam(value = "idCurso") Long idCurso,
            @RequestParam(value = "idTurma") Long[] idTurma,
            @RequestParam(value = "dataPostagemInicial")  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataPostagemInicial,
            @RequestParam(value = "dataPostagemFinal")  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataPostagemFinal,
            @RequestParam(value = "statusComprovante") StatusComprovanteConstraint statusComprovante,
            @RequestParam(value = "pagina", required = false, defaultValue = "1") Integer pagina){
        
        log.debug("Buscando Comprovante por  idCurso {}, idTurma {},dataPostagemInicial{}, dataPostagemFinal{} e statusComprovante{}"
        		,idCurso, idTurma,dataPostagemInicial, dataPostagemFinal, statusComprovante);

        ComprovantePage comprovantePage = new ComprovantePage(repository.findByIdCursoAndIdTurmaAndDataCriacaoAndStatusComprovante(
        		idCurso, idTurma, DateUtil.asLocalDateTimeMinTime(dataPostagemInicial), DateUtil.asLocalDateTimeMaxTime(dataPostagemFinal), 
        		statusComprovante, PageableConfig.sortedPageableByDataCriacao(pageable, pagina)));
        return ResponseEntity.ok().body(comprovantePage);
    }
    
    @ApiOperation(hidden = true, value = "Busca Comprovantes por id do curso, id da turma e status")
    @JsonView(Views.ComprovanteAtividadeComplementarView.class)
    @GetMapping(params = { "idCurso", "idTurma", "statusComprovante" },
    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComprovantePage> findByIdCursoAndIdTurmaAndStatusComprovantes(
            @RequestParam(value = "idCurso") Long idCurso,
            @RequestParam(value = "idTurma") Long[] idTurma,
            @RequestParam(value = "statusComprovante") StatusComprovanteConstraint statusComprovante,
            @RequestParam(value = "pagina", required = false, defaultValue = "1") Integer pagina) {
        
        log.debug("Buscando Comprovante por  idCurso {}, idTurma {} e statusComprovante{}"
        		,idCurso, idTurma, statusComprovante);
        											
       ComprovantePage comprovantePage = new ComprovantePage(repository.findByIdCursoAndIdTurmaAndStatusComprovante(
        		idCurso,idTurma, statusComprovante, PageableConfig.sortedPageableByDataCriacao(pageable, pagina)));
        return ResponseEntity.ok().body(comprovantePage);
    }
    
    @ApiOperation(hidden = true, value = "Busca Comprovantes por código da instituição, id do curso e status")
    @JsonView(Views.ComprovanteAtividadeComplementarView.class)
    @GetMapping(params = { "codigoInstituicao", "idCurso", "statusComprovante" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComprovantePage> findByCodigoInstituicaoAndIdCursoAndStatusComprovante(
            @RequestParam(value = "codigoInstituicao") Long codigoInstituicao,
            @RequestParam(value = "idCurso") Long idCurso,
            @RequestParam(value = "statusComprovante") StatusComprovanteConstraint[] statusComprovante,
            @RequestParam(value = "pagina", required = false, defaultValue = "1") Integer pagina) {

        log.debug("Buscando Comprovante por  codigoInstuicao {}, idCurso {} e statusComprovante {}", 
        		codigoInstituicao, idCurso, statusComprovante);
        											
        ComprovantePage comprovantePage = new ComprovantePage(repository.findByCodigoInstituicaoAndIdCursoAndStatusComprovante(
        		codigoInstituicao,	idCurso, statusComprovante, PageableConfig.sortedPageableByDataCriacao(pageable, pagina)));
        return ResponseEntity.ok().body(comprovantePage);
    }
    
    @ApiOperation(value = "Busca usuários através dos comprovantes de atividades complementares",
            response = ComprovanteAtividadeComplementarUsuarioDTO.class, responseContainer = "List")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),            
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @GetMapping(value = "/usuario", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ComprovanteAtividadeComplementarUsuarioDTO>> findComprovanteAtividadeComplementarUsuario() {
    	
    	log.debug("Buscando usuários nos comprovantes de atividades complementares");    	    
    	
    	return ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME, ChronoUnit.HOURS)))
    			.body(repository.findComprovanteAtividadeComplementarUsuario());    			
    	
    }
    
    @ApiOperation(hidden = true,
            value = "Busca Comprovante de Tema Transversal por id RGM do aluno, id do curso e usuário",
            response = ComprovanteAtividadeComplementar.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 204, message = "Não há conteúdo para enviar para esta solicitação"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @JsonView(Views.ComprovanteAtividadeComplementarView.class)
    @GetMapping(value = "/tema-transversal", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ComprovanteAtividadeComplementar>> findComprovanteTemaTransversalByIdAlunoRgmAndIdCursoAndUsuario(
            @RequestParam Long idAlunoRgm, @RequestParam Long idCurso, @RequestParam String usuario) {

    	usuario = usuario.trim();
    	
        log.debug("Busca comprovante de tema transversal por idAlunoRgm {}, idCurso {} e usuario {}",
        		idAlunoRgm, idCurso, usuario);

        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME, ChronoUnit.HOURS)))
                .body(repository.findComprovanteTemaTransversalByIdAlunoRgmAndIdCursoAndUsuario(idAlunoRgm, idCurso, usuario));
    }
       
    @ApiOperation(value = "Persiste status e usuario Comprovante Atividades Complementare")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "A requisição foi bem sucedida o recurso foi atualizado"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado") })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @JsonView(Views.ComprovanteAtividadeComplementarView.class)
    @PatchMapping(value = "/{id}/status-comprovante", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateStatusComprovanteAndUsuario(@PathVariable(required = true) final Long id,
            @JsonView(Views.ComprovanteAtividadeComplementarView.class) @RequestBody final ComprovanteAtividadeComplementar comprovante) {
        
        log.debug("Salvando status {} e usuario {} no Comprovante Atividades Complementares id = {}",
                comprovante.getStatusComprovante(), comprovante.getUsuario(), id);

        repository.updateStatusComprovanteAndUsuario(id, comprovante.getStatusComprovante(), comprovante.getUsuario());       
    }
    
    @ApiOperation(value = "Persiste idPolo Comprovante Atividades Complementares "
    		+ "(update parcial utilizando método HTTP PUT e não PATCH, pois é chamado pelo legado)", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "A requisição foi bem sucedida o recurso foi atualizado"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado") })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/polo/{idPoloOld}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateIdPoloComprovante(@PathVariable(required = true) final Long idPoloOld,
            @RequestBody @Valid final ComprovanteAtividadeComplementarPoloDTO polo) {
    	
    	log.debug("Atualizando idPolo {} para idPolo {} no Comprovante Atividades Complementares do idAlunoRgm {} "
        		+ "e idCurso {}", idPoloOld, polo.getIdPoloNew(), polo.getIdAlunoRgm(), polo.getIdCurso());

        repository.updateIdPoloAluno(polo.getIdPoloNew(), polo.getIdAlunoRgm(), polo.getIdCurso(), idPoloOld);  
    }
    
    @ApiOperation(value = "Persiste codigoInstituicao e idAlunoRgm Comprovante Atividades Complementares "
    		+ "(update parcial utilizando método HTTP PUT e não PATCH, pois é chamado pelo legado)", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "A requisição foi bem sucedida o recurso foi atualizado"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado") })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/idAlunoRgm/{idAluRgmOld}/codigoInstituicao/{codigoInstituicaoOld}", 
    produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateCodigoInstituicaoAndIdAlunoRgmComprovante(@PathVariable(required = true) 
    		final Long idAluRgmOld, @PathVariable(required = true) final Long codigoInstituicaoOld,     		
            @RequestBody @Valid final ComprovanteAtividadeComplementarInstituicaoPoloIdAluRgmDTO comprovante) {
    	
    	log.debug("Atualizando codigoInstituicao {} e idAlunoRgm {} para codigoInstituicao {},  idAlunoRgm {} "
    			+ "no Comprovante Atividades Complementares do idAlunoRgm {} e idCurso {}", 
    			codigoInstituicaoOld, idAluRgmOld, comprovante.getCodigoInstituicaoNew(), comprovante.getIdAlunoRgmNew(), 
    			idAluRgmOld, comprovante.getIdCurso());

        repository.updateCodigoInstituicaoAndIdAlunoRgm(comprovante.getCodigoInstituicaoNew(),
        		comprovante.getIdAlunoRgmNew(), idAluRgmOld, comprovante.getIdCurso(), codigoInstituicaoOld);  
    }
    
    @ApiOperation(value = "Persiste codigoInstituicao, idAlunoRgm e idPolo Comprovante Atividades Complementares "
    		+ "(update parcial utilizando método HTTP PUT e não PATCH, pois é chamado pelo legado)", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "A requisição foi bem sucedida o recurso foi atualizado"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado") })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/idAlunoRgm/{idAluRgmOld}/codigoInstituicao/{codigoInstituicaoOld}/idPolo/{idPoloOld}", 
    produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateCodigoInstituicaoAndIdAluRgmAndIdPoloComprovante(@PathVariable(required = true) 
    		final Long idAluRgmOld, @PathVariable(required = true) final Long codigoInstituicaoOld, 
    		@PathVariable(required = true) final Long idPoloOld, 
            @RequestBody @Valid final ComprovanteAtividadeComplementarInstituicaoPoloIdAluRgmDTO comprovante) {
    	
    	log.debug("Atualizando codigoInstituicao {}, idPolo {} e idAlunoRgm {} para codigoInstituicao {},  idPolo {}, idAlunoRgm {} "
    			+ "no Comprovante Atividades Complementares do idAlunoRgm {} e idCurso {}", 
    			codigoInstituicaoOld, idPoloOld, idAluRgmOld, comprovante.getCodigoInstituicaoNew(), comprovante.getIdPoloNew(), 
    			comprovante.getIdAlunoRgmNew(), idAluRgmOld, comprovante.getIdCurso());

        repository.updateCodigoInstituicaoAndIdPoloAndIdAlunoRgm(comprovante.getIdPoloNew(), comprovante.getCodigoInstituicaoNew(),
        		comprovante.getIdAlunoRgmNew(), idAluRgmOld, comprovante.getIdCurso(), idPoloOld, codigoInstituicaoOld);  
    }

    @Transactional
    @ApiOperation(value = "Atualiza Comprovante Atividade Complementar", response = ComprovanteAtividadeComplementar.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),            
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @JsonView(Views.ComprovanteAtividadeComplementarView.class)
    @PutMapping(value = { "/{id}" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComprovanteAtividadeComplementar> update(@PathVariable(required = true) Long id,
    		@JsonView(Views.ComprovanteAtividadeComplementarView.class) @Valid @RequestBody ComprovanteAtividadeComplementar comprovante) {

        log.debug("Atualizando Comprovante");
        
        repository.save(ComprovanteAtividadeComplementar.builder()
				.id(id).statusComprovante(comprovante.getStatusComprovante()).usuario(comprovante.getUsuario())
				.idAlunoRgm(comprovante.getIdAlunoRgm()).dataAtividade(comprovante.getDataAtividade())
				.cargaHoraria(comprovante.getCargaHoraria()).tipoDocumento(comprovante.getTipoDocumento())
				.idAnexo(comprovante.getIdAnexo()).tipoDigestArquivo(comprovante.getTipoDigestArquivo())	
				.digestArquivo(comprovante.getDigestArquivo()).descricao(comprovante.getDescricao())
			    .observacao(comprovante.getObservacao()).idCurso(comprovante.getIdCurso())
			    .justificativa(comprovante.getJustificativa()).tituloComprovante(comprovante.getTituloComprovante())
			    .quadroCurso(comprovante.getQuadroCurso()).idFmFilepath(comprovante.getIdFmFilepath()).seqAtiv(comprovante.getSeqAtiv())
			    .quadroGrupoTipoComprovante(comprovante.getQuadroGrupoTipoComprovante()).idPolo(comprovante.getIdPolo())
			    .idTurma(comprovante.getIdTurma()).dataAnalise(comprovante.getDataAnalise())
			    .codigoInstituicao(comprovante.getCodigoInstituicao()).idOferta(comprovante.getIdOferta()).build());
        
        em.flush();
        em.clear();
        return ResponseEntity.ok().body(repository.getOne(id));
    }
   
  	
  	@ApiOperation(value = "Busca trilha por id do RGM do aluno e id do curso", 
  			response = ComprovanteAtividadeComplementarTrilhaAlunoDTO.class, responseContainer = "List")
  	@ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),			
  			@ApiResponse(code = 401, message = "Não autorizado para visualizar"),
  			@ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
  			@ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
  	@GetMapping(value = "/trilha",params = { "idAlunoRgm", "idCurso" }, produces = MediaType.APPLICATION_JSON_VALUE)
  	
  	public ResponseEntity<List<ComprovanteAtividadeComplementarTrilhaAlunoDTO>> findByIdAlunoRgmAndIdCurso(
  			@ApiParam("id do RGM do aluno") @RequestParam Long idAlunoRgm,
  			@ApiParam("id do curso") @RequestParam Long idCurso) {
  		
  		log.debug("Buscando Trilha por idAlunoRgm {} e idCurso {}", idAlunoRgm, idCurso);
  
  		return ResponseEntity.ok()
  				.cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME, ChronoUnit.HOURS)).cachePrivate())
  				.body(trilhaRepository.findByIdAlunoRgmAndIdCurso(idAlunoRgm, idCurso));	
  	}	
  
  }
