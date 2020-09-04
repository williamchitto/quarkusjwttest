package br.com.psainfo.mimir.dao.jpa;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.psainfo.mimir.dominio.DominioSituacao;
import br.com.psainfo.mimir.model.jpa.base.Plano;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class PlanoDao implements PanacheRepository<Plano> {
	
	@Inject
	EntityManager entityManager;
	
	public Plano findByLogin(final String login) {
		
		final StringBuilder sql = new StringBuilder();
		sql.append(" SELECT pln.id, pln.fk_id_usuario, pln.situacao, pln.tipo, pln.data_inicial, pln.data_final, pln.numero_consulta, pln.numero_consulta_limite, pln.data_alteracao, pln.data_inclusao, pln.versao ");
		sql.append(" FROM mimir.plano pln ");
		sql.append(" INNER JOIN mimir.usuario usr ");
		sql.append(" ON(pln.fk_id_usuario = usr.id AND pln.situacao = :situacao AND usr.login = :login) ");
		
		final Query query = this.entityManager.createNativeQuery(sql.toString(), Plano.class);
		
		query.setParameter("situacao", DominioSituacao.ATIVO.getCod());	
		query.setParameter("login", login);
		
		return (Plano)query.getSingleResult();		
	}
	
	public Integer updateConsultaByLogin(final Plano plano) {
		final StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE mimir.plano pln ");
		sql.append(" SET pln.numero_consulta = :numero_consulta ");
		sql.append(" WHERE pln.id = :id ");
		
		final Query query = this.entityManager.createNativeQuery(sql.toString(), Plano.class);
		
		query.setParameter("numero_consulta", plano.getNumeroConsulta());	
		query.setParameter("id", plano.getId());
		
		return query.executeUpdate();
	}
}
