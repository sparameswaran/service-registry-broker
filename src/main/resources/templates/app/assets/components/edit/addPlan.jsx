/** @jsx React.DOM */
var React = require('react');
var Router = require('react-router');
var JsonEditor = require('react-json');
var _ = 'lodash';
var $ = require('jquery');

var RegistryServices = require('../shared/registryServices.jsx');
var sampleData = require('./samplePlan.json');


(function () {
  'use strict';
  
 
		var Bullets = React.createClass({
		    addInputField: function(e) {
		        e.preventDefault();
		
		        var inputs = this.state.inputs;
		        inputs.push({name: null});
		        this.setState({inputs : inputs});
		    },
		    removeInputField: function(index) {
		        var inputs = this.state.inputs;
		        inputs.splice(index, 1);
		        this.setState({inputs : inputs});
		    },
		    handleSubmit: function (e) {
		        e.preventDefault();
		        // What do I do here?
		    },
		    getInitialState: function() {
		        console.log("Inside initial state for bullets..");
		        return {inputs : [ '']};
		    },
		    render: function (){
		    
		        console.log("Inside bullets rendering...");
		        var inputs = this.state.inputs;
		        return (
		            // Setting up the form
		            // Blah blah
		           <div className="form-group">
		               <div className="col-sm-6">
		                   {inputs.map(function (input, index) {
		                       var ref = "input_" + index;
		                       return (
		                           <div className="input-group" key={index}>
		                                <input type="text" placeholder="Enter tag" value={input.name} ref={ref} input-field={ref} />
		                                <span className="input-group-addon" onClick={this.removeInputField.bind(this, index)} id={ref} ><i className="fa fa-times"></i></span>
		                           </div>
		                       )
		                   }.bind(this))}
		                    <button className="btn btn-success btn-block" onClick={this.addInputField}>Add Input</button>
		               </div>
		           </div>
		        );
		    }
		});
      
 
  var AddPlan = React.createClass({
	
	getInitialState: function() {
	    var jsonPayload = {};
	    var space = '\t';
	    
	    console.log("sample data contains: ", sampleData); 
	    sampleData.bullets = [ ''];
	    return { value: sampleData, bulletInputs : [''] , credsInputs : [ {url: '' }]}; 
	  },
	  
	  handleChange: function(event) {
	    this.setState({value: event.target.value});
	    
	    console.log("Inside handleChange with event: ", event);
	    
	  },
	  
    addBulletsInputField: function(e) {
        e.preventDefault();

        var inputs = this.state.bulletInputs;
        inputs.push({name: null});
        this.setState({bulletInputs : inputs});
        console.log("Got on adding input field, with state: ", this.state , " and bulletInputs : ", inputs);
    },
    
    removeBulletsInputField: function(index) {
        var inputs = this.state.bulletInputs;
        inputs.splice(index, 1);
        this.setState({bulletInputs : inputs});
    },
		
    addCredsInputField: function(e) {
        e.preventDefault();

        var inputs = this.state.credsInputs;
        inputs.push({name: null, value: null});
        this.setState({credsInputs : inputs});
        console.log("Got on adding input field, with state: ", this.state , " and credsInputs : ", inputs);
    },
    
    removeCredsInputField: function(index) {
        var inputs = this.state.credsInputs;
        inputs.splice(index, 1);
        this.setState({credsInputs : inputs});
    },
		
		    
	  
	  handleSubmit: function(event) {
	    
	    console.log("Got handleSubmitRow! with state: ", this.state);
	    
	   
	    // Get values via this.refs
	    var data = {
	      name     : this.refs.name.getDOMNode().value,
	      description : this.refs.description.getDOMNode().value,
	      free    : this.refs.free.getDOMNode().checked,
	    }
	    
	    var bulletTags = [];
	    for (var ref in this.refs) {
	        //console.log("Ref is ", ref , " and its object is : ", this.refs[ref]);	        
	        //console.log("DOM Node contained there is ", this.refs[ref].getDOMNode());
	        
	        if (ref.lastIndexOf("input_", 0) === 0) {
	          bulletTags.push (this.refs[ref].getDOMNode().value);
	        }
	    }
	    
        data['metadata'] = { bullets : bulletTags };        
	    
	    data['credentials'] = [];
	    
	    var keyArr = new Array(20);
	    var valArr = new Array(20);
	    var count = 0;
	    for (var ref in this.refs) {
	        console.log("Ref is ", ref , " and its object is : ", this.refs[ref]);	        
	        console.log("DOM Node contained there is ", this.refs[ref].getDOMNode());
	        
	        if (ref.lastIndexOf("creds_", 0) === 0) {
	          var index = ref.substr(ref.lastIndexOf('_') +  1);
	          
	          if (ref.lastIndexOf("creds_name", 0) === 0) {
	           keyArr[index] = this.refs[ref].getDOMNode().value;
	           count += 1;
	          } else {
	            valArr[index] = this.refs[ref].getDOMNode().value;
	          }	          
	        }	        
	    }
	    var credentialArr = {};
	    for(var i=0;i<count;i++) {
		  credentialArr[ keyArr[i] ] = valArr[i]; 
		}
		data['credentials'] = credentialArr;
		
	    console.log("Got handleSubmit data: ", JSON.stringify(data));
	    
	    RegistryServices.addPlanToService(this.props.params['serviceId'], JSON.stringify(data));
	       
	    var router = Router.create({});
	    router.transitionTo('/');	    
	  },
	  
     updateJsonFormValue: function( nextValue ) {
     
        this.setState( {value: nextValue} );
        
        console.log("Updated data onJsonFormUpdate: ", nextValue);
     },
     
     updateValue: function( nextValue ) {
     
        this.setState( {rawValue: nextValue.target.value} );
        
        console.log("Updated data onUpdate: ", nextValue.target.value);
     },

    componentDidMount: function() {
      console.log("Inside componentDidMount in AddPlan page, this params : ", this.props.params);
      console.log("Inside componentDidMount in AddPlan page, this.props contains: " , this.props, " and state contains: " , this.state);
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
	          
        var bulletInputs = this.state.bulletInputs;
        var credsInputs = this.state.credsInputs;
        
      return (

        <div className="page-title bg-neutral-11 pvxl">
          <div className="container">          
          
            <div className="media">
              <div className="media-body media-middle">
                <div className="media">
                  <div className="media-body media-middle">
                    <p className="type-sm em-alt label-alt mvn">
                      <span className="em-default type-accent-4">Services</span>                      
                    </p>
                    <p className='h2 type-dark-1 mvn em-low'>Add new Service Plan </p>
                   </div>                    
                  </div>
                </div>
              </div>
              
              <div className="media-body media-middle txt-l">
               
          <p className="type-dark-4 mvn type-sm mtl">
            <span className="mrl em-alt">
            </span>
			</p>	
 			        <form id="outerForm" class="styleguide-form" role="form">
			          
			            <label for="name">Name</label>
			            <input className="form-control"  type="text" ref="name" placeholder="Enter Plan Name" />
			            <label for="description">Description</label>
			            <input className="form-control"  type="text" ref="description" placeholder="Enter Plan Description" />
			            <label>
			                <div class="col-xs-1" >
			            	<input className="form-control"  type="checkbox" ref="free" /> 
			            	</div>
			            	Free Plan 
			            </label>
			            <br/>
			            <label for="tags">
				            Tags
				           <div className="form-group">
				               <div className="col-sm-6">
				                   {bulletInputs.map(function (input, index) {
				                       var ref = "input_" + index;
				                       return (
				                           <div className="input-group" key={index}>
				                                <input type="text" placeholder="Enter tag" value={input.name} ref={ref} aria-describedby={ref} />
				                                <span className="input-group-addon" onClick={this.removeBulletsInputField.bind(this, index)} id={ref} ><i className="fa fa-times"></i></span>
				                           </div>
				                       )
				                   }.bind(this))}
				                    <button className="btn btn-success btn-block" onClick={this.addBulletsInputField}>Add Input</button>
				               </div>
				           </div>
						</label>
			            <label for="creds">
				            Credentials
				           <div className="form-group">
				               <div className="col-sm-6">
				                   {credsInputs.map(function (input, index) {
				                       var nameref = "creds_name_" + index;
				                       var valueref = "creds_value_" + index;
				                       return (
				                           <div className="input-group" key={index}>
				                                <input type="text" placeholder="Enter name" value={input.name} ref={nameref} aria-describedby={nameref}  />
				                                <input type="text" placeholder="Enter value" value={input.value} ref={valueref} aria-describedby={valueref} />
				                                <span className="input-group-addon" onClick={this.removeCredsInputField.bind(this, index)} id={nameref} ><i className="fa fa-times"></i></span>
				                           </div>
				                       )
				                   }.bind(this))}
				                    <button className="btn btn-success btn-block" onClick={this.addCredsInputField}>Add Input</button>
				               </div>
				           </div>
						</label>

			        </form>
					<br/><br/>
							     
			        <div align="left">	
			        <button id="submit" class="btn btn-default" onClick={this.handleSubmit} >Submit</button> 
			        </div>
		            
		          </div>      
		        </div>
		        </div>
	  );
	 
  }

} );

module.exports = AddPlan;

}());



