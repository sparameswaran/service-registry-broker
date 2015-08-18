alter table plans drop foreign key FK_cckisotw5asbyai9xy2xnyhn4
alter table plans drop foreign key FK_med4ruw2v39cdl5flp25nglr3
alter table service_bindings drop foreign key FK_mh5u6eqd0ipu9ax2uw2wk5ncu
drop table if exists credentials
drop table if exists plans
drop table if exists service_bindings
drop table if exists service_instances
drop table if exists services
create table credentials (id varchar(255) not null, certFormat varchar(255), certLocation varchar(255), certName varchar(255), password varchar(255), uri varchar(255) not null, username varchar(255), primary key (id))
create table plans (id varchar(255) not null, description varchar(255) not null, name varchar(255) not null, plan_cred_id varchar(255), service_id varchar(255), primary key (id))
create table service_bindings (id varchar(255) not null, appGuid varchar(255) not null, instanceId varchar(255) not null, planId varchar(255) not null, serviceId varchar(255) not null, service_binding_id varchar(255), primary key (id))
create table service_instances (id varchar(255) not null, orgGuid varchar(255) not null, planId varchar(255) not null, serviceId varchar(255) not null, spaceGuid varchar(255) not null, primary key (id))
create table services (id varchar(255) not null, bindable bit not null, description varchar(255) not null, name varchar(255) not null, primary key (id))
alter table plans add constraint FK_cckisotw5asbyai9xy2xnyhn4 foreign key (plan_cred_id) references credentials (id)
alter table plans add constraint FK_med4ruw2v39cdl5flp25nglr3 foreign key (service_id) references services (id)
alter table service_bindings add constraint FK_mh5u6eqd0ipu9ax2uw2wk5ncu foreign key (service_binding_id) references credentials (id)