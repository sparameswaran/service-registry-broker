insert into servicemetadata(id, providerDisplayName, displayName) values(1,'Test Provider Inc.', 'Test Provider')
insert into services (name, description, bindable, metadata_id) values ('EDMSRetreiveInterface', 'EDMSContentRetrievalSystem', true, 1)

insert into credentials (id, uri, username, password) values (1, 'http://document-service.classic.coke.cf-app.com/soap/RetrieveService', null, null);
insert into credentials (id, uri)                     values (2, 'http://gold-document-service.classic.coke.cf-app.com/soap/RetrieveService');

insert into planmetadata (id) values(1)
insert into planmetadata (id) values(2)

insert into PlanMetadata_bullets (PlanMetadata_id, bullets) values (1, 'Free, SOAP Service')
insert into PlanMetadata_bullets (PlanMetadata_id, bullets) values (2, 'Paid, premium SOAP Service')


insert into plans (name, description, service_id, plan_cred_id, metadata_id, isFree) values ('basic', 'Basic Plan throttled to 5 connections per second','EDMSRetreiveInterface', 1, 1, 1);
insert into plans (name, description, service_id, plan_cred_id, metadata_id, isFree) values ('premium', 'Premium Plan throttled to 25 connections per second','EDMSRetreiveInterface', 2, 2, 0);



insert into servicemetadata(id, providerDisplayName, displayName) values(2,'Service Provider Inc.', 'Service Provider')
insert into services (name, description, bindable, metadata_id) values ('PolicyInterface', 'Policy Retrieval System', true, 2)

insert into credentials (id, uri, username, password) values (3, 'http://policy-service.classic.coke.cf-app.com/soap/RetrieveService', null, null);
insert into credentials (id, uri) values (4, 'http://gold-policy-service.classic.coke.cf-app.com/soap/RetrieveService');

insert into planmetadata (id) values(3)
insert into planmetadata (id) values(4)

insert into PlanMetadata_bullets (PlanMetadata_id, bullets) values (3, 'Free, SOAP Service')
insert into PlanMetadata_bullets (PlanMetadata_id, bullets)  values (4, 'Paid, premium SOAP Service')

insert into plans (name, description, service_id, plan_cred_id, metadata_id, isFree) values ('basic', 'Basic Plan throttled to 5 connections per second','PolicyInterface',3, 3, 1);
insert into plans (name, description, service_id, plan_cred_id, metadata_id, isFree) values ('premium', 'Premium Plan throttled to 50 connections','PolicyInterface', 4, 4, 0);

