package com.nimsoc.workshop.service;

import com.nimsoc.workshop.utils.MessageList;
import com.nimsoc.workshop.objects.Student;

import java.util.List;

public interface StudentService {

  /**
   * Loads all existing students from database
   * @return
   */
  List<Student> getAll();

  /**
   * Helper method to insert a student
   * @param s
   */
  void insertAStudent(Student s);

  /**
   * Demo for not using @Transactional
   * @param students
   */
  void insertNonTransactional(List<Student> students);


  /**
   * Demo for @Transactional with default propagation REQUIRED
   * @param students
   */
  MessageList insertTransactionalRequired(List<Student> students);

  /**
   * Demo for @Transactional with default propagation REQUIRES NEW
   * @param students
   */
  MessageList insertTransactionalRequiresNew(List<Student> students);


  /**
   * Demo for @Transactional with default propagation SUPPORTS
   * @param students
   */
  void insertTransactionalSupports(List<Student> students);

  /**
   * Demo for @Transactional with default propagation MANDATORY
   * @param students
   */
  void insertTransactionalMandatory(List<Student> students);

  /**
   * Demo for @Transactional with default propagation NEVER
   * @param students
   */
  void insertTransactionalNever(List<Student> students);

  /**
   * Demo for @Transactional with default propagation NOT SUPPORTED
   * @param students
   */
  void insertTransactionalNotSupported(List<Student> students);

  /**
   * Demo for @Transactional with default propagation NESTED
   * @param students
   */
  MessageList insertTransactionalNested(List<Student> students);

  /**
   * Demo for @Transactional with self invocation
   * @param students
   */
  MessageList insertTransactionalSelfInvocation(List<Student> students);

  /**
   * Demo for Transaction Template
   * @param students
   */
  MessageList insertTransactionTemplate(List<Student> students);

  /**
   * Demo for READ_UNCOMMITTED Isolation
   * @param students
   */
  MessageList insertIsolationReadUncommitted(List<Student> students);

  /**
   * Demo for READ_COMMITTED Isolation
   * @param student
   */
  MessageList insertIsolationReadCommitted(Student student);

  /**
   * Demo for REPEATABLE_READ Isolation
   * @param students
   */
  MessageList insertIsolationRepeatableRead(List<Student> students);


  /**
   * Demo for SERIALIZABLE Isolation
   * @param students
   */
  MessageList insertIsolationSerializable(List<Student> students);

}
