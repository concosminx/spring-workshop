package com.nimsoc.workshop.objects;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Student {
  private Integer id;
  private String name;
  private Integer age;
}
