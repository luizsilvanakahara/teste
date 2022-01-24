package br.edu.cruzeirodosul.domain.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ComprovanteAtividadeComplementarInstituicaoPoloIdAluRgmDTO implements Serializable {
	
	private static final long serialVersionUID = -3520575764407610790L;
	
	@NotNull
	private Long idAlunoRgmNew;

	@NotNull
	private Long codigoInstituicaoNew;
		
	private Long idPoloNew;
	
	@NotNull
	private Long idCurso;

}

