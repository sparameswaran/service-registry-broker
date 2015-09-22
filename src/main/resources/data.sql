insert into servicemetadata(id, provider_display_name, display_name) values(1,'Test Provider Inc.', 'Test Provider');
insert into services (name, id, description, bindable, metadata_id) values ('EDMSRetreiveInterface', '3745cb31-64c7-3ba0-a3a8-374c1fe38d98', 'EDMSContentRetrievalSystem', true, 1);

insert into credentials (id, uri, username, password) values (1, 'http://document-service.classic.coke.cf-app.com/soap/RetrieveService', null, null);
insert into credentials (id, uri)                     values (2, 'http://gold-document-service.classic.coke.cf-app.com/soap/RetrieveService');

insert into planmetadata (id) values(1);
insert into planmetadata (id) values(2);

insert into plan_metadata_bullets (plan_metadata_id, bullets) values (1, 'Free, SOAP Service');
insert into plan_metadata_bullets (plan_metadata_id, bullets) values (2, 'Paid, premium SOAP Service');


insert into plans (name, id, description, service_id, plan_cred_id, metadata_id, is_free) values ('basic', '0ab7a335-b495-3009-ad14-40b06919bf12', 'Basic Plan throttled to 5 connections per second', '3745cb31-64c7-3ba0-a3a8-374c1fe38d98', 1, 1, 1);
insert into plans (name, id, description, service_id, plan_cred_id, metadata_id, is_free) values ('premium', '65040368-a981-31ac-962d-e09d8331a4b8', 'Premium Plan throttled to 25 connections per second', '3745cb31-64c7-3ba0-a3a8-374c1fe38d98', 2, 2, 0);



insert into servicemetadata(id, provider_display_name, display_name) values(2,'Service Provider Inc.', 'Service Provider');
insert into services (name, id, description, bindable, metadata_id) values ('PolicyInterface', '2896b732-4587-386a-9a5e-3bde75e57df3', 'Policy Retrieval System', true, 2);

insert into credentials (id, uri, username, password) values (3, 'http://policy-service.classic.coke.cf-app.com/soap/RetrieveService', null, null);
insert into credentials (id, uri) values (4, 'http://gold-policy-service.classic.coke.cf-app.com/soap/RetrieveService');

insert into planmetadata (id) values(3);
insert into planmetadata (id) values(4);

insert into plan_metadata_bullets (plan_metadata_id, bullets) values (3, 'Free, SOAP Service');
insert into plan_metadata_bullets (plan_metadata_id, bullets)  values (4, 'Paid, premium SOAP Service');

insert into plans (name, id, description, service_id, plan_cred_id, metadata_id, is_free) values ('basic', '33febe21-64d6-39f8-aafa-e102e145a98a', 'Basic Plan throttled to 5 connections per second', '2896b732-4587-386a-9a5e-3bde75e57df3',3, 3, 1);
insert into plans (name, id, description, service_id, plan_cred_id, metadata_id, is_free) values ('premium', 'fa3d189a-6298-3089-8c1f-e9144b48e16c', 'Premium Plan throttled to 50 connections', '2896b732-4587-386a-9a5e-3bde75e57df3', 4, 4, 0);



