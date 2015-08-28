alter table PlanMetadata_bullets drop foreign key FK_oerf61ppqc6gvj4q5ky571d37;
alter table creds_other_attributes drop foreign key FK_84s4e8wcvjusoqqtrtpkqv1ec;
alter table plans drop foreign key FK_cckisotw5asbyai9xy2xnyhn4;
alter table plans drop foreign key FK_atb3b4gjfq9j9u6vafa93qdhp;
alter table plans drop foreign key FK_med4ruw2v39cdl5flp25nglr3;
alter table service_bindings drop foreign key FK_mh5u6eqd0ipu9ax2uw2wk5ncu;
alter table services drop foreign key FK_gl9268i32nx9sjt94fs6258eg;


drop table if exists PlanMetadata_bullets;
drop table if exists credentials;
drop table if exists creds_other_attributes;
drop table if exists planmetadata;
drop table if exists plans;
drop table if exists service_bindings;
drop table if exists service_instances;
drop table if exists servicemetadata;
drop table if exists services;



create table PlanMetadata_bullets (PlanMetadata_id integer not null, bullets varchar(255));
create table credentials (id integer not null auto_increment, password varchar(255), uri varchar(255), username varchar(255), primary key (id));
create table creds_other_attributes (creds_other_attrib_id integer not null, value varchar(255), name varchar(255) not null, primary key (creds_other_attrib_id, name));
create table planmetadata (id integer not null auto_increment, primary key (id));
create table plans (id varchar(255) not null, description varchar(255) not null, isFree bit, name varchar(255), serviceName varchar(255) not null, plan_cred_id integer, metadata_id integer, service_id varchar(255), primary key (id));
create table service_bindings (id varchar(255) not null, appGuid varchar(255) not null, instanceId varchar(255) not null, planId varchar(255) not null, serviceId varchar(255) not null, service_binding_id integer, primary key (id));
create table service_instances (id varchar(255) not null, orgGuid varchar(255) not null, planId varchar(255) not null, serviceId varchar(255) not null, spaceGuid varchar(255) not null, primary key (id));
create table servicemetadata (id integer not null auto_increment, displayName varchar(255) not null, documentationUrl tinyblob, imageUrl tinyblob, longDescription varchar(255), providerDisplayName varchar(255) not null, supportUrl tinyblob, primary key (id));
create table services (id varchar(255) not null, bindable bit not null, description varchar(255) not null, name varchar(255) not null, metadata_id integer, primary key (id));



alter table PlanMetadata_bullets add constraint FK_oerf61ppqc6gvj4q5ky571d37 foreign key (PlanMetadata_id) references planmetadata (id);
alter table creds_other_attributes add constraint FK_84s4e8wcvjusoqqtrtpkqv1ec foreign key (creds_other_attrib_id) references credentials (id);
alter table plans add constraint FK_cckisotw5asbyai9xy2xnyhn4 foreign key (plan_cred_id) references credentials (id);
alter table plans add constraint FK_atb3b4gjfq9j9u6vafa93qdhp foreign key (metadata_id) references planmetadata (id);
alter table plans add constraint FK_med4ruw2v39cdl5flp25nglr3 foreign key (service_id) references services (id);
alter table service_bindings add constraint FK_mh5u6eqd0ipu9ax2uw2wk5ncu foreign key (service_binding_id) references credentials (id);
alter table services add constraint FK_gl9268i32nx9sjt94fs6258eg foreign key (metadata_id) references servicemetadata (id);
