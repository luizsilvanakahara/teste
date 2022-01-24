package br.edu.cruzeirodosul.domain.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import br.edu.cruzeirodosul.domain.enums.SituacaoTemasTransversaisConstraint;
import br.edu.cruzeirodosul.domain.enums.StatusComprovanteConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class ComprovanteAtividadesAlunoDTO implements Serializable {
	
	private static final long serialVersionUID = 930490098635544641L;
		
	private final Long idAlunoRgm;
	private final Long idCurso;
	private final String tituloComprovante;
	private final Integer cargaHoraria;
	private final StatusComprovanteConstraint statusComprovante;	
	private final String situacaoTemasTransversais;
	private final LocalDateTime dataAnalise;
	private final LocalDateTime dataCriacao;
	private final Integer horasDispensadas;
	private final Long totalHorasRealizadasAtividadesComplementares;
	private final Long totalHorasRealizadasTemasTransversais;	
	private final Long totalHorasRealizadasTrilhas;	
		
	public Long getTotalHorasRealizadasAtividadesComplementares() {
		if (totalHorasRealizadasAtividadesComplementares == null) {
			return 0L;
		}
		
		return totalHorasRealizadasAtividadesComplementares;
	}
	
	public Long getTotalHorasRealizadasTemasTransversais() {
		if (totalHorasRealizadasTemasTransversais == null) {
			return 0L;
		}
						
		return totalHorasRealizadasTemasTransversais;			
	}
	
	public Long getTotalHorasRealizadasTrilhas() {
		if (totalHorasRealizadasTrilhas == null) {
			return 0L;
		}
						
		return totalHorasRealizadasTrilhas;
	}
	
	public Integer getHorasDispensadas( ) {
		if (horasDispensadas == null) {
			return 0;
		}
		
		return horasDispensadas;
	}
	
	public static Relatorio gerar(List<ComprovanteAtividadesAlunoDTO> atividadesAluno) {
		
		if (atividadesAluno == null || atividadesAluno.isEmpty()) {
			return null;
		}
		
		List<Atividade> atividades = atividadesAluno.parallelStream().map(a -> Atividade.builder()
				.tituloComprovante(a.getTituloComprovante())
				.statusComprovante(a.getStatusComprovante())
				.dataAnalise(a.getDataAnalise())
				.dataCriacao(a.getDataCriacao())
				.cargaHoraria(a.getCargaHoraria()).build())
				.collect(Collectors.toList());
		
		ComprovanteAtividadesAlunoDTO aluno = atividadesAluno.get(0);
		
		return Relatorio.builder().idAlunoRgm(aluno.getIdAlunoRgm()).idCurso(aluno.getIdCurso())
			
			.situacaoTemasTransversais(SituacaoTemasTransversaisConstraint.valueOf(aluno.getSituacaoTemasTransversais()))
			.totalHorasRealizadasAtividadesComplementares(aluno.getTotalHorasRealizadasAtividadesComplementares())
			.totalHorasRealizadasTemasTransversais(aluno.getTotalHorasRealizadasTemasTransversais())
			.totalHorasRealizadasTrilhas(aluno.getTotalHorasRealizadasTrilhas())
			.horasDispensadas(aluno.getHorasDispensadas()).atividades(atividades)
			.build();			
	}
		
	@Builder	
	@Getter
	public static class Relatorio implements Serializable {	
		
		private static final long serialVersionUID = -8828266925083621502L;
		
		private Long idAlunoRgm;
		private Long idCurso;		
		private SituacaoTemasTransversaisConstraint situacaoTemasTransversais;
		private Long totalHorasRealizadasAtividadesComplementares;
		private Long totalHorasRealizadasTemasTransversais;
		private Long totalHorasRealizadasTrilhas;
		private Integer horasDispensadas;		
		private Collection<Atividade> atividades;		
		
	}
	
	@Builder	
	@Getter
	public static class Atividade implements Serializable {
		
		private static final long serialVersionUID = -506960777174542707L;
		
		private String tituloComprovante;
		private Integer cargaHoraria;
		private StatusComprovanteConstraint statusComprovante;
		private LocalDateTime dataAnalise;
		private LocalDateTime dataCriacao;
		
	}

}
