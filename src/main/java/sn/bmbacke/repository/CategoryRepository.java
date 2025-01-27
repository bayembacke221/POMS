package sn.bmbacke.repository;

import sn.bmbacke.models.Category;

public interface CategoryRepository extends  GenericRepository<Category, Integer> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Integer id);
}
