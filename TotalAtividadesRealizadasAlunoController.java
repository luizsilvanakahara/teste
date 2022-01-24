package br.edu.cruzeirodosul.controller;

import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.cruzeirodosul.domain.dto.AlunoCursoAtividadeAggregateDTO;
import br.edu.cruzeirodosul.domain.entities.Dispensa;
import br.edu.cruzeirodosul.persistence.ComprovanteAtividadeComplementarRepository;
import br.edu.cruzeirodosul.persistence.DispensaRepository;
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
public class TotalAtividadesRealizadasAlunoController {

    private final ComprovanteAtividadeComplementarRepository comprovanteRepository;
    
    private final DispensaRepository dispensaRepository;                 

    @ApiOperation(
            value = "Consulta total de horas e quantidade aprovada por tipo de grupo de atividade "
            		+ "+ informação de dispensa do aluno com situação de temas transversais original (não considera se TT se é Opcional) ",            		
            response = AlunoCursoAtividadeAggregateDTO.TotalAtividadesRealizadas.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operação realizada com sucesso"),
    		@ApiResponse(code = 204, message = "A requisição foi bem sucedida, porém não encontrou conteúdo para exibir"),
            @ApiResponse(code = 401, message = "Não autorizado para visualizar"),
            @ApiResponse(code = 403, message = "Acessar o recurso que você estava tentando acessar é proibido"),
            @ApiResponse(code = 404, message = "O recurso que você esta tentando acessar não foi encontrado") })
    @GetMapping(value = "/total-atividades-realizadas-aluno", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AlunoCursoAtividadeAggregateDTO.TotalAtividadesRealizadas> sumHorasAndQuantidadeAtividadesWithDispensaWithStatusAprovado(
            @ApiParam("id RGM do aluno") @RequestParam Long idAlunoRgm,
            @ApiParam("id do curso") @RequestParam Long idCurso) {

        log.debug("Busca atividades realizadas do aluno por idAlunoRgm {} e idCurso {}", idAlunoRgm, idCurso);
        
        AlunoCursoAtividadeAggregateDTO.TotalAtividadesRealizadas result = null;
        
        Optional<AlunoCursoAtividadeAggregateDTO> atividadesRealizadas = comprovanteRepository
                .sumHorasAndQuantidadeAtividadesWithDispensaWithStatusAprovado(idAlunoRgm, idCurso);
        
        Dispensa dispensa = dispensaRepository.findByIdAlunoRgmAndIdCurso(idAlunoRgm, idCurso).stream().findFirst().orElse(null);
        
        if (atividadesRealizadas.isPresent()) {
        	
        	result = AlunoCursoAtividadeAggregateDTO.gerar(atividadesRealizadas.get(), dispensa);        	
        	        	        	
        } else {

			result = AlunoCursoAtividadeAggregateDTO.gerar(dispensa);
        }
        
        if (result == null) {
        	return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(result);               
    }
 
}
