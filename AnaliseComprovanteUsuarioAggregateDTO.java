package br.edu.cruzeirodosul.domain.dto;

import java.io.Serializable;

import br.edu.cruzeirodosul.domain.enums.StatusComprovanteConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AnaliseComprovanteUsuarioAggregateDTO implements Serializable {
	
	private static final long serialVersionUID = -3520575764407610790L;
	
	private String usuario;
	private Long quantidadeComprovanteAnalisado;
	private StatusComprovanteConstraint statusComprovante;

}
