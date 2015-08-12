insert into services (id, name, description, bindable) values ('1', 'EDMSRetreiveInterface', 'EDMSContentRetrievalSystem', true)
insert into plans (id, name, description, service_id) values ('1', 'basic', 'Basic Plan throttled to 5 connections per second','1');
insert into plans (id, name, description, service_id) values ('2', 'premium', 'Premium Plan throttled to 25 connections per second','1');
insert into credentials (id, plan_id, uri, username, password,  certName, certLocation, certFormat) values ('cred1', '1', 'http://document-service.10.244.0.34.xip.io/soap/RetrieveService', null, null, null, null, null);
insert into credentials (id, plan_id, uri) values ('cred2', '2', 'http://gold-document-service.10.244.0.34.xip.io/soap/RetrieveService');
insert into services (id, name, description, bindable) values ('2', 'PolicyInterface', 'Policy Retrieval System', true)
insert into plans (id, name, description, service_id) values ('3', 'basic', 'Basic Plan throttled to 5 connections per second','2');
insert into plans (id, name, description, service_id) values ('4', 'premium', 'Premium Plan throttled to 50 connections','2');
insert into credentials (id, plan_id, uri, username, password,  certName, certLocation, certFormat) values ('cred3', '3', 'http://policy-service.10.244.0.34.xip.io/soap/RetrieveService', null, null, null, null, null);
insert into credentials (id, plan_id, uri) values ('cred4', '4', 'http://gold-policy-service.10.244.0.34.xip.io/soap/RetrieveService');

