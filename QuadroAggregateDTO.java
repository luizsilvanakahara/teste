package br.edu.cruzeirodosul.domain.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QuadroAggregateDTO implements Serializable {
		
	private static final long serialVersionUID = -4097885681728244903L;
	
	private Long totalQuadrosTemCurso;
	
}
