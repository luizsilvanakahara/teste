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
public class ComprovanteAtividadeComplementarPoloDTO implements Serializable{
	
	private static final long serialVersionUID = -3520575764407610790L;
	
	@NotNull
	private Long idAlunoRgm;
	
	@NotNull
	private Long idPoloNew;
	
	@NotNull
	private Long idCurso;
}
