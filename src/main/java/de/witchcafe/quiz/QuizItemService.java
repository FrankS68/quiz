package de.witchcafe.quiz;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuizItemService {

    private final QuizItemRepository quizItemRepository;

    QuizItemService(QuizItemRepository quizItemRepository) {
        this.quizItemRepository = quizItemRepository;
    }
    
    @Transactional
    public QuizItem createQuizItem(String c,String q,String a,String d) {
        var quizItem = new QuizItem(c,q,a,d);
        quizItemRepository.saveAndFlush(quizItem);
        return quizItem;
    }
    
    @Transactional
    public QuizItem createQuizItem(String c,String q,String a,String d,ArrayList<String> o,ArrayList<String> t) {
        var quizItem = new QuizItem(c,q,a,d);
        quizItem.setOptions(o);
        quizItem.setTags(t);
        quizItemRepository.saveAndFlush(quizItem);
        return quizItem;
    }
    
    @Transactional(readOnly = true)
    public List<QuizItem> list(Pageable pageable) {
        return quizItemRepository.findAllBy(pageable).toList();
    }
    
    @Transactional(readOnly = true)
    public List<QuizItem> findByCategory(String category) {
        return quizItemRepository.findByCategory(category);
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> lookupCategories() {
        return quizItemRepository.lookupCategories();
    }
}
