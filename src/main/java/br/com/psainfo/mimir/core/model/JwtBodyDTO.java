package br.com.psainfo.mimir.core.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@RegisterForReflection
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtBodyDTO implements Serializable {

	private static final long serialVersionUID = 2512745478176186561L;

	private String jti;

	private Integer iat;

	private String iss;

	private List<String> permissions = null;

	private Integer exp;

	private String name;

	private Integer accessType;

}