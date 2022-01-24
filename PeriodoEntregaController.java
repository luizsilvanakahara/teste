package br.edu.cruzeirodosul.controller;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import br.edu.cruzeirodosul.domain.entities.PeriodoEntrega;
import br.edu.cruzeirodosul.domain.entities.PeriodoEntregaCurso;
import br.edu.cruzeirodosul.domain.entities.PeriodoEntregaEmpresa;
import br.edu.cruzeirodosul.domain.entities.PeriodoEntregaPolo;
import br.edu.cruzeirodosul.domain.entities.PeriodoEntregaTurma;
import br.edu.cruzeirodosul.domain.entities.view.Views.PeriodoEntregaView;
import br.edu.cruzeirodosul.persistence.PeriodoEntregaRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(PeriodoEntregaController.URL)
@Api(value = "PeriodoEntrega", tags = { "PeriodoEntrega" })
public class PeriodoEntregaController {

    private static final Logger logger = LoggerFactory.getLogger(PeriodoEntregaController.class);

    public static final String URL = "/periodo-entrega";

    public static final int DURATION_TIME = 4;

    @Autowired
    private PeriodoEntregaRepository repository;
    
    @ApiOperation(value = "Persiste Período Entrega", response = PeriodoEntrega.class)
	@ApiResponses(value = {
			@ApiResponse(code = 201,
					message = "A requisição foi bem sucedida e um novo recurso foi criado como resultado"), 
			@ApiResponse(code = 401, message = "Não autorizado para visualizar"),
			@ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
			@ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado") })
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PeriodoEntrega> save(
			@ApiParam("Existem quatro maneiras de configurar o Período de Entrega e para cada uma deve ser acrescentando ao body o "
					+ "identificador único da classe e o discriminator (valor fixo) conforme mencionado abaixo: \n\n"
					+ "Quando configuração por Empresa deve-se acrescentar \"codigoEmpresa\": {codigoEmpresa} e \"discriminator\": 10 \n\n"
					+ "Quando configuração por Polo deve-se acrescentar \"idPolo\": {idPolo} e \"discriminator\": 20 \n\n"
					+ "Quando configuração por Curso deve-se acrescentar \"idCurso\": {idCurso} e \"discriminator\": 30 \n\n"
					+ "Quando configuração por Turma deve-se acrescentar \"idTurma\": {idTurma} e \"discriminator\": 40 \n")
			@Valid @RequestBody final PeriodoEntrega periodoEntrega) {
		logger.debug("Salvando Período Entrega");
		final PeriodoEntrega result = repository.save(periodoEntrega);
		return ResponseEntity.created(URI.create(URL + "/" + result.getId())).body(result);
	}
    
    @ApiOperation(value = "Busca Período Entrega por id", response = PeriodoEntrega.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),            
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado")
    })    
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PeriodoEntrega> findById(@PathVariable Long id) {
        logger.debug("Buscando Período Entrega por Id");                           	   
    	
    	return repository.findById(id).map(q -> ResponseEntity.ok()
    			.cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME, ChronoUnit.HOURS))).body(q))
    			.orElseThrow(() -> new EmptyResultDataAccessException(0));    	               
    }     
    
    @ApiOperation(hidden = true, value = "Busca PeriodoEntrega por IdTurma ou IdCurso ou IdPolo ou CodigoEmpresa", response = PeriodoEntrega.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })	
    @GetMapping(params= {"idTurma","idCurso","codigoEmpresa"},produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PeriodoEntrega>> findByIdTurmaOrIdCursoOrIdPoloOrCodigoEmpresa(
    		@RequestParam Long idTurma, @RequestParam Long idCurso,
    		@RequestParam(required = false) Long idPolo,@RequestParam Long codigoEmpresa) {
        logger.debug("Encontrando PeriodoEntrega por id");

        return ResponseEntity.ok()
        		.cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME,ChronoUnit.HOURS)))
                .body(repository.findByIdTurmaOrIdCursoOrIdPoloOrCodigoEmpresa(idTurma, idCurso, idPolo, codigoEmpresa));	        
     }
   
    @ApiOperation(value = "Busca periodo por critérios, sendo possível efetuar diversas combinações de filtros",
            response = PeriodoEntrega.class, responseContainer = "List")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 400, message = "Requisição incorreta."),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você estava tentando acessar não foi encontrado") })
    @JsonView(PeriodoEntregaView.class)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PeriodoEntrega>> findByCriterios(
    		
    		 @ApiParam("Id do curso. Irá executar a busca filtrando por id do curso. Possíveis combinações:\n\n"
           		+ "| idCurso, idAno, semestre |") 
     		@RequestParam(value = "idCurso", required = false) Long idCurso,
     		
             @ApiParam("Id do polo. Irá executar a busca filtrando por id do polo. Possíveis combinações:\n\n"            		
             		+  "| idPolo, ano, semestre |")    		
     		@RequestParam(value = "idPolo",required = false) Long idPolo,
             
             @ApiParam("Id da turma. Irá executar a busca filtrando por id da turma. Possíveis combinações:\n\n"
            		 +  "| idTurma, ano, semestre |")     		
     		@RequestParam(value = "idTurma", required = false) Long idTurma,
    	    		
            @ApiParam("codigo da Empresa. Irá executar a busca filtrando por codigo do empresa. Possíveis combinações:\n\n"
                		+ "| codEmpresa, ano, semestre |") 
    		@RequestParam(value = "codigoEmpresa", required = false) Long codigoEmpresa,
    		@RequestParam(value = "ano", required = false) Long ano,
    		@RequestParam(value = "semestre", required = false) Long semestre
    		
    		) {

        logger.debug("Método utilizado apenas para documentação Swagger/OpenAPI");

        return ResponseEntity.badRequest().build();
    }
             
    @ApiOperation(hidden =true,value = "Busca PeriodoEntrega por IdTurma ", response = PeriodoEntregaTurma.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
    		@ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })	
    @GetMapping(params= {"idTurma","ano","semestre"},produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PeriodoEntregaTurma>> findByIdTurma(
    		@RequestParam Long idTurma, @RequestParam Long ano,@RequestParam Long semestre) {
        logger.debug("Encontrando PeriodoEntrega por idTurma");

        return ResponseEntity.ok()
        		.cacheControl(CacheControl.maxAge(Duration.of(DURATION_TIME,ChronoUnit.HOURS)))
                .body(repository.findByIdTurma(idTurma,ano,semestre));	
        
     }
    
    @ApiOperation(hidden =true,value = "Busca PeriodoEntrega por IdCurso ", response = PeriodoEntregaCurso.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })	
    @GetMapping(params= {"idCurso","ano","semestre"},produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PeriodoEntregaCurso>> findByIdCurso(@RequestParam Long idCurso,@RequestParam Long ano,@RequestParam Long semestre) {
        logger.debug("Encontrando PeriodoEntrega por idCurso");

        return ResponseEntity.ok()
                .body(repository.findByIdCurso(idCurso,ano,semestre));	
        
     }
    
    @ApiOperation(hidden =true, value = "Busca PeriodoEntrega por codigoEmpresa ", response = PeriodoEntregaEmpresa.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })	
    @GetMapping(params= {"codigoEmpresa","ano","semestre"},produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PeriodoEntregaEmpresa>> findByCodEmpresa(@RequestParam Long codigoEmpresa,@RequestParam Long ano,@RequestParam Long semestre) {
        logger.debug("Encontrando PeriodoEntrega por codigoEmpresa");

        return ResponseEntity.ok()	
                .body(repository.findByCodEmpresa(codigoEmpresa,ano,semestre));	
     }
    
    @ApiOperation(hidden =true,value = "Busca PeriodoEntrega por idPolo ", response = PeriodoEntregaPolo.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })	
    @GetMapping(params= {"idPolo","ano","semestre"},produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PeriodoEntregaPolo>> findByIdPolo(@RequestParam Long idPolo,@RequestParam Long ano,@RequestParam Long semestre) {
        logger.debug("Encontrando PeriodoEntrega por idPolo");

        return ResponseEntity.ok()
                .body(repository.findByIdPolo(idPolo,ano,semestre));	
        
     }
     
    @ApiOperation(value = "Atualiza PeriodoEntrega", response = PeriodoEntrega.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
			@ApiResponse(code = 401, message = "Não autorizado para visualizar"),
			@ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
			@ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
	@PutMapping(value = { "/{id}" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PeriodoEntrega> update(
			@ApiParam("Existem quatro maneiras de configurar o Período de Entrega e para cada uma deve ser acrescentando ao body o "
					+ "identificador único da classe e o discriminator (valor fixo) conforme mencionado abaixo: \n\n"
					+ "Quando configuração por Empresa deve-se acrescentar \"codigoEmpresa\": {codigoEmpresa} e \"discriminator\": 10 \n\n"
					+ "Quando configuração por Polo deve-se acrescentar \"idPolo\": {idPolo} e \"discriminator\": 20 \n\n"
					+ "Quando configuração por Curso deve-se acrescentar \"idCurso\": {idCurso} e \"discriminator\": 30 \n\n"
					+ "Quando configuração por Turma deve-se acrescentar \"idTurma\": {idTurma} e \"discriminator\": 40 \n")
			@PathVariable(required = true) Long id,
			@Valid @RequestBody PeriodoEntrega periodoEntrega) {
    	
    		logger.debug("Atualizando Periodo Entrega");  
    		
    		PeriodoEntrega result;
    	
    		if (periodoEntrega instanceof PeriodoEntregaTurma) {
    			
    			result = repository.save(PeriodoEntregaTurma.builder().id(id)
    					.mensagem(periodoEntrega.getMensagem())    					
    					.idTurma(((PeriodoEntregaTurma) periodoEntrega).getIdTurma())
    					.dataInicialCadastro(periodoEntrega.getPeriodoCadastro().getDataInicial())
    					.dataFinalCadastro(periodoEntrega.getPeriodoCadastro().getDataFinal())
    					.dataInicialAjuste(periodoEntrega.getPeriodoAjuste().getDataInicial())
    					.dataFinalAjuste(periodoEntrega.getPeriodoAjuste().getDataFinal())
    					.semestre(periodoEntrega.getSemestre())	
    					.ano(periodoEntrega.getAno()).build());
    			
    		} else if (periodoEntrega instanceof PeriodoEntregaCurso) {
    			
    			result = repository.save(PeriodoEntregaCurso.builder().id(id)
    					.mensagem(periodoEntrega.getMensagem())    					
    					.idCurso(((PeriodoEntregaCurso) periodoEntrega).getIdCurso())
    					.dataInicialCadastro(periodoEntrega.getPeriodoCadastro().getDataInicial())
    					.dataFinalCadastro(periodoEntrega.getPeriodoCadastro().getDataFinal())
    					.dataInicialAjuste(periodoEntrega.getPeriodoAjuste().getDataInicial())
    					.dataFinalAjuste(periodoEntrega.getPeriodoAjuste().getDataFinal())
    					.semestre(periodoEntrega.getSemestre())	
    					.ano(periodoEntrega.getAno()).build());
    			
    		} else if (periodoEntrega instanceof PeriodoEntregaPolo) {
    			
    			result = repository.save(PeriodoEntregaPolo.builder().id(id)
    					.mensagem(periodoEntrega.getMensagem())    					
    					.idPolo(((PeriodoEntregaPolo) periodoEntrega).getIdPolo())
    					.dataInicialCadastro(periodoEntrega.getPeriodoCadastro().getDataInicial())
    					.dataFinalCadastro(periodoEntrega.getPeriodoCadastro().getDataFinal())
    					.dataInicialAjuste(periodoEntrega.getPeriodoAjuste().getDataInicial())
    					.dataFinalAjuste(periodoEntrega.getPeriodoAjuste().getDataFinal())
    					.semestre(periodoEntrega.getSemestre())	
    					.ano(periodoEntrega.getAno()).build());
    			
    		} else {
    			
    			result = repository.save(PeriodoEntregaEmpresa.builder().id(id)
    					.mensagem(periodoEntrega.getMensagem())    					
    					.codigoEmpresa(((PeriodoEntregaEmpresa) periodoEntrega).getCodigoEmpresa())
    					.dataInicialCadastro(periodoEntrega.getPeriodoCadastro().getDataInicial())
    					.dataFinalCadastro(periodoEntrega.getPeriodoCadastro().getDataFinal())
    					.dataInicialAjuste(periodoEntrega.getPeriodoAjuste().getDataInicial())
    					.dataFinalAjuste(periodoEntrega.getPeriodoAjuste().getDataFinal())
    					.semestre(periodoEntrega.getSemestre())	
    					.ano(periodoEntrega.getAno()).build());
    			    			
    		}	
    		
    		return  ResponseEntity.ok(result);
    }

	@ApiOperation(value = "Deleta PeriodoEntrega")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Não há conteúdo para enviar para esta solicitação"),
			@ApiResponse(code = 401, message = "Não autorizado para visualizar"),
			@ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido") })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping(value = "/{id}")
	public void deleteById(@PathVariable Long id) {
		logger.debug("Deletando PeriodoEntrega por id");
		repository.deleteById(id);
	}
}
