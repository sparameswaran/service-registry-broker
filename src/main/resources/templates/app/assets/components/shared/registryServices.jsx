var $ = require('jquery');

(function () {

    var baseURL = "http://service-registry-broker.classic.coke.cf-app.com/";

    // The public API
    var RegistryService = {
        findAllServices: function() {
            return $.ajax(baseURL + "/services");
        },
        findServiceById: function(serviceId) {
            return $.ajax(baseURL + "/services/" + serviceId);
        },
        findServiceByName: function(searchKey) {
            return $.ajax({url: baseURL + "/searchService", data: {name: searchKey}});
        },        
        findServiceByProviderName: function(searchKey) {
            return $.ajax({url: baseURL + "/searchServiceByProvider", data: {name: searchKey}});
        },
        getPlansForService: function(serviceId) {
            return $.ajax({url: baseURL + "/services/" + serviceId + "/plans"});
        },
        findPlanById: function(planId) {
            return $.ajax(baseURL + "/plans/" + planId);
        },
        getCredentialsForPlans: function(planId) {
            return $.ajax({url: baseURL + "/credentialsForPlan/" + planId});
        },
        findCredentialsById: function(credId) {
            return $.ajax(baseURL + "/credentials/" + credId);
        }
    };
    
    module.exports = RegistryService;

}());