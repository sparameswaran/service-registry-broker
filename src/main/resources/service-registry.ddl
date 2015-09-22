alter table creds_other_attributes drop foreign key FK_84s4e8wcvjusoqqtrtpkqv1ec;
alter table plan_metadata_bullets drop foreign key FK_ne0tydfhi3t83iywwb78j0t3w;
alter table plans drop foreign key FK_cckisotw5asbyai9xy2xnyhn4;
alter table plans drop foreign key FK_atb3b4gjfq9j9u6vafa93qdhp;
alter table plans drop foreign key FK_med4ruw2v39cdl5flp25nglr3;
alter table service_bindings drop foreign key FK_mh5u6eqd0ipu9ax2uw2wk5ncu;
alter table services drop foreign key FK_gl9268i32nx9sjt94fs6258eg;

drop table if exists credentials;
drop table if exists creds_other_attributes;
drop table if exists plan_metadata_bullets;
drop table if exists planmetadata;
drop table if exists plans;
drop table if exists service_bindings;
drop table if exists service_instances;
drop table if exists servicemetadata;
drop table if exists services;

create table credentials (id integer not null auto_increment, password varchar(255), uri varchar(255), username varchar(255), primary key (id));
create table creds_other_attributes (creds_other_attrib_id integer not null, value varchar(255), name varchar(255) not null, primary key (creds_other_attrib_id, name));
create table plan_metadata_bullets (plan_metadata_id integer not null, bullets varchar(255));
create table planmetadata (id integer not null auto_increment, primary key (id));
create table plans (id varchar(255) not null, description varchar(255) not null, is_free bit, name varchar(255), plan_cred_id integer, metadata_id integer, service_id varchar(255), primary key (id));
create table service_bindings (id varchar(255) not null, app_guid varchar(255) not null, instance_id varchar(255) not null, plan_id varchar(255) not null, service_id varchar(255) not null, service_binding_id integer, primary key (id));
create table service_instances (id varchar(255) not null, org_guid varchar(255) not null, plan_id varchar(255) not null, service_id varchar(255) not null, space_guid varchar(255) not null, primary key (id));
create table servicemetadata (id integer not null auto_increment, display_name varchar(255) not null, documentation_url tinyblob, image_url tinyblob, long_description varchar(255), provider_display_name varchar(255) not null, support_url tinyblob, primary key (id));
create table services (id varchar(255) not null, bindable bit not null, description varchar(255) not null, name varchar(255) not null, metadata_id integer, primary key (id));

alter table plans add constraint UK_cckisotw5asbyai9xy2xnyhn4  unique (plan_cred_id);
alter table creds_other_attributes add constraint FK_84s4e8wcvjusoqqtrtpkqv1ec foreign key (creds_other_attrib_id) references credentials (id);
alter table plan_metadata_bullets add constraint FK_ne0tydfhi3t83iywwb78j0t3w foreign key (plan_metadata_id) references planmetadata (id);
alter table plans add constraint FK_cckisotw5asbyai9xy2xnyhn4 foreign key (plan_cred_id) references credentials (id);
alter table plans add constraint FK_atb3b4gjfq9j9u6vafa93qdhp foreign key (metadata_id) references planmetadata (id);
alter table plans add constraint FK_med4ruw2v39cdl5flp25nglr3 foreign key (service_id) references services (id);
alter table service_bindings add constraint FK_mh5u6eqd0ipu9ax2uw2wk5ncu foreign key (service_binding_id) references credentials (id);
alter table services add constraint FK_gl9268i32nx9sjt94fs6258eg foreign key (metadata_id) references servicemetadata (id);


-- select   TABLE_NAME,COLUMN_NAME,CONSTRAINT_NAME, REFERENCED_TABLE_NAME,REFERENCED_COLUMN_NAME from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where   REFERENCED_TABLE_NAME = 'credentials';
