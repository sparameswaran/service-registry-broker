/** @jsx React.DOM */
var React = require('react');
var Router = require('react-router');
var _ = require('lodash');

var RegistryServices = require('../shared/registryServices.jsx');
var CredentialsEditor = require('./credentialsEditor.jsx');
var TagsEditor = require('./tagsEditor.jsx');


(function () {
  'use strict';
  
 
  var EditPlan = React.createClass({
	
	getInitialState: function() {
	    var jsonPayload = {};
	    var space = '\t';
	    
	    var planId = this.props.params.planId;
	    
	    console.log("Current props includes: ", this.props);
   	    console.log("Current state includes: ", this.state);
   	    
	    this.setState({ planId : this.props.params['planId'] });
	    
   	     
   	    var planName = '';
   	    var planDescrp = '';         
        var cost = 0.0;
        var units = 'MONTHLY';
        var currency = 'usd';         
        var rawTagList = [];
        var tagList = [];
        var serviceId;
        
        // If we are here for editing of a plan
        // then use the available data...
        if (  (typeof(planId) != "undefined") &&  (typeof(this.props.services) != "undefined") ) {
        

		  var serviceEntry = _.filter(this.props.services, {plans: [ { id : planId } ]  })[0];
 	      serviceId = serviceEntry.id;
   		  this.setState({ serviceId : serviceId });	       
		  
		  console.log("Found matching service Entry: ", serviceEntry.plans);
		  var planEntry = _.filter(serviceEntry.plans,  { id : planId })[0];		    
		  console.log("Found matching plan Entry: ", planEntry);
          
          this.setState( { plan : planEntry });
          
	   	  planName = planEntry.name;
	   	  planDescrp = planEntry.description;
	       
	      for (var key in planEntry.metadata.cost.amount) {
	      	currency = key;
	      }
	      
	      units = planEntry.metadata.cost.unit;
	      cost = parseFloat(planEntry.metadata.cost.amount[currency]);
	      rawTagList = planEntry.metadata.bullets;
	      
	      console.log("Edit Plan, currency: " , currency, " and cost is: ", cost);	
	      
          if ( typeof(rawTagList) != "undefined" ) {
        	tagList = [ ];
		        for(var i = 0; i < rawTagList.length; i++) {
	    			tagList.push( { cvalue: rawTagList[i] } );
			    }
	      }						    
			 console.log("Updated tag list: " ,  tagList);					        
        
        } else {
        
          // This means this is for a brand new plan..
          // Grab the service id so we can use that for submission later..
          serviceId = this.props.params['serviceId'];
   		  this.setState({ serviceId : serviceId });	 
        }
	    
	    
	    
	    return {	planId: planId, 
	    			serviceId : serviceId, 
	    			planName: planName, 
	    			planDescrp: planDescrp, 
	    			cost: cost, 
	    			units: units, 
	    			currency: currency,
	    			tagList : tagList  
	    		}; 
	  },
	  
	  handleSubmit: function(event) {
	    
	    console.log("Got handleSubmitRow! with state: ", this.state);

	    // Get values via this.refs
	    var planPayload = {
	      name     : this.refs.name.getDOMNode().value,
	      description : this.refs.description.getDOMNode().value,
	      free    : this.refs.free.getDOMNode().checked,
	    }
	    
	    var cost     = parseInt(this.refs.cost.getDOMNode().value);
	    var units    = this.refs.units.getDOMNode().value;
	    var currencyStr = this.refs.currency.getDOMNode().value;

        var amount = { };
        amount[ currencyStr] = cost;
        amount.toString = function() {
           return { currencyStr : cost };
        }
        
        planPayload['metadata'] =  {  
                                cost: { 
                                     amount, 
                                     unit: units 
                                 } 
                             };   
                                  
	    planPayload['credentials'] = this.refs.credentials.toString() ;
		planPayload['metadata']['bullets'] =  this.refs.tags.toString();
	    console.log("Got handleSubmit data: ", planPayload);
	    console.log("Got handleSubmit data JSON: ", JSON.stringify(planPayload));
	    
	    var serviceId = this.state.serviceId;
	    var planId = this.state.planId;
	    
	    if ( typeof(planId) == "undefined") {	    
	    	
	    	console.log("Going to do an add of plan to service of the payload and redirect to: ", serviceId);
	    	
	    	
	    	RegistryServices.addPlanToService(serviceId, JSON.stringify(planPayload));
	       
	    	var router = Router.create({});
	    	router.transitionTo('/service/' + serviceId);
	    	
	    	
	    } else {
	        
	        console.log("Going to do an edit of the plan with the payload and redirect to: ", serviceId);
	    	
	    	RegistryServices.editPlan(planId, JSON.stringify(planPayload));
	       
	    	var router = Router.create({});
	    	router.transitionTo('/service/' + serviceId);
	    	
	    }
	  },
	  
	  render: function() {
	    var space = '  ';
	    
	    console.log("Inside render: Current state includes: ", this.state);
         
      return (
            
         <div className="page-title bg-neutral-11 pvxl">
          <div className="container">          
              <div className="media-body">
			          
                    <p className='h1 type-dark-1 mvn em-low'>Plan Editor</p>
                    
 			        <form id="outerForm" role="form">
			          
			          
			            <label for="name"><h4>Plan Name</h4></label>
			            <input className="form-control"  type="text" ref="name" placeholder="Enter Plan Name" defaultValue={this.state.planName} />
			            <br/>
			            <label for="description"><h4>Plan Description</h4></label>
			            <input className="form-control"  type="text" ref="description" placeholder="Enter Plan Description" defaultValue={this.state.planDescrp} />
			            <br/>
			            <label>
			                <div>
			            	<input className="form-control"  type="checkbox" ref="free" /> 
			            	</div>
			            	Free Plan 
			            </label>
			            	
			            <br/><br/>
			            <label for="costs" >
			            <h4>Costs </h4>
			            <div align="left">
			            
			                <div className="input-group " key="amount">
			                <h5>Amount (in double)</h5>
			                <input className="form-control" defaultValue={this.state.cost} type="double" ref="cost" />			                
			            	</div>
			            	
			            	<div className="input-group" key="currency">
			            	<label> <h5> Currency</h5> </label>
			            	<input className="form-control" defaultValue={this.state.currency} type="text" ref="currency" /> 
			            	</div>
			            	
			            	<div className="input-group" key="units">
			            	<h5> Units </h5> <input className="form-control" placeholder="select"  defaultValue={this.state.units}  list="units" ref="units"/>
				            	<datalist id="units">
								    <option value="WEEKLY"/>
								    <option value="MONTHLY"/>
								    <option value="YEARLY"/>
								  </datalist>
			            	</div>
			            </div>
			            </label>
			            <br/>
			        
			        
			        
			            <TagsEditor tags={this.state.tagList} ref="tags"/>
						<br/>
			            <CredentialsEditor planId={this.state.planId} ref="credentials"/>
			            <br/>
			            
			        <div align="left">	
			        <button id="submit" className="btn btn-primary" onClick={this.handleSubmit} >Submit</button> 
			        </div>
			        				            
			        </form>
					<br/><br/>
							     
	            
		            
		            
		          </div>      
		        </div>
		        </div>

	  );
	 
  }

} );

module.exports = EditPlan;

}());



