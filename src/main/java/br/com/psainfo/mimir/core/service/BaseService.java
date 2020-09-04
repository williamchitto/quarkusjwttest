
package br.com.psainfo.mimir.core.service;

import static io.quarkus.panache.common.Sort.by;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import br.com.psainfo.mimir.core.exception.BusinessException;
import br.com.psainfo.mimir.core.model.BaseVersionedEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

public abstract class BaseService<T extends BaseVersionedEntity> implements IBaseService<T> {

	public abstract PanacheRepository<T> getDao();

	@Override
	public T persist(T model) {

		this.getDao().persist(model);

		return model;
	}

	@Override
	public T merge(T model) {

		try {

			T persistedInstance = this.getDao().findById(model.getId());

			if (persistedInstance != null && persistedInstance.isPersistent()) {

				BeanUtils.copyProperties(persistedInstance, model);

				return model;
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("Erro ao efetuar operação: " + e.getMessage());
		}

		return null;
	}

	@Override
	public T merge(final Long id, Parameters parameters) {

		try {

			T persistedInstance = this.getDao().findById(id);

			if (persistedInstance != null && persistedInstance.isPersistent()) {

				BeanUtils.populate(persistedInstance, parameters.map());

				return persistedInstance;
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("Erro ao efetuar operação: " + e.getMessage());
		}

		return null;
	}

	@Override
	public void delete(T model) {

		this.getDao().delete(model);
	}

	@Override
	public T findById(Long id) {

		return this.getDao().findById(id);
	}

	@Override
	public List<T> findAll() {

		return this.getDao().findAll().list();
	}

	@Override
	public PanacheQuery<T> findAllPanacheQuery() {

		return this.getDao().findAll();
	}

	@Override
	public List<T> findAll(String orderByField) {

		return this.getDao().findAll(by(orderByField)).list();
	}

	@Override
	public List<T> findByField(String query, Object... params) {

		return this.getDao().find(query, params).list();
	}

	@Override
	public boolean existsById(Long id) {

		return this.getDao().findById(id) != null;
	}
}
