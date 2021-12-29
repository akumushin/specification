package common;

import java.util.List;

import org.springframework.data.domain.Page;

public interface IService<T, TID, TFilter> {
	void save(T entity);
	void delete(TID id);
	T getOne(TID id);
	List<T> getAll();
	Page<T> getOnePage(TFilter filter, PageInfo pageInfo );
	boolean existById(TID id);
}
