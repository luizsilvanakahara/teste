package br.edu.cruzeirodosul.domain.dto;

import java.io.Serializable;
import java.util.Arrays;

import javax.validation.constraints.NotNull;

import br.edu.cruzeirodosul.domain.enums.StatusComprovanteConstraint;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AlunoCursoStatusComprovanteDTO  implements Serializable {
	
	public static final Double INDEX_LIMIT = 1000.0;
	
	private static final long serialVersionUID = -9017519664328458999L;	
		
	@NotNull
	private Long[] idAlunoRgm;
	
	@NotNull
	private Long idCurso;
	
	@NotNull
	private StatusComprovanteConstraint[] statusComprovante;
	
	private int pagina;
	
	@ApiModelProperty(hidden = true)
	private Double iterations;
	
	@ApiModelProperty(hidden = true)
	private int startIndex;
	
	@ApiModelProperty(hidden = true)
	private int endIndex;	
	
	@ApiModelProperty(hidden = true)
	public boolean nextIdAlunoRgmChunk() {		
		int maxLength = getMaxLength();
		
		if (iterations == null) {
			iterations = Math.ceil(maxLength / INDEX_LIMIT);
		}
		
		iterations--;
		startIndex = endIndex;
    	endIndex = startIndex + INDEX_LIMIT.intValue();
    	
    	if (endIndex > maxLength) {
        	endIndex = maxLength;
        }
		
		return iterations >= 0; 
	}
	
	@ApiModelProperty(hidden = true)
	public boolean isLastIdAlunoRgmChunk() {
		return iterations <= 0;
	}
	
	@ApiModelProperty(hidden = true)
	public void resetIterations(int quantityPageFound) {
		
		if (pageLessOrEqualThan(quantityPageFound)) {
			this.iterations = null;
			this.startIndex = 0;
			this.endIndex = 0;
		}
				
	}
	
	@ApiModelProperty(hidden = true)
	public int getMaxLength() {
		if (idAlunoRgm == null) {
			return 0;
		}
		return idAlunoRgm.length;
	}
	
	@ApiModelProperty(hidden = true)
	public Long[] getIdAlunoRgmChunk() {
		return Arrays.asList(idAlunoRgm).subList(startIndex, endIndex).toArray(new Long[0]);
	}
	
	@ApiModelProperty(hidden = true)
	public int getPagina() {
		if (pagina == 0) {
			return 1;
		}
		
		return pagina;		
	}
	
	@ApiModelProperty(hidden = true)
	public boolean pageLessOrEqualThan(int quantityPageFound) {
		return getPagina() <= quantityPageFound;
	}
	
}
