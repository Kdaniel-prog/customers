INSERT INTO role (id, name) VALUES (1, 'USER');
INSERT INTO role (id, name) VALUES (2, 'ADMIN');

INSERT INTO address (zip_code, city, street, house_number, stairs, flat, door)
VALUES ('1051', 'Budapest', 'Szent István körút', 20, 'A', '3', '2'),
       ('4024', 'Debrecen', 'Piac utca', 5, NULL, NULL, NULL),
       ('6720', 'Szeged', 'Kossuth Lajos sugárút', 10, 'B', '1', 'A'),
       ('7621', 'Pécs', 'Király utca', 15, NULL, NULL, NULL);

INSERT INTO customer (username, password, full_name, email, age, address_id, role_id)
VALUES ('jhorvath', 'password1', 'János Horváth', 'j.horvath@example.hu', 35, 1,2),
       ('e.kiss', 'password2', 'Éva Kiss', 'eva.kiss@example.hu', 28, 2,2),
       ('inagy', 'password3', 'István Nagy', 'istvan.nagy@example.hu', 42, 3,1),
       ('b.toth', 'password4', 'Béla Tóth', 'bela.toth@example.hu', 50, 4,1);
