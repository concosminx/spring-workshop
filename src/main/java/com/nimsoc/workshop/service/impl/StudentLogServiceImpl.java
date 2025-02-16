package com.nimsoc.workshop.service.impl;

import com.nimsoc.workshop.objects.StudentLog;
import com.nimsoc.workshop.service.StudentLogService;
import com.nimsoc.workshop.utils.MessageList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentLogServiceImpl implements StudentLogService {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  private void save(String description) {
    namedParameterJdbcTemplate.update("insert into student_log(description) values (:description)",
        Map.of("description", description));
  }

  @Override
  public List<StudentLog> getAll() {
    return namedParameterJdbcTemplate.query("select id, description from student_log ", Collections.emptyMap(), (rs, rowNum) -> new StudentLog(rs.getInt("id"), rs.getString("description")));
  }

  @Override
  public void insertNonTransactional(String description) {
    debugMiniTransaction();
    save(description);
  }

  private void debugMiniTransaction() {
    boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
    log.info("Mini transaction active: {}", actualTransactionActive);
    if (actualTransactionActive) {
      log.info("Mini transaction name {}", TransactionAspectSupport.currentTransactionStatus().getTransactionName());
    }
  }

  @Override
  @Transactional
  public MessageList insertTransactionalRequired(String description) {
    MessageList ml = new MessageList();
    debugMiniTransaction();

    try {
      save(description);
    } catch (Exception exc) {
      log.error("Error on [StudentLog.insertTransactionalRequired] ", exc);

      ml.addErrorMessage("Error inserting StudentLog with " + exc.getMessage());
    } finally {
      if (ml.hasErrorMessages()) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
    }

    return ml;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public MessageList insertTransactionalRequiresNew(String description) {
    MessageList ml = new MessageList();
    debugMiniTransaction();
    try {
      save(description);
    } catch (Exception exc) {
      log.error("Error on [StudentLog.insertTransactionalRequired] ", exc);

      ml.addErrorMessage("Error inserting StudentLog with " + exc.getMessage());
    } finally {
      if (ml.hasErrorMessages()) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
    }

    return ml;
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  public void insertTransactionalSupports(String description) {
    debugMiniTransaction();
    save(description);

  }

  @Transactional(propagation = Propagation.MANDATORY)
  public void insertTransactionalMandatory(String description) {
    debugMiniTransaction();
    save(description);
  }

  @Transactional(propagation = Propagation.NEVER)
  public void insertTransactionalNever(String description) {
    debugMiniTransaction();
    save(description);
  }

  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public void insertTransactionalNotSupported(String description) {
    debugMiniTransaction();
    save(description);
  }

  @Transactional(propagation = Propagation.NESTED)
  //@Transactional(propagation = Propagation.REQUIRES_NEW)
  public MessageList insertTransactionalNested(Integer id, String description) {
    debugMiniTransaction();
    MessageList ml = new MessageList();
    try {
      //check if we see the record inserted in main service
      String foundName = namedParameterJdbcTemplate.query("select name from student where id = :id ", Map.of("id", id), rs -> {
        if (rs.next()) {
          return rs.getString("name");
        }
        return "N/A";
      });

      ml.addInfoMessage("The name for the student with " + id + " is: " + foundName);

      save(description);
    } catch (Exception exc) {
      log.error("Error on [StudentLog.insertTransactionalNested] ", exc);

      ml.addErrorMessage("Error inserting StudentLog with " + exc.getMessage());
    } finally {
      if (ml.hasErrorMessages()) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
    }

    return ml;
  }
}
