
DROP TABLE IF EXISTS images;
DROP TABLE IF EXISTS detected_images;

create table images(
id int auto_increment,
label varchar(50) null,
file_name varchar(50) null,
type varchar(50) null,
file_size long null,
object_detection_enabled boolean null,
#file_data MEDIUMBLOB NULL,
primary key (id)
);

create table detected_images(
id int auto_increment,
label varchar(30) null,
image_id int null,
constraint detected_images
primary key (id)
);

INSERT INTO images (label, object_detection_enabled, file_name, type, file_size ) VALUES ('apples', true, 'morning breakfast.jpeg', 'jpeg', 523456.77);
INSERT INTO images (label, object_detection_enabled, file_name, type, file_size) VALUES ('bacon', false, 'dinner.jpeg', 'jpeg', 87654.348);

INSERT INTO detected_images (id, label, image_id) VALUES (1, 'apple', 1);
INSERT INTO detected_images (id, label, image_id) VALUES (2, 'spoon', 1);
INSERT INTO detected_images (id, label, image_id) VALUES (3, 'chicken', 2);
INSERT INTO detected_images (id, label, image_id) VALUES (4, 'rice', 2);