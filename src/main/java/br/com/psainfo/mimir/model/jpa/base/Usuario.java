package br.com.psainfo.mimir.model.jpa.base;

import br.com.psainfo.mimir.core.model.BaseVersionedEntity;
import br.com.psainfo.mimir.core.rest.GsonUtil;
import br.com.psainfo.mimir.dominio.DominioConvenio;
import br.com.psainfo.mimir.dominio.DominioConvenioConverter;
import br.com.psainfo.mimir.dominio.DominioSituacao;
import br.com.psainfo.mimir.dominio.DominioSituacaoConverter;
import com.google.gson.annotations.Expose;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(
    name = "usuario",
    uniqueConstraints =
        @UniqueConstraint(
            name = "usuario_login_uk",
            columnNames = {"login"}))
@RegisterForReflection
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString(includeFieldNames = true)
public class Usuario extends BaseVersionedEntity {

  private static final long serialVersionUID = 643984191220080239L;

  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull private String nome;

  @NotNull private String login;

  @NotNull
  @Convert(converter = DominioSituacaoConverter.class)
  private DominioSituacao ativo;

  @NotNull
  @Column(name = "convenio", nullable = false)
  @Convert(converter = DominioConvenioConverter.class)
  private DominioConvenio convenio;

  @Column(name = "data_contratacao", nullable = true)
  @Temporal(TemporalType.TIMESTAMP)
  private Date dataContratacao;

  @Column(name = "observacao", nullable = true, length = 4000)
  private String observacao;

  @Column(name = "telefone", nullable = true)
  private String telefone;

  @Email
  @Column(name = "email", nullable = true)
  private String email;

  @Column(name = "is_gestor", nullable = false)
  private Boolean isGestor;

  @Column(name = "senha", nullable = true, length = 255)
  private String senha;

  @Column(name = "permissao", nullable = true, length = 255)
  private String permissao;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name="fk_consignataria", nullable=false)
  @Expose(serialize = false)
  private Consignataria consignataria;

  public static Usuario fromJson(final String json) {

    return GsonUtil.instance().fromJson(json, Usuario.class);
  }

  public void setLogin(final String login) {

    this.login = login.trim().toLowerCase();
  }

  public Long getId() {
    return id;
  }
}
