package com.nimsoc.workshop.controller;

import com.nimsoc.workshop.objects.Student;
import com.nimsoc.workshop.service.StudentLogService;
import com.nimsoc.workshop.service.StudentService;
import com.nimsoc.workshop.utils.MessageList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

  private final StudentService studentService;
  private final StudentLogService studentLogService;

  @GetMapping
  public Map<String, Object> getAll() {
    return Map.of("students", studentService.getAll(), "logs", studentLogService.getAll());
  }

  @PostMapping(path = "/insertNonTransactional", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public MessageList insertNonTransactional(@RequestBody List<Student> list) {
    studentService.insertNonTransactional(list);
    return new MessageList();
  }

  @PostMapping(path = "/insertTransactionalRequired", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public MessageList insertTransactionalRequired(@RequestBody List<Student> list) {
    return studentService.insertTransactionalRequired(list);
  }

  @PostMapping(path = "/insertTransactionalRequiresNew", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public MessageList insertTransactionalRequiresNew(@RequestBody List<Student> list) {
    return studentService.insertTransactionalRequiresNew(list);
  }

  @PostMapping(path = "/insertTransactionalSupports", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public MessageList insertTransactionalSupports(@RequestBody List<Student> list) {
    studentService.insertTransactionalSupports(list);
    return new MessageList();
  }

  @PostMapping(path = "/insertTransactionalMandatory", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public MessageList insertTransactionalMandatory(@RequestBody List<Student> list) {
    studentService.insertTransactionalMandatory(list);
    return new MessageList();
  }

  @PostMapping(path = "/insertTransactionalNever", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public MessageList insertTransactionalNever(@RequestBody List<Student> list) {
    studentService.insertTransactionalNever(list);
    return new MessageList();
  }

  @PostMapping(path = "/insertTransactionalNotSupported", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public MessageList insertTransactionalNotSupported(@RequestBody List<Student> list) {
    studentService.insertTransactionalNotSupported(list);
    return new MessageList();
  }

  @PostMapping(path = "/insertTransactionalNested", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public MessageList insertTransactionalNested(@RequestBody List<Student> list) {
    return studentService.insertTransactionalNested(list);
  }

  @PostMapping(path = "/insertTransactionalSelfInvocation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public MessageList insertTransactionalSelfInvocation(@RequestBody List<Student> list) {
    return studentService.insertTransactionalSelfInvocation(list);
  }

  @PostMapping(path = "/insertTransactionTemplate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public MessageList insertTransactionTemplate(@RequestBody List<Student> list) {
    return studentService.insertTransactionTemplate(list);
  }

  @PostMapping(path = "/insertIsolationReadUncommitted", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public MessageList insertIsolationReadUncommitted(@RequestBody List<Student> list) {
    return studentService.insertIsolationReadUncommitted(list);
  }

  @PostMapping(path = "/insertIsolationReadCommitted", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public MessageList insertIsolationReadCommitted(@RequestBody Student student) {
    return studentService.insertIsolationReadCommitted(student);
  }

  @PostMapping(path = "/insertIsolationRepeatableRead", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public MessageList insertIsolationRepeatableRead(@RequestBody List<Student> list) {
    return studentService.insertIsolationRepeatableRead(list);
  }

  @PostMapping(path = "/insertIsolationSerializable", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public MessageList insertIsolationSerializable(@RequestBody List<Student> list) {
    return studentService.insertIsolationSerializable(list);
  }

}
