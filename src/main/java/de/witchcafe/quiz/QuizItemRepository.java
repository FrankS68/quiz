package de.witchcafe.quiz;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface QuizItemRepository extends JpaRepository<QuizItem, Long>, JpaSpecificationExecutor<QuizItem> {

    // If you don't need a total row count, Slice is better than Page as it only performs a select query.
    // Page performs both a select and a count query.
    Slice<QuizItem> findAllBy(Pageable pageable);
	List<QuizItem> findByCategory(String category);

    @Query("SELECT category, count(*) FROM QuizItem GROUP BY category ORDER BY category DESC")
    List<Object[]> lookupCategories();
}
