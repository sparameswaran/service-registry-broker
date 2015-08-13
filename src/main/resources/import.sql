insert into services (id, name, description, bindable) values ('1', 'EDMSRetreiveInterface', 'EDMSContentRetrievalSystem', true)
-- Update the domain information based on CF apps domain
--insert into credentials (id, uri, username, password,  certName, certLocation, certFormat) values ('cred1', 'http://document-service.10.244.0.34.xip.io/soap/RetrieveService', null, null, null, null, null);
--insert into credentials (id, uri) values ('cred2', 'http://gold-document-service.10.244.0.34.xip.io/soap/RetrieveService');
insert into credentials (id, uri, username, password,  certName, certLocation, certFormat) values ('cred1', 'http://document-service.classic.coke.cf-app.com/soap/RetrieveService', null, null, null, null, null);
insert into credentials (id, uri) values ('cred2', 'http://gold-document-service.classic.coke.cf-app.com/soap/RetrieveService');
insert into plans (id, name, description, service_id, plan_cred_id) values ('plan1', 'basic', 'Basic Plan throttled to 5 connections per second','1', 'cred1');
insert into plans (id, name, description, service_id, plan_cred_id) values ('plan2', 'premium', 'Premium Plan throttled to 25 connections per second','1', 'cred2');

insert into services (id, name, description, bindable) values ('2', 'PolicyInterface', 'Policy Retrieval System', true)
--insert into credentials (id, uri, username, password,  certName, certLocation, certFormat) values ('cred3', 'http://policy-service.10.244.0.34.xip.io/soap/RetrieveService', null, null, null, null, null);
--insert into credentials (id, uri) values ('cred4', 'http://gold-policy-service.10.244.0.34.xip.io/soap/RetrieveService');
insert into credentials (id, uri, username, password,  certName, certLocation, certFormat) values ('cred3', 'http://policy-service.classic.coke.cf-app.com/soap/RetrieveService', null, null, null, null, null);
insert into credentials (id, uri) values ('cred4', 'http://gold-policy-service.classic.coke.cf-app.com/soap/RetrieveService');

insert into plans (id, name, description, service_id, plan_cred_id) values ('plan3', 'basic', 'Basic Plan throttled to 5 connections per second','2', 'cred3');
insert into plans (id, name, description, service_id, plan_cred_id) values ('plan4', 'premium', 'Premium Plan throttled to 50 connections','2', 'cred4');

