package com.nimsoc.workshop.service;

import com.nimsoc.workshop.objects.StudentLog;
import com.nimsoc.workshop.utils.MessageList;

import java.util.List;

public interface StudentLogService {

  /**
   * Load all the logs
   * @return
   */
  List<StudentLog> getAll();

  /**
   * Inserts a log entry without using @Transactional
   *
   * @param description
   */
  void insertNonTransactional(String description);


  /**
   * Inserts a log entry using @Transactional with default propagation - REQUIRED
   *
   * @param description
   */
  MessageList insertTransactionalRequired(String description);

  /**
   * Inserts a log entry using @Transactional with propagation - REQUIRES NEW
   *
   * @param description
   */
  MessageList insertTransactionalRequiresNew(String description);

  /**
   * Inserts a log entry using @Transactional with propagation - SUPPORTS
   *
   * @param description
   */
  void insertTransactionalSupports(String description);

  /**
   * Inserts a log entry using @Transactional with propagation - MANDATORY
   *
   * @param description
   */
  void insertTransactionalMandatory(String description);

  /**
   * Inserts a log entry using @Transactional with propagation - NEVER
   *
   * @param description
   */
  void insertTransactionalNever(String description);

  /**
   * Inserts a log entry using @Transactional with propagation - NEVER
   *
   * @param description
   */
  void insertTransactionalNotSupported(String description);

  /**
   * Inserts a log entry using @Transactional with propagation - NESTED
   *
   * @param description
   */
  MessageList insertTransactionalNested(Integer id, String description);

}
