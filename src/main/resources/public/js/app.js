var parseQueryString = function() {

    var str = window.location.search;
    var objURL = {};

    str.replace(
        new RegExp( "([^?=&]+)(=([^&]*))?", "g" ),
        function( $0, $1, $2, $3 ){
            objURL[ $1 ] = $3;
        }
    );
    return objURL;
};  

var app = angular.module('myApp', []);
  
  /*
  app.config(['$routeProvider', function($routeProvider) {
      $routeProvider
          .when('/', {
              templateUrl: "index.html",
              controller: ServicesController
          })
          .when('/edit', {
              templateUrl: "edit.html",
              controller: EditController,
              resolve: {
                  factory: checkRouting
              }
          })
          .when('/add', {
              templateUrl:"add.html",
              controller: AddController,
              resolve: {
                  factory: checkRouting
              }
          })
          .otherwise({ redirectTo: '/' });
  }]);
  */
  
  app.controller('servicesCtrl', function($rootScope, $http) {
      $http.get("/services")
      .success(function(response) {    	
    	$rootScope.services = response.services;
    	console.log($rootScope.services);
      });
  });
  
  app.controller('gotoPage', function($scope, $http, $location, $window) {
	  
	  $scope.gotoPage = function(page, serviceName) {
		
		var newUrl = $window.location.protocol + "//" + $window.location.host 
						+ "/" + page + ".html?service="+ serviceName;
		
		$window.location = newUrl;
		//alert ("New location is: " + newUrl + " while window location is : " + $window.location);
	    
	  };
	  
	  
  });
  
  app.controller('editServiceDefn', function($scope, $http, $window) {
	  
	  $scope.editServiceDefn = function() {
		  
	  
		  
	  var params = parseQueryString();
  	  var serviceName = params['service'];
	  $http.get("/services/" + serviceName)
      .success(function(response) {
    	  $scope.serviceDefn = response;
    	  
      	});
	  };
	  
	  
  });
  
  app.controller('serviceSearchCtrl', function($scope, $http) {
	  
	  $scope.submit = function() {		  
	      $http.get("/search?name=" + $scope.name )
	      .success(function(response) {
	    	console.log($scope.serviceNames);
	    	  $scope.serviceNames = response.serviceNames;    	  
	      });

	  };
  });
  
  
  app.controller('getCredentialsForPlanCtrl', function($scope, $http) {
	  console.log("Call into getCreds");
	  $scope.showcreds=true;
	  $scope.getCreds = function(id) {
		  console.log(id);
	      $http.get("/getCredentialsForPlan?planId=" + id )
	      .success(function(response) {
	    	console.log(response);
	    	$scope.credentials = [ response.credentials ];    	
	    	console.log($scope.credentials);
	    	console.log($scope.credentials[0].uri);
	      });

	  };
  });
  
  /*
  app.config(function($stateProvider, $urlRouterProvider) {
	  $stateProvider

	    .state('edit', {
	      url: "/edit",
	      views: {
	    	  'edit': {
	    	  templateUrl: 'edit.html',
	    	  controller: 'addServiceCtrl'
	    	}
	      }
	    })

	    .state('add', {
	      url: '/add',
	      views: {
	        'add': {
	          templateUrl: 'add.html',
	          controller: 'addServiceCtrl'
	        },
	      }
    })
  });
  

  var checkRouting = function ($q, $rootScope, $location) {
	    if ($rootScope.userProfile) {
	        return true;
	    } else {
	        var deferred = $q.defer();
	        $http.post("/loadUserProfile", { userToken: "blah" })
	            .success(function (response) {
	                $rootScope.userProfile = response.userProfile;
	                deferred.resolve(true);
	            })
	            .error(function () {
	                deferred.reject();
	                $location.path("/");
	             });
	        return deferred.promise;
	    }
	};
  
  */