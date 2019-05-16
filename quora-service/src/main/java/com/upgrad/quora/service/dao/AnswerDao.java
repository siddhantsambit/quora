package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;
    //Named queries created according to the functionality as suggested by the name of the respective methods
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }
    public List<AnswerEntity> getAllAnswersByQuestion(final QuestionEntity questionEntity){
        try {
            return entityManager.createNamedQuery("allAnswersByQuestion", AnswerEntity.class).setParameter("questionEntity", questionEntity).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
    public AnswerEntity getAnswerByAnsUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("answerByAnsUuid", AnswerEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    public AnswerEntity updateAnswer(final AnswerEntity answerEntity) {
        return entityManager.merge(answerEntity);
    }
    public String deleteAnswer(final AnswerEntity answerEntity) {
        String uuid=answerEntity.getUuid();
        entityManager.remove(answerEntity);
        return uuid;
    }
}