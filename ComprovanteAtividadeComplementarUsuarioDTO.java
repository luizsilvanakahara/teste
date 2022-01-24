package br.edu.cruzeirodosul.domain.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ComprovanteAtividadeComplementarUsuarioDTO implements Serializable {
	
	private static final long serialVersionUID = 3133393681762217475L;
	
	private String nomeUsuario;

}
