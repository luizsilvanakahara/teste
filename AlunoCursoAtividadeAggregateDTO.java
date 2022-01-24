package br.edu.cruzeirodosul.domain.dto;

import java.io.Serializable;

import br.edu.cruzeirodosul.domain.entities.Dispensa;
import br.edu.cruzeirodosul.domain.enums.SituacaoTemasTransversaisConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class AlunoCursoAtividadeAggregateDTO implements Serializable {
		
	private static final long serialVersionUID = 8277165458024491612L;
	
	private final Long totalHorasComprovante;	
	private final Long totalQuantidadeComprovante;
	private final Long totalHorasTemasTransversais;	
	private final Long totalHorasTrilhas;	
	private final Long totalQuantidadeTrilhas;
	private Long quantidadeHorasDispensadas;
	private SituacaoTemasTransversaisConstraint situacaoTemasTransversais;
	
	public static TotalAtividadesRealizadas gerar(AlunoCursoAtividadeAggregateDTO atividade, Dispensa dispensa) {
		
		if (dispensa == null) {
			
			dispensa = Dispensa.builder()
					.quantidadeHoras(0)
					.situacaoTemasTransversais(SituacaoTemasTransversaisConstraint.A_CUMPRIR)
					.build();
			
		}
		
		return TotalAtividadesRealizadas.builder()
				.comprovante(Comprovante.builder().totalHoras(atividade.getTotalHorasComprovante())
						.totalQuantidade(atividade.getTotalQuantidadeComprovante()).build())
				.temaTransversal(TemaTransversal.builder().totalHoras(atividade.getTotalHorasTemasTransversais()).build())
				.trilha(Trilha.builder().totalHoras(atividade.getTotalHorasTrilhas())
						.totalQuantidade(atividade.getTotalQuantidadeTrilhas()).build())
				.dispensa(DispensaComSituacaoTemaTransversal.builder()
						.quantidadeHoras(Long.valueOf(dispensa.getQuantidadeHoras()))
						.situacaoTemasTransversais(dispensa.getSituacaoTemasTransversais()).build())
				.build();			
	}
	
	public static TotalAtividadesRealizadas gerar(Dispensa dispensa) {
		
		if (dispensa == null) {
			return null;
		}
				
		return TotalAtividadesRealizadas.builder()
				.comprovante(Comprovante.builder().totalHoras(0L)
						.totalQuantidade(0L).build())
				.temaTransversal(TemaTransversal.builder().totalHoras(0L).build())
				.trilha(Trilha.builder().totalHoras(0L)
						.totalQuantidade(0L).build())
				.dispensa(DispensaComSituacaoTemaTransversal.builder()
						.quantidadeHoras(Long.valueOf(dispensa.getQuantidadeHoras()))
						.situacaoTemasTransversais(dispensa.getSituacaoTemasTransversais()).build())
				.build();		
	}
	
	public Long getTotalHorasComprovante() {
		if (totalHorasComprovante == null) {
			return 0L;
		}
		
		return totalHorasComprovante;
	}
	
	public Long getTotalQuantidadeComprovante() {
		if (totalQuantidadeComprovante == null) {
			return 0L;
		}
		
		return totalQuantidadeComprovante;
	}
	
	public Long getTotalHorasTemasTransversais() {
		if (totalHorasTemasTransversais == null) {
			return 0L;
		}
		
		return totalHorasTemasTransversais;
	}
	
	public Long getTotalHorasTrilhas() {
		if (totalHorasTrilhas == null) {
			return 0L;
		}
		
		return totalHorasTrilhas;
	}
	
	public Long getTotalQuantidadeTrilhas() {
		if (totalQuantidadeTrilhas == null) {
			return 0L;
		}
		
		return totalQuantidadeTrilhas;
	}
	
	public Long getQuantidadeHorasDispensadas() {
		if (quantidadeHorasDispensadas == null) {
			return 0L;
		}
		
		return quantidadeHorasDispensadas;
	}
	
	public SituacaoTemasTransversaisConstraint getSituacaoTemasTransversais() {
		if (situacaoTemasTransversais == null) {
			return SituacaoTemasTransversaisConstraint.A_CUMPRIR;
		}
		
		return this.situacaoTemasTransversais;
	}
	
	@Builder	
	@Getter
	public static class TotalAtividadesRealizadas implements Serializable {	
												
		private static final long serialVersionUID = -301315350584124377L;
		
		private Comprovante comprovante;
		private TemaTransversal temaTransversal;	
		private Trilha trilha;
		private DispensaComSituacaoTemaTransversal dispensa;
		
	}
			
	@Builder	
	@Getter
	private static class Comprovante implements Serializable {	
			
		private static final long serialVersionUID = -7427066902606802994L;
		
		private Long totalHoras;
		private Long totalQuantidade;
		
	}
	
	@Builder	
	@Getter
	private static class TemaTransversal implements Serializable {	
												
		private static final long serialVersionUID = 1819313900974110668L;
		
		private Long totalHoras;				
		
	}
	
	@Builder	
	@Getter
	private static class Trilha implements Serializable {	
												
		private static final long serialVersionUID = 3321109330757559129L;
				
		private Long totalHoras;	
		private Long totalQuantidade;
		
	}
	
	@Builder	
	@Getter
	private static class DispensaComSituacaoTemaTransversal implements Serializable {	
												
		private static final long serialVersionUID = 5874800925606999502L;
		
		private Long quantidadeHoras;	
		private SituacaoTemasTransversaisConstraint situacaoTemasTransversais;
		
	}

}
