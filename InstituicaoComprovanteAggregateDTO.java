package br.edu.cruzeirodosul.domain.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InstituicaoComprovanteAggregateDTO implements Serializable {
	
	private static final long serialVersionUID = -1828031204455922796L;
	
	private Long codigoInstituicao;
	private Long quantidadeComprovante;

}
