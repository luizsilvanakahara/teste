package br.edu.cruzeirodosul.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.cruzeirodosul.domain.dto.AnaliseComprovanteUsuarioAggregateDTO;
import br.edu.cruzeirodosul.domain.dto.AnaliseComprovanteUsuarioDTO;
import br.edu.cruzeirodosul.domain.dto.ComprovanteAtividadesAlunoDTO;
import br.edu.cruzeirodosul.domain.dto.InstituicaoComprovanteAggregateDTO;
import br.edu.cruzeirodosul.domain.enums.StatusComprovanteConstraint;
import br.edu.cruzeirodosul.persistence.ComprovanteAtividadeComplementarRelatorioRepository;
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
@RequestMapping(RelatorioController.URL)
@Api(value = "Relatorio Atividades Complementares", tags = { "Relatorio Atividades Complementares" })
public class RelatorioController {
	
	public static final String URL = "/relatorio";
	public static final String ALL_USERS= "allUsers";
    
    private final ComprovanteAtividadeComplementarRelatorioRepository comprovanteRelatorioRepository;
    
    @ApiOperation(value = "Consulta quantidade de comprovantes enviados por instituição, filtrando por período", 
    		response = InstituicaoComprovanteAggregateDTO.class, responseContainer = "List") 
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @GetMapping(value = "/quantidade-enviado-instituicao", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<InstituicaoComprovanteAggregateDTO>> countComprovanteEnviadoInstituicaoByDataCriacao(
    		@RequestParam(value = "dataPostagemInicial") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataPostagemInicial,
            @RequestParam(value = "dataPostagemFinal")  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataPostagemFinal) {
        
        log.debug("Consultando quantidade de comprovantes enviados por instituição filtrando por dataPostagemInicial {},"
        		+ " dataPostagemFinal {}", dataPostagemInicial, dataPostagemFinal);
        											
        return ResponseEntity.ok().body(comprovanteRelatorioRepository.countComprovanteEnviadoInstituicaoByDataCriacao(
        		DateUtil.asLocalDateTimeMinTime(dataPostagemInicial), DateUtil.asLocalDateTimeMaxTime(dataPostagemFinal)));
    }
    
    @ApiOperation(value = "Retorna relação de atividades do aluno com a informação de "
    		+ "situação de temas transversais original (não considera se TT é Opcional)", 
    		response = ComprovanteAtividadesAlunoDTO.Relatorio.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @GetMapping(value = "/atividades-aluno", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComprovanteAtividadesAlunoDTO.Relatorio> findComprovanteAtividadesDoAlunoByIdAlunoRgmAndIdCursoAndStatusComprovanteIn(
    		@RequestParam Long idAlunoRgm, @RequestParam Long idCurso,
            @RequestParam StatusComprovanteConstraint[] statusComprovante) {
        
        log.debug("Consultando relação de atividades do aluno por idAlunoRgm {},"
        		+ " idCurso {} e statusComprovante {}", idAlunoRgm, idCurso, statusComprovante);
        
        return ResponseEntity.ok(ComprovanteAtividadesAlunoDTO.gerar(comprovanteRelatorioRepository
        		.findComprovanteAtividadesAlunoByIdAlunoRgmAndIdCursoAndStatusComprovanteIn(idAlunoRgm, idCurso, statusComprovante)));              
     
    }
    
    @ApiOperation(value = "Consulta quantidade de comprovantes analisados por usuário, filtrando por período"
    		+ " e array de status do comprovante", response = AnaliseComprovanteUsuarioAggregateDTO.class, responseContainer = "List") 
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @GetMapping(value = "/quantidade-analisado-usuario", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AnaliseComprovanteUsuarioAggregateDTO>> countComprovanteAnalisadoUsuarioByDataAnaliseBetweenAndStatusComprovanteIn(
    		@RequestParam(value = "dataAnaliseInicial") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataAnaliseInicial,
            @RequestParam(value = "dataAnaliseFinal")  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataAnaliseFinal,
            @RequestParam StatusComprovanteConstraint[] statusComprovante) {
        
        log.debug("Consultando quantidade de comprovantes analisados por usuario filtrando por dataAnaliseInicial {},"
        		+ " dataAnaliseFinal {} e array de status {}", dataAnaliseInicial, dataAnaliseFinal, statusComprovante);
        											
        return ResponseEntity.ok().body(comprovanteRelatorioRepository.countComprovanteAnalisadoUsuarioByDataAnaliseBetweenAndStatusComprovanteIn(
        		DateUtil.asLocalDateTimeMinTime(dataAnaliseInicial), DateUtil.asLocalDateTimeMaxTime(dataAnaliseFinal), statusComprovante));
        
    }
    
    @ApiOperation(value = "Consulta relação de comprovantes de alunos avaliados por usuario, filtrando por período"
    		+ " e array de status do comprovante", response = AnaliseComprovanteUsuarioDTO.class, responseContainer = "List") 
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @GetMapping(value = "/atividades-analisadas-usuario", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AnaliseComprovanteUsuarioDTO>> findComprovantesAnalisadosUsuarioByDataAnaliseBetweenAndStatusComprovanteIn(
    		@RequestParam(value = "dataAnaliseInicial") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataAnaliseInicial,
            @RequestParam(value = "dataAnaliseFinal")  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataAnaliseFinal,
         	@RequestParam(value="statusComprovante") StatusComprovanteConstraint[] statusComprovante,
         	@RequestParam(value = "usuario", required = false,  defaultValue = ALL_USERS) String usuario)      	{
        
        log.debug("Consultando quantidade de comprovantes analisados por usuario filtrando por dataAnaliseInicial {},"
        		+ " dataAnaliseFinal {}, usuario {} e array de status {}", dataAnaliseInicial, dataAnaliseFinal, usuario, statusComprovante);
        
        usuario=usuario.toLowerCase();
                       											
        return ResponseEntity.ok().body(comprovanteRelatorioRepository.findComprovantesAnalisadoComprovanteAtividadeComplementarsUsuarioByDataAnaliseBetweenAndStatusComprovanteIn(
        		DateUtil.asLocalDateTimeMinTime(dataAnaliseInicial), DateUtil.asLocalDateTimeMaxTime(dataAnaliseFinal), statusComprovante, usuario));
        
    }

}
