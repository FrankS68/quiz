package de.witchcafe.quiz;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import consulting.segieth.security.UserProfile;

@Service
public class QuizService {

    private final QuizRepository quizRepository;

    QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }
    
    @Transactional
    public Quiz createQuiz(String c,UserProfile u) {
        var quiz = new Quiz(c,u);
        quizRepository.saveAndFlush(quiz);
        return quiz;
    }
    
    @Transactional
    public Quiz createQuiz(String c,UserProfile u,String r) {
        var quiz = new Quiz(c,u);
        quiz.setResult(r);
        quizRepository.saveAndFlush(quiz);
        return quiz;
    }
    
    @Transactional(readOnly = true)
    public List<Quiz> list(Pageable pageable) {
        return quizRepository.findAllBy(pageable).toList();
    }
}
