INSERT INTO role (id, name) VALUES (1, 'USER');
INSERT INTO role (id, name) VALUES (2, 'ADMIN');

INSERT INTO customer (username, password, full_name, email, age, role_id)
VALUES ('jhorvath', 'password1', 'János Horváth', 'j.horvath@example.hu', 35 ,2),
       ('e.kiss', 'password2', 'Éva Kiss', 'eva.kiss@example.hu', 28,2),
       ('inagy', 'password3', 'István Nagy', 'istvan.nagy@example.hu', 42,1),
       ('b.toth', 'password4', 'Béla Tóth', 'bela.toth@example.hu', 50,1);
