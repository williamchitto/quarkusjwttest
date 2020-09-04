
package br.com.psainfo.mimir.core.model;

import java.io.Serializable;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@RegisterForReflection
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetornoDTO implements Serializable {

	private static final long serialVersionUID = -3165388233682335755L;

	private Integer codigo;

	private String mensagem;
}
