
package br.com.psainfo.mimir.core.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import br.com.psainfo.mimir.core.model.BaseVersionedEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;

public interface IBaseService<T extends BaseVersionedEntity> {

	T persist(T model);

	T merge(T model) throws IllegalAccessException, InvocationTargetException;

	T merge(final Long id, final Parameters parameters);

	void delete(T model);

	T findById(Long id);

	List<T> findAll();

	List<T> findAll(String orderByField);

	PanacheQuery<T> findAllPanacheQuery();

	List<T> findByField(String query, Object... params);

	boolean existsById(final Long id);

}
