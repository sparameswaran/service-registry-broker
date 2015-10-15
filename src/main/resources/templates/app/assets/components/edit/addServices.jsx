/** @jsx React.DOM */
var React = require('react');
var sampleService = '/sampleService.json';
var _ = 'lodash';
var $ = require('jquery');

var RegistryServices = require('../shared/registryServices.jsx');


var sampleData = require('./sampleService.json');


(function () {
  'use strict';
  
  var AddServices = React.createClass({
	
	getInitialState: function() {
	    var jsonPayload = {};
	    var space = '\t';
	    
	    console.log("sample data contains: ", sampleData); 
	    return { value: sampleData };
	  },
	  
	  handleChange: function(event) {
	    this.setState({value: event.target.value});
	    
	    console.log("Inside handleChange with event: ", event);
	    
	  },
	  
	  handleSubmit: function(event) {
	    console.log("Got handle submit with : ", this.state.rawValue);
	    
	    var servicesPayload = this.state.rawValue;
	    
	    RegistryServices.postServices(this.state.rawValue);
	    
	  },
	  
     updateJsonFormValue: function( nextValue ) {
     
        this.setState( {value: nextValue} );
        
        console.log("Updated data onJsonFormUpdate: ", nextValue);
     },
     
     updateValue: function( nextValue ) {
     
        this.setState( {rawValue: nextValue.target.value} );
        
        console.log("Updated data onUpdate: ", nextValue.target.value);
     },

	  render: function() {
	    var space = '  ';
	    var value = this.state.value;

	    if (typeof(value) == "undefined") 
	    	value = sampleData;

	    var rawValue = this.state.rawValue;

	    if (typeof(rawValue) == "undefined") 
	    	rawValue = JSON.stringify(this.state.value, undefined, space);;

	    //console.log("Value in state: ", value);
	    //console.log("Raw Value in state: ", rawValue);	    
	    	
	    var startRawValue = JSON.stringify(this.state.value, undefined, space);
	    	
	    return (
	          
			    <div className="page-title bg-neutral-11 pvxl">
		          <div className="container">
		            <div className="media">
		              <div className="media-body">
			          	<h4 class="aligner txt-c" className="media-heading em-default txt-m type-dark-1">Add Service definitions</h4>
			          	
			          	
		                <p className='mvn type-sm em-default type-dark-5'>Modify the JSON template below...!!</p>
		                <form class="styleguide-form" role="form" className="jsonSubmit" onSubmit={this.handleSubmit}>
		                
		                <div class="form-group">
		                	<textarea rows="60" cols="80" value={ rawValue } onChange= { this.updateValue }/>
		                 </div>	
		                 <br/> 
		                 <button type="submit" class="btn btn-default">Submit</button>
						</form>
		              </div>
		            </div>
		          </div>      
		        </div>
       
        );
	    
	    
	  },

  onSubmit: function(e) {
    e.preventDefault()

    // check if form is valid
    var validation = this.refs.form.value().validation
    if (ReactForms.validation.isFailure(validation)) {
      console.log('invalid form')
      return
    }

    
  }
});

module.exports = AddServices;

}());


