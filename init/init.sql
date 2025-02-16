--db initialization script
CREATE TABLE student (
  id SERIAL PRIMARY KEY,
  name VARCHAR(10),
  age INTEGER
);

INSERT INTO student (name, age) VALUES
  ('Phantom', 99);


CREATE TABLE student_log (
  id SERIAL PRIMARY KEY,
  description VARCHAR(50)
);