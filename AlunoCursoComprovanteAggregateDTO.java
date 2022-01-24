package br.edu.cruzeirodosul.domain.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AlunoCursoComprovanteAggregateDTO implements Serializable {
		
	private static final long serialVersionUID = -4097885681728244903L;
	
	private Long totalHoras;
	private Long totalQuantidade;

}
