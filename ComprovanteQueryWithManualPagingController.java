package br.edu.cruzeirodosul.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import br.edu.cruzeirodosul.config.properties.PageableProperties;
import br.edu.cruzeirodosul.domain.dto.AlunoCursoStatusComprovanteDTO;
import br.edu.cruzeirodosul.domain.entities.ComprovanteAtividadeComplementar;
import br.edu.cruzeirodosul.domain.entities.page.ComprovanteManualPage;
import br.edu.cruzeirodosul.domain.entities.view.Views;
import br.edu.cruzeirodosul.persistence.ComprovanteAtividadeComplementarRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(ComprovanteAtividadeComplementarController.URL)
@Api(value = "Comprovante Atividades Complementares", tags = { "Comprovante Atividades Complementares" })
public class ComprovanteQueryWithManualPagingController {

    private final ComprovanteAtividadeComplementarRepository repository;

    private final PageableProperties pageable;              
    
    @ApiOperation(hidden = true, value = "Busca Comprovantes por array de id RGM do aluno, id do curso e array de status do comprovante")
    @JsonView(Views.ComprovanteAtividadeComplementarView.class)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ComprovanteManualPage> findByIdAlunoRgmChunkAndIdCursoAndStatusComprovante(
    	@Valid AlunoCursoStatusComprovanteDTO filter, BindingResult result) throws BindException {
        
        log.debug("Buscando Comprovante por array de idAlunoRgm, idCurso e array de status comprovante"); 
        
        if (result.hasErrors()) {
			throw new BindException(result);
		}
        
        int totalElements = getTotalElements(filter);        
        int maxItensPage = pageable.getMaxPageSize();        
        int totalPages = (int) Math.ceil(Double.valueOf(totalElements) / Double.valueOf(maxItensPage));
           
        return ResponseEntity.ok(getPagination(filter, maxItensPage, totalElements, totalPages));
    }
    
    private int getTotalElements(AlunoCursoStatusComprovanteDTO filter) {
		
		int totalElements = 0;
		
		while (filter.nextIdAlunoRgmChunk()) {
        	
			totalElements += repository.countByIdAlunoRgmChunkAndIdCursoAndStatusComprovante(filter.getIdAlunoRgmChunk(), 
        			filter.getIdCurso(), filter.getStatusComprovante());
        	        	        	        			        
        }
		
		return totalElements;
		
	}

	private ComprovanteManualPage getPagination(AlunoCursoStatusComprovanteDTO filter, int maxItensPage, 
			int totalElements, int totalPages) {
		
		filter.resetIterations(totalPages);
		
		List<ComprovanteAtividadeComplementar> comprovantes = new ArrayList<>();
                
        int finalIndex = filter.getPagina() * maxItensPage - 1;
        int initialIndex = finalIndex - maxItensPage + 1;
        
        while (filter.nextIdAlunoRgmChunk()) {
        	        	
        	repository.findByIdAlunoRgmChunkAndIdCursoAndStatusComprovante(filter.getIdAlunoRgmChunk(), 
        			filter.getIdCurso(), filter.getStatusComprovante()).stream().forEachOrdered(comprovantes::add);
        	
        	if (isPageFoundWithRange(comprovantes.size(), initialIndex + 1, finalIndex + 1) && filter.isLastIdAlunoRgmChunk()) {
        		
        		comprovantes = comprovantes.subList(initialIndex, comprovantes.size());        		
        		break;
        		
        	}
        	        	
        	if (isPageFound(comprovantes.size(), finalIndex + 1)) {  
        		
        		comprovantes = comprovantes.subList(initialIndex, finalIndex + 1);        		
        		break;
        	}
        	        			        
        }
        
		return ComprovanteManualPage.builder().conteudo(comprovantes)
        		.paginaAtual(filter.getPagina()).ultimaPagina(filter.getPagina() == totalPages).totalPaginas(totalPages)
        		.totalRegistros(totalElements).build();
	}
	
	private boolean isPageFound(int quantityOfElementsFound, int quantityOfElementsExpected) {
		return quantityOfElementsFound >= quantityOfElementsExpected;
	}
		
	private boolean isPageFoundWithRange(int quantityOfElementsFound, int quantityOfElementsExpectedMin, int quantityOfElementsExpectedMax) {
		return quantityOfElementsFound >= quantityOfElementsExpectedMin && quantityOfElementsFound <= quantityOfElementsExpectedMax;
	}

}
