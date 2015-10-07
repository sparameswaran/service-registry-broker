registryServices = (function () {

    var baseURL = "";

    // The public API
    return {
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

}());