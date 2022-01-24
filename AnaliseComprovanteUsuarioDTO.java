package br.edu.cruzeirodosul.domain.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.edu.cruzeirodosul.domain.enums.StatusComprovanteConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class AnaliseComprovanteUsuarioDTO implements Serializable {

	private static final long serialVersionUID = 7947308718497233127L;

	private String usuario;
	private Long idAlunoRgm;
	private Long idCurso;
	private Long codigoInstituicao;
	private String tituloComprovante;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = ISO.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dataAnalise;
	
	private StatusComprovanteConstraint statusComprovante;

}
