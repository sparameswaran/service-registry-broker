var $ = require('jquery');

(function () {

    //var baseURL = "http://localhost:8080";
    var baseURL = "";

    var user   = "testuser";
    var passwd = "testuser";
    
    
    function make_base_auth(user, password) {
	  var tok = user + ':' + password;
	  var hash = btoa(tok);
	  return "Basic " + hash;
	}


    // The public API
    var RegistryService = {
        findAllServices: function() {
           return $.ajax( baseURL + "/services");            
        },
        findServiceById: function(serviceId) {
            return $.ajax(baseURL + "/services/" + serviceId);
        },
        findServiceByName: function(searchKey) {
            return $.ajax({url: baseURL + "/searchService?", data: {name: searchKey}});
        },        
        findServiceByProviderName: function(searchKey) {
            return $.ajax({url: baseURL + "/searchServiceByProvider?", data: {name: searchKey}});
        },
        getPlansForService: function(serviceId) {
            return $.ajax({url: baseURL + "/services/" + serviceId + "/plans"});
        },
        findPlanById: function(planId) {
            return $.ajax(baseURL + "/plans/" + planId);
        },
        getCredentialsForPlans: function(planId) {
            return $.ajax({url: baseURL + "/credentialsForPlan",  data: {planId: planId}});
        },
        findCredentialsById: function(credId) {
            return $.ajax(baseURL + "/credentials/" + credId);
        },
        postServices: function(servicesPayload) {
            return $.ajax( { 
            url: baseURL + "/services", 
            type: "POST", 
            headers: { 
			        'Accept': 'application/json',
			        'Content-Type': 'application/json' 
			    },
			dataType: 'json',    
			data:  servicesPayload });
        },
        editService: function(serviceId, servicePayload) {
            return $.ajax( { 
            url: baseURL + "/services/" + serviceId, 
            type: "PATCH",
            async: false, 
            headers: { 
			        'Accept': 'application/json',
			        'Content-Type': 'application/json' 
			    },
			dataType: 'json',    
			data:  servicePayload,     
			success: function() {
		        console.log("Finished updating service...");
		    }
		    });
        },
        deleteService: function(serviceId) {
            return $.ajax( { 
            url: baseURL + "/services/" + serviceId, 
            type: "DELETE",
            async: false, 
            headers: { 
			        'Accept': 'application/json',
			        'Content-Type': 'application/json' 
			    },
			success: function() {
		        console.log("Finished deleting...");
		    },
		    error: function(xhr, status, error) {
		      console.info ("Got error on delete, xhr: ", xhr, 'status: ', status, 'error: ', error);
			}
			
			});
        },
        addPlanToService: function(serviceId, planPayload) {
            return $.ajax( { 
            url: baseURL + '/services/' + serviceId + '/plans', 
            type: "PUT",
            async: false,  
            headers: { 
			        'Accept': 'application/json',
			        'Content-Type': 'application/json' 
			    },
			dataType: 'json',    
			data:  planPayload });
        },
        editPlan: function(planId, planPayload) {
            return $.ajax( { 
            url: baseURL + "/plans/" + planId, 
            type: "PATCH",
            async: false, 
            headers: { 
			        'Accept': 'application/json',
			        'Content-Type': 'application/json' 
			    },
			dataType: 'json',    
			data:  planPayload,     
			success: function() {
		        console.log("Finished updating plan...");
		    }
		    });
        },        
        deletePlan: function(planId) {
            return $.ajax( { 
            url: baseURL + "/plans/" + planId, 
            type: "DELETE",
            async: false, 
            headers: { 
			        'Accept': 'application/json',
			        'Content-Type': 'application/json' 
			    },
			success: function() {
		        console.log("Finished deleting plan...");
		    },
		    error: function(xhr, status, error) {
		      console.info ("Got error on delete, xhr: ", xhr, 'status: ', status, 'error: ', error);
			}
			
			});
        }
    };
    
    module.exports = RegistryService;

}());

