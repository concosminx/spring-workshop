package com.nimsoc.workshop.service.impl;

import com.nimsoc.workshop.objects.Student;
import com.nimsoc.workshop.service.StudentLogService;
import com.nimsoc.workshop.service.StudentService;
import com.nimsoc.workshop.utils.MessageList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService, InitializingBean {

  private final DataSource dataSource;
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private final StudentLogService studentLogService;
  private final PlatformTransactionManager transactionManager;
  private TransactionTemplate newTransactionTemplate;

  public List<Student> getAll() {
    return namedParameterJdbcTemplate.query("select id, name, age from student ", Collections.emptyMap(), new RowMapper<Student>() {
      @Override
      public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Student(rs.getInt("id"), rs.getString("name"), rs.getInt("age"));
      }
    });
  }

  private Student load(Integer studentId) {
    return namedParameterJdbcTemplate.query("select id, name, age from student where id = :id", Map.of("id", studentId), new ResultSetExtractor<Student>() {
      @Override
      public Student extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (rs.next()) {
          return new Student(rs.getInt("id"), rs.getString("name"), rs.getInt("age"));
        }
        return null;
      }
    });
  }

  private void update(Student student) {
    namedParameterJdbcTemplate.update("update student set name = :name, age = :age where id = :id ",
        Map.of("name", student.getName(), "age", student.getAge(), "id", student.getId()));
  }

  private int save(Student student) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    namedParameterJdbcTemplate.update("insert into student(name, age) values (:name, :age)",
        new MapSqlParameterSource(Map.of("name", student.getName(), "age", student.getAge())),
        keyHolder, new String[]{"id"});

    return keyHolder.getKey().intValue();
  }

  private String getDescription(Student student) {
    //we force some error if the student name starts with an A
    String description = "Student with name " + student.getName() + " was inserted.";
    if (student.getName().startsWith("A")) {
      //violate a constraint, on purpose
      description = "Student with name " + student.getName() + " was inserted. And a very long description after that in order to violate the column length constraint ... ";
    }
    return description;
  }


  //----------------------- DEMO METHODS -------------------------------
  @Override
  public void insertNonTransactional(List<Student> students) {
    debugMainTransaction();

    //we are relying on Spring to roll back the transaction for an eventual RuntimeException
    students.forEach(student -> {
      String description = getDescription(student);
      save(student);
      studentLogService.insertNonTransactional(description);
    });
  }

  @Override
  @Transactional
  public MessageList insertTransactionalRequired(List<Student> students) {
    debugMainTransaction();

    final MessageList ml = new MessageList();

    try {
      students.forEach(student -> {
        String description = getDescription(student);

        save(student);

        // another service call, on the same transaction
        // we need to know if that service call was completed
        // successfully or not
        MessageList logMessages = studentLogService.insertTransactionalRequired(description);
        ml.transferMessages(logMessages);

      });
    } catch (Exception exc) {
      log.error("Error on [Student.insertTransactionalRequired] ", exc);

      ml.addErrorMessage("Error inserting Student with " + exc.getMessage());
    } finally {
      if (ml.hasErrorMessages()) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
    }

    return ml;
  }

  @Override
  @Transactional
  public MessageList insertTransactionalRequiresNew(List<Student> students) {
    final MessageList ml = new MessageList();
    debugMainTransaction();

    try {
      students.forEach(student -> {
        String description = getDescription(student);

        //the REQUIRES_NEW transactions will be persisted even if the big transaction will fail
        //if (student.getName().startsWith("X")) {
        //  throw new RuntimeException("We got a student with X in name. We need to rollback!");
        //}

        save(student);

        // another service call, on the different transaction
        // we want to continue even if the insertion in the log table fails
        // we will add the error only as information messages
        MessageList logMessages = studentLogService.insertTransactionalRequiresNew(description);
        //we don't care about the rollback on the new transaction
        logMessages.getMessages().forEach(
            x -> ml.addInfoMessage("Original severity (" + x.getSeverity() + "). " + x.getMessage())
        );

      });
    } catch (Exception exc) {
      log.error("Error on [Student.insertTransactionalRequiresNew] ", exc);

      ml.addErrorMessage("Error inserting Student with " + exc.getMessage());
    } finally {
      if (ml.hasErrorMessages()) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
    }

    return ml;
  }

  @Override
  @Transactional
  public void insertTransactionalSupports(List<Student> students) {
    debugMainTransaction();


    students.forEach(student -> {
      String description = getDescription(student);

      save(student);
      studentLogService.insertTransactionalSupports(description);

    });
  }

  private void debugMainTransaction() {
    boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
    log.info("Main transaction active: {}", actualTransactionActive);
    if (actualTransactionActive) {
      log.info("Main transaction name: {}", TransactionAspectSupport.currentTransactionStatus().getTransactionName());
    }
  }

  @Override
  //@Transactional
  public void insertTransactionalMandatory(List<Student> students) {
    debugMainTransaction();
    students.forEach(student -> {
      String description = getDescription(student);

      save(student);
      studentLogService.insertTransactionalMandatory(description);

    });
  }

  @Override
  @Transactional
  public void insertTransactionalNever(List<Student> students) {
    debugMainTransaction();
    students.forEach(student -> {
      String description = getDescription(student);

      save(student);
      studentLogService.insertTransactionalNever(description);

    });
  }

  @Override
  @Transactional
  public void insertTransactionalNotSupported(List<Student> students) {
    debugMainTransaction();
    log.info("Has transaction is main service.before: {}", TransactionAspectSupport.currentTransactionStatus().hasTransaction());

    students.forEach(student -> {
      String description = getDescription(student);
      save(student);
      studentLogService.insertTransactionalNotSupported(description);

      log.info("Has transaction is main service.after: {}", TransactionAspectSupport.currentTransactionStatus().hasTransaction());

    });
  }

  @Override
  @Transactional
  public MessageList insertTransactionalNested(List<Student> students) {
    final MessageList ml = new MessageList();
    debugMainTransaction();

    try {
      students.forEach(student -> {
        String description = getDescription(student);

        Integer id = save(student);

        // another service call, on the different transaction
        // we want to continue even if the insertion in the log table fails
        // we will add the error only as information messages
        MessageList logMessages = studentLogService.insertTransactionalNested(id, description);
        //we don't care about the rollback on the nested transaction
        logMessages.getMessages().forEach(
            x -> ml.addInfoMessage("Original severity (" + x.getSeverity() + "). " + x.getMessage())
        );

      });
    } catch (Exception exc) {
      log.error("Error on [Student.insertTransactionalNested] ", exc);

      ml.addErrorMessage("Error inserting Student with " + exc.getMessage());
    } finally {
      if (ml.hasErrorMessages()) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
    }

    return ml;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void insertAStudent(Student s) {
    try {
      log.info("Mini Transaction name {}", TransactionAspectSupport.currentTransactionStatus().getTransactionName());
      save(s);
    } catch (Exception exc) {
      log.error("Error saving " + exc.getMessage());

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
  }

  @Override
  @Transactional
  public MessageList insertTransactionalSelfInvocation(List<Student> students) {
    final MessageList ml = new MessageList();
    log.info("Main Transaction name {}", TransactionAspectSupport.currentTransactionStatus().getTransactionName());

    try {
      students.forEach(student -> {

        //only a method call, not a new transaction
        insertAStudent(student);

        log.info("Checking transaction status after {} and isMarkedForRollback: {}",
            student.getName(),
            TransactionAspectSupport.currentTransactionStatus().isRollbackOnly());

      });

    } catch (Exception exc) {
      log.error("Error on [Student.insertTransactionalSelfInvocation] ", exc);

      ml.addErrorMessage("Error inserting Student with " + exc.getMessage());
    } finally {
      if (ml.hasErrorMessages()) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
    }

    return ml;
  }

  @Override
  @Transactional
  public MessageList insertTransactionTemplate(List<Student> students) {
    final MessageList ml = new MessageList();
    log.info("Main Transaction name {}", TransactionAspectSupport.currentTransactionStatus().getTransactionName());

    try {
      students.forEach(student -> {
        String description = getDescription(student);

        save(student);

        newTransactionTemplate.execute(status -> {

          MessageList logMessages = studentLogService.insertTransactionalRequired(description);

          logMessages.getMessages().forEach(
              x -> ml.addInfoMessage("Original severity (" + x.getSeverity() + "). " + x.getMessage())
          );

          // don't forget to mark the status as rollback if it's the case
          if (logMessages.hasMessages()) {
            status.setRollbackOnly();
          }

          return null;
        });

      });
    } catch (Exception exc) {
      log.error("Error on [Student.insertTransactionTemplate] ", exc);

      ml.addErrorMessage("Error inserting Student with " + exc.getMessage());
    } finally {
      if (ml.hasErrorMessages()) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
    }

    return ml;
  }


  @Override
  @Transactional(isolation = Isolation.READ_UNCOMMITTED)
  public MessageList insertIsolationReadUncommitted(List<Student> students) {
    //Dirty read
    //Cannot be tested on PostgreSQL - see https://www.postgresql.org/docs/current/transaction-iso.html
    final MessageList ml = new MessageList();
    try {

      List<Student> all = getAll();
      ml.addInfoMessage("Already in the database: " + all);

      //see if we got some records in the database
      students.forEach(this::save);

      if (students.size() > 1) {
        //simulate some heavy operations
        TimeUnit.SECONDS.sleep(20);
      }

    } catch (Exception exc) {
      log.error("Error on [Student.insertIsolationReadUncommitted] ", exc);

      ml.addErrorMessage("Error inserting Student with " + exc.getMessage());
    } finally {
      if (ml.hasErrorMessages()) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
    }

    return ml;
  }

  @Override
  @Transactional(isolation = Isolation.READ_COMMITTED)
  public MessageList insertIsolationReadCommitted(Student student) {
    //Non-repeatable read
    final MessageList ml = new MessageList();
    try {
      Student fromDb1 = load(student.getId());
      ml.addInfoMessage("First read: " + fromDb1);

      //if age is even we wait
      if (student.getAge() % 2 == 0) {
        TimeUnit.SECONDS.sleep(10);
        ml.addInfoMessage("Check for non repeatable read");
      } else {
        update(student);
        ml.addInfoMessage("Student updated: " + fromDb1 + " with " + student);
      }

      Student fromDb2 = load(student.getId());
      ml.addInfoMessage("Second read: " + fromDb2);

    } catch (Exception exc) {
      log.error("Error on [Student.insertIsolationReadCommitted] ", exc);

      ml.addErrorMessage("Error inserting Student with " + exc.getMessage());
    } finally {
      if (ml.hasErrorMessages()) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
    }

    return ml;
  }

  @Override
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public MessageList insertIsolationRepeatableRead(List<Student> students) {
    //Phantom read
    final MessageList ml = new MessageList();
    try {

      ml.addInfoMessage("First read before saving: " + getAll());

      //see if we got some records in the database
      students.forEach(this::save);

      if (students.size() > 1) {
        //simulate some heavy operations
        TimeUnit.SECONDS.sleep(10);
      }

      ml.addInfoMessage("Second read: " + getAll());

    } catch (Exception exc) {
      log.error("Error on [Student.insertIsolationRepeatableRead] ", exc);

      ml.addErrorMessage("Error inserting Student with " + exc.getMessage());
    } finally {
      if (ml.hasErrorMessages()) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
    }

    return ml;
  }

  @Override
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public MessageList insertIsolationSerializable(List<Student> students) {
    //Phantom read
    final MessageList ml = new MessageList();
    try {

      ml.addInfoMessage("First read before saving: " + getAll());

      //see if we got some records in the database
      students.forEach(this::save);

      if (students.size() > 1) {
        //simulate some heavy operations
        TimeUnit.SECONDS.sleep(20);
      }

      ml.addInfoMessage("Second read read before saving: " + getAll());

    } catch (Exception exc) {
      log.error("Error on [Student.insertIsolationSerializable] ", exc);

      ml.addErrorMessage("Error inserting Student with " + exc.getMessage());
    } finally {
      if (ml.hasErrorMessages()) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
    }

    return ml;
  }


  @Override
  public void afterPropertiesSet() {
    newTransactionTemplate = new TransactionTemplate(transactionManager);
    newTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
  }
}

