package br.edu.cruzeirodosul.domain.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import br.edu.cruzeirodosul.domain.enums.StatusComprovanteConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ComprovanteAtividadeComplementarTrilhaAlunoDTO implements Serializable {
	
	private static final long serialVersionUID = 930490098635544641L;
	private static final int PERCENTUAL = 10;
	
	private  Long idAlunoRgm;
	private  Long idCurso;
	private  String trilha;	
	private  int cargaHorariaAproveitada;
	private  LocalDateTime dataCriacao;
    private  StatusComprovanteConstraint statusComprovante;    	
	

	public String getPeriodoLetivo() {
		return dataCriacao.format(DateTimeFormatter.ofPattern("yyyyMM"));
	}
	

	public Integer getCargaHorariaTotal() {				
		return this.cargaHorariaAproveitada * PERCENTUAL;
	}	
}
