package hexlet.code.mapper;

import hexlet.code.model.BaseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.TargetType;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class SlugMapper {
    @PersistenceContext
    private EntityManager entityManager;

    public <T extends BaseEntity> T toEntity(String slug, @TargetType Class<T> entityClass) {
        if (slug == null || slug.isEmpty()) {
            return null;
        }

        try {
            return entityManager.createQuery(
                            "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e.slug = :slug",
                            entityClass)
                    .setParameter("slug", slug)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }
}
