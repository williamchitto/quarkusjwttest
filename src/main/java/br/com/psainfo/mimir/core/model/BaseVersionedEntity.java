package br.com.psainfo.mimir.core.model;

import com.google.gson.Gson;
import io.quarkus.hibernate.orm.panache.runtime.JpaOperations;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public abstract class BaseVersionedEntity<T> implements Serializable {

	private static final long serialVersionUID = -3251855217707609049L;

	@Version
	@Column(nullable = false)
	private Long versao;

	@NotNull
	@Column(name = "data_inclusao", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataInclusao;

	@Column(name = "data_alteracao", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataAlteracao;

	@PreRemove
	public void preRemove() {

		this.populaDadosGerais();
	}

	private void populaDadosGerais() {

		if(this.getId() == null) {

			this.dataInclusao = new Date();

		}
		else {

			this.dataAlteracao = new Date();

		}
	}

	public abstract Long getId();

	public abstract void setId(final Long id);

	@PrePersist
	public void prePersist() {

		this.populaDadosGerais();
	}

	@PreUpdate
	public void preUpdate() {

		this.populaDadosGerais();
	}

	public String toJson() {

		return new Gson().toJson(this);
	}

	@JsonbTransient
	public boolean isPersistent() {

		return JpaOperations.isPersistent(this);
	}

	public Date getDataInclusao() {

		return this.dataInclusao;
	}

	public void setDataInclusao(final Date dataInclusao) {

		this.dataInclusao = dataInclusao;
	}

	public Date getDataAlteracao() {

		return this.dataAlteracao;
	}

	public void setDataAlteracao(final Date dataAlteracao) {

		this.dataAlteracao = dataAlteracao;
	}

}